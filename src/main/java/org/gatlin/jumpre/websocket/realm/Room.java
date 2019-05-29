package org.gatlin.jumpre.websocket.realm;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gatlin.jumpre.JumperConfig;
import org.gatlin.jumpre.http.request.GameEndRequest;
import org.gatlin.jumpre.http.request.GameStartRequest;
import org.gatlin.jumpre.util.DateUtil;
import org.gatlin.jumpre.util.ExcutorUtil;
import org.gatlin.jumpre.util.KeyUtil;
import org.gatlin.jumpre.websocket.WebScoketJumpre;
import org.gatlin.jumpre.websocket.menu.LoseReason;
import org.gatlin.jumpre.websocket.menu.MsgState;
import org.gatlin.jumpre.websocket.menu.RoomState;
import org.gatlin.jumpre.websocket.msg.FinishMsg;
import org.gatlin.jumpre.websocket.msg.MatchMsg;
import org.gatlin.jumpre.websocket.msg.ScopeMsg;
import org.gatlin.jumpre.websocket.msg.StartMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

public class Room {
	
	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(Room.class);
	private Player player1;
	private Player player2;
	private ScheduledFuture<?> endTask;
	private RoomState state;
	private LoseReason loseReason;//失败原因
	private String startTime;//开始时间
	private String endTime;//结束时间
	private String gameId = "";

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
		this.state = RoomState.ready;
	}

	public synchronized void action(MsgState state, String message, Player player) {
		switch (state) {
		case scope:// 比赛时时数据
			if (this.state == RoomState.run) {
				ScopeMsg recevice = gson.fromJson(message, ScopeMsg.class);
				player.setScope(player.getScope() + recevice.getScope());
				recevice.setScope(player.getScope());
				if (player.getRival() != null)
					player.getRival().send(recevice);
			}
			break;
		case ready:// 玩家就绪
			if (player.isMatch() && this.state == RoomState.ready) {
				player.setReady(true);
				if (player.getRival().isReady()) {// 如果对手已经就绪
					player.send(new StartMsg());
					player.getRival().send(new StartMsg());
					this.state = RoomState.run;
					this.startTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
					this.gameId = KeyUtil.uuid();
					endTask = ExcutorUtil.excuter.schedule(() -> {
						// 60秒后游戏结束
						finish();
					}, 60, TimeUnit.SECONDS);
					//调用比赛开始接口
					Room.httpStart(gameId, player1.getUserId(), player2.getUserId(), JumperConfig.appType(), startTime);
				}
			}
			break;
		case finish:
			player.close();
			break;
		case quit:
			finish_(player,LoseReason.quit);
			break;
		default:
			break;
		}
	}

	// 比赛时间到服务器发起正常结束
	private synchronized void finish() {
		if (state == RoomState.run) {
			state = RoomState.finish;
			if (player1.getScope() > player2.getScope()) {
				WebScoketJumpre.addSuc(player1.getUserId());// 添加连胜
				WebScoketJumpre.succession.remove(player2.getUserId());
			} else if (player1.getScope() < player2.getScope()) {
				WebScoketJumpre.addSuc(player2.getUserId());// 添加连胜
				WebScoketJumpre.succession.remove(player1.getUserId());
			} else {
				WebScoketJumpre.succession.remove(player1.getUserId());
				WebScoketJumpre.succession.remove(player2.getUserId());
			}
			player1.send(new FinishMsg(player1.getScope(),
					player1.getScope() > player2.getScope() ? 1 : player1.getScope() < player2.getScope() ? 0 : 2,
					WebScoketJumpre.getSuc(player1.getUserId())));
			player2.send(new FinishMsg(player2.getScope(),
					player2.getScope() > player1.getScope() ? 1 : player2.getScope() < player1.getScope() ? 0 : 2,
					WebScoketJumpre.getSuc(player2.getUserId())));
			this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS);
			this.loseReason = LoseReason.normal;
			logger.info("{} 玩家 {} 与  {} 比赛结束,失败原因：{}",endTime,player1.getUserId(),player2.getUserId(),loseReason.desc());
			// 调用对方奖励结算接口
			Room.httpEnd(gameId, player1.getUserId(),player1.getScope(), player2.getUserId(),player2.getScope(),JumperConfig.appType(), endTime,loseReason.mark());
		}
	}

	/**
	 * 比赛异常结束 有玩家退出
	 * 
	 * @param player
	 *            退出的玩家
	 */
	public synchronized void finish_(Player player,LoseReason reason) {
		if (state == RoomState.run) {
			endTask.cancel(false);
			state = RoomState.finish;
			WebScoketJumpre.addSuc(player.getRival().getUserId());// 添加连胜
			WebScoketJumpre.succession.remove(player.getUserId());
			player.send(new FinishMsg(player.getScope(), 0));
			player.getRival().send(new FinishMsg(player.getRival().getScope(), 1,
					WebScoketJumpre.getSuc(player.getRival().getUserId())));
			player.close();
			this.endTime = DateUtil.getDate(DateUtil.YYYY_MM_DD_HH_MM_SS_SSS);
			this.loseReason = reason;
			logger.info("{} 玩家 {} 主动退出比赛结束,失败原因：{}",endTime,player.getUserId(),loseReason.desc());
			// 调用对方奖励结算接口
			Room.httpEnd(gameId, player1.getUserId(),player1.getScope(), player2.getUserId(),player2.getScope(), JumperConfig.appType(), endTime,loseReason.mark());
		}else {
			player.close();
		}
	}
	
	public static void httpStart(String gameId, String userIdA, String userIdB, String appType, String startTime) {
		GameStartRequest.Builder builder = new GameStartRequest.Builder(gameId, userIdA,userIdB, appType, startTime);
		builder.build().sync_();
	}
	
	public static void httpEnd(String gameId, String userIdA,Integer scopeA, String userIdB,Integer scopeB, String appType, String endTime,int loseReason) {
		GameEndRequest.Builder builder = new GameEndRequest.Builder(gameId, userIdA,scopeA,userIdB,scopeB, appType, endTime,loseReason);
		builder.build().sync_();
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

	public RoomState getState() {
		return state;
	}

	public void setState(RoomState state) {
		this.state = state;
	}

}
