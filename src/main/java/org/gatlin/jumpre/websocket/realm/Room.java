package org.gatlin.jumpre.websocket.realm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.websocket.Session;

import org.gatlin.jumpre.http.request.GameEndRequest;
import org.gatlin.jumpre.http.request.GameStartRequest;
import org.gatlin.jumpre.util.DateUtil;
import org.gatlin.jumpre.util.ExcutorUtil;
import org.gatlin.jumpre.util.KeyUtil;
import org.gatlin.jumpre.websocket.WebScoketJumpre;
import org.gatlin.jumpre.websocket.menu.LoseReason;
import org.gatlin.jumpre.websocket.menu.MsgState;
import org.gatlin.jumpre.websocket.menu.RoomState;
import org.gatlin.jumpre.websocket.msg.CancelMsg;
import org.gatlin.jumpre.websocket.msg.FinishMsg;
import org.gatlin.jumpre.websocket.msg.MatchMsg;
import org.gatlin.jumpre.websocket.msg.ResultMsg;
import org.gatlin.jumpre.websocket.msg.Scope;
import org.gatlin.jumpre.websocket.msg.ScopeMsg;
import org.gatlin.jumpre.websocket.msg.StartMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class Room {

	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(Room.class);
	private Player player1;
	private Player player2;
	private ScheduledFuture<?> endTask;
	// private RoomState state;
	private AtomicReference<RoomState> roomState = new AtomicReference<RoomState>(null);//房间状态
	private AtomicReference<Boolean> isCalculate = new AtomicReference<Boolean>(false);//是否再计算得分中
	private LoseReason loseReason;// 失败原因
	private String startTime;// 开始时间
	private String endTime;// 结束时间
	private String gameId = "";
	private String winner = "1000";// 1000表示平局;

	public Room(String player1Id, String player2Id) {
		this.player1 = WebScoketJumpre.players.get(player1Id);
		this.player2 = WebScoketJumpre.players.get(player2Id);
	}

	public void start() {
		player1.setMatch(true);
		player2.setMatch(true);
		player1.setMatchUserId(player2.getUserId());
		player2.setMatchUserId(player1.getUserId());
		player1.send(new MatchMsg(player1.getMatchUserId()));
		player2.send(new MatchMsg(player2.getMatchUserId()));
		player1.setRoom(this);
		player2.setRoom(this);
		// this.state = RoomState.ready;
		roomState.set(RoomState.ready);
		logger.info("玩家 {} 与 {} 成功匹配",player1.getUserId(),player2.getUserId());
	}

	public void action(MsgState state, String message, Player player) {
		switch (state) {
		case scope:// 比赛时时数据
			try {
				if (RoomState.run == roomState.get()) {
					isCalculate.compareAndSet(false, true);
					ScopeMsg recevice = gson.fromJson(message, ScopeMsg.class);
					player.setScope(player.getScope() + recevice.getScope());
					recevice.setScope(player.getScope());
					if (player.getRival() != null)
						player.getRival().send(recevice);
				}
			} catch (Exception e) {
				logger.info(e.getMessage());
			} finally {
				isCalculate.set(false);
			}
			break;
		case ready:// 玩家就绪
			if (player.isMatch() && RoomState.ready == roomState.get()) {
				player.setReady(true);
				if (player.getRival().isReady()) {// 如果对手已经就绪
					if (roomState.compareAndSet(RoomState.ready, RoomState.run)) {
						player.send(new StartMsg());
						player.getRival().send(new StartMsg());
						// this.state = RoomState.run;
						this.startTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
						this.gameId = KeyUtil.uuid();
						endTask = ExcutorUtil.excuter.schedule(() -> {
							// 60秒后游戏结束
							finish();
						}, 60, TimeUnit.SECONDS);
						// 调用比赛开始接口
						Room.httpStart(gameId, player1.getUserId(), player2.getUserId(), startTime);
					}
				}
			}
			break;
		case finish:
			player.close();
			break;
		case quit:
			finish_(player, LoseReason.quit);
			break;
		default:
			break;
		}
	}

	// 比赛时间到服务器发起正常结束
	private void finish() {
		if (roomState.compareAndSet(RoomState.run, RoomState.finish)) {
			while(!isCalculate.get()) {
				int scope1 = player1.getScope();
				int scope2 = player2.getScope();
				String userId1 = player1.getUserId();
				String userId2 = player2.getUserId();
				if (scope1 > scope2) {
					WebScoketJumpre.addSuc(userId1);// 添加连胜
					WebScoketJumpre.succession.remove(userId2);
					winner = userId1;
				} else if (scope1 < scope2) {
					WebScoketJumpre.addSuc(userId2);// 添加连胜
					WebScoketJumpre.succession.remove(userId1);
					winner = userId2;
				} else {
					WebScoketJumpre.succession.remove(userId1);
					WebScoketJumpre.succession.remove(userId2);
				}
				player1.send(new FinishMsg(scope1,scope1 > scope2 ? 1 : scope1 < scope2 ? 0 : 2,WebScoketJumpre.getSuc(userId1)));
				player2.send(new FinishMsg(scope2,scope2 > scope1 ? 1 : scope2 < scope1 ? 0 : 2,WebScoketJumpre.getSuc(userId2)));
				this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS);
				this.loseReason = LoseReason.normal;
				logger.info("{} 玩家 {} 与  {} 比赛结束,失败原因：{}", endTime, userId1, userId2,loseReason.desc());
				// 调用对方奖励结算接口
				Room.httpEnd(gameId, userId1, scope1, userId2, scope2,endTime, loseReason.mark(), winner);
				break;
			}
		}
		// if (state == RoomState.run) {
		// state = RoomState.finish;
		// if (player1.getScope() > player2.getScope()) {
		// WebScoketJumpre.addSuc(player1.getUserId());// 添加连胜
		// WebScoketJumpre.succession.remove(player2.getUserId());
		// winner = player1.getUserId();
		// } else if (player1.getScope() < player2.getScope()) {
		// WebScoketJumpre.addSuc(player2.getUserId());// 添加连胜
		// WebScoketJumpre.succession.remove(player1.getUserId());
		// winner = player2.getUserId();
		// } else {
		// WebScoketJumpre.succession.remove(player1.getUserId());
		// WebScoketJumpre.succession.remove(player2.getUserId());
		// }
		// player1.send(new FinishMsg(player1.getScope(),
		// player1.getScope() > player2.getScope() ? 1 : player1.getScope() <
		// player2.getScope() ? 0 : 2,
		// WebScoketJumpre.getSuc(player1.getUserId())));
		// player2.send(new FinishMsg(player2.getScope(),
		// player2.getScope() > player1.getScope() ? 1 : player2.getScope() <
		// player1.getScope() ? 0 : 2,
		// WebScoketJumpre.getSuc(player2.getUserId())));
		// this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS);
		// this.loseReason = LoseReason.normal;
		// logger.info("{} 玩家 {} 与 {} 比赛结束,失败原因：{}", endTime, player1.getUserId(),
		// player2.getUserId(),
		// loseReason.desc());
		// // 调用对方奖励结算接口
		// Room.httpEnd(gameId, player1.getUserId(), player1.getScope(),
		// player2.getUserId(), player2.getScope(),
		// endTime, loseReason.mark(), winner);
		// }
	}

	/**
	 * 比赛异常结束 有玩家退出
	 * 
	 * @param player
	 *            退出的玩家
	 */
	public void finish_(Player player, LoseReason reason) {
		if (roomState.compareAndSet(RoomState.run, RoomState.finish)) {
			endTask.cancel(false);
			Player winner = player.getRival();//对手获胜
			WebScoketJumpre.addSuc(winner.getUserId());// 添加连胜
			WebScoketJumpre.succession.remove(player.getUserId());
			player.send(new FinishMsg(player.getScope(), 0));
			winner.send(new FinishMsg(winner.getScope(), 1,
					WebScoketJumpre.getSuc(winner.getUserId())));
			this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
			this.loseReason = reason;
			logger.info("{} 玩家 {} 主动退出比赛结束,失败原因：{}", endTime, player.getUserId(), loseReason.desc());
			player.close();
			// 调用对方奖励结算接口
			Room.httpEnd(gameId, player1.getUserId(), player1.getScope(), player2.getUserId(), player2.getScope(),
					endTime, loseReason.mark(), winner.getUserId());
		} else if (roomState.compareAndSet(RoomState.ready, null)) {
			if (player.getRival() != null)
				player.getRival().send(new CancelMsg());
			player.close();
		} else {
			player.close();
		}
		// if (state == RoomState.run) {
		// endTask.cancel(false);
		// state = RoomState.finish;
		// WebScoketJumpre.addSuc(player.getRival().getUserId());// 添加连胜
		// WebScoketJumpre.succession.remove(player.getUserId());
		// player.send(new FinishMsg(player.getScope(), 0));
		// player.getRival().send(new FinishMsg(player.getRival().getScope(), 1,
		// WebScoketJumpre.getSuc(player.getRival().getUserId())));
		// this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
		// this.loseReason = reason;
		// logger.info("{} 玩家 {} 主动退出比赛结束,失败原因：{}", endTime, player.getUserId(),
		// loseReason.desc());
		// // 调用对方奖励结算接口
		// Room.httpEnd(gameId, player1.getUserId(), player1.getScope(),
		// player2.getUserId(), player2.getScope(),
		// endTime, loseReason.mark(), player.getMatchUserId());
		// player.close();
		// } else {
		// player.close();
		// }
	}

	public static void httpStart(String gameId, String userIdA, String userIdB, String startTime) {
		GameStartRequest.Builder builder = new GameStartRequest.Builder(gameId, userIdA, userIdB, startTime);
		builder.build().sync_();
	}

	public static void httpEnd(String gameId, String userIdA, Integer scopeA, String userIdB, Integer scopeB,
			String endTime, int loseReason, String winner) {
		GameEndRequest.Builder builder = new GameEndRequest.Builder(gameId, userIdA, scopeA, userIdB, scopeB, endTime,
				loseReason, winner);
		builder.build().sync_();
	}

	// 房间重连
	public void reConnect(Player player) {
		if (roomState.get() == RoomState.ready) {
			player.quitRoom();
			GameRunner.INSTANCE.push(player.getUserId());
		}
		if (roomState.get() == RoomState.run) {
			Scope scope1 = new Scope(player.getUserId(), player.getScope());
			Scope scope2 = new Scope(player.getMatchUserId(), player.getRival().getScope());
			List<Scope> list = new ArrayList<>();
			list.add(scope1);
			list.add(scope2);
			player.send(new ResultMsg(list));
		} else if (roomState.get() == RoomState.finish) {
			player.send(new FinishMsg(player.getScope(), player.getScope() > player.getRival().getScope() ? 1
					: player.getScope() < player.getRival().getScope() ? 0 : 2));
		}

	}
	
	/**
	 * 匹配成功且没开始前有玩家断线 解散房间
	 * @param player 断线玩家
	 */
	public synchronized void dissolve(Player player) {
		if(roomState.get() == RoomState.ready) {
			try {
				Player to = player.getRival();
				if(to != null) {
					to.quitRoom();
					if(WebScoketJumpre.players.containsKey(to.getUserId())) {
						GameRunner.INSTANCE.push(to.getUserId());
						to.send(new CancelMsg());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				player.quitRoom();
			}
		}
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

}
