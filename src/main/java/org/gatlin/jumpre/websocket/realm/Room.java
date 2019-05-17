package org.gatlin.jumpre.websocket.realm;


import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.gatlin.jumpre.websocket.WebScoketJumpre;
import org.gatlin.jumpre.websocket.menu.MsgState;
import org.gatlin.jumpre.websocket.menu.RoomState;
import org.gatlin.jumpre.websocket.msg.FinishMsg;
import org.gatlin.jumpre.websocket.msg.MatchMsg;
import org.gatlin.jumpre.websocket.msg.ScopeMsg;
import org.gatlin.jumpre.websocket.msg.StartMsg;

import com.google.gson.Gson;

public class Room {

	private static Gson gson = new Gson();
	
	private Player player1;
	private Player player2;
	private RoomState state;
	
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
		this.state = RoomState.matching;
		Executors.newSingleThreadScheduledExecutor().schedule(()->{
			//60秒后游戏结束
			finish();
		}, 60, TimeUnit.SECONDS);
		
	}
	
	public void action(MsgState state,String message,Player player) {
		switch (state) {
		case scope:// 比赛时时数据
			if(this.state == RoomState.matching) {
				ScopeMsg recevice = gson.fromJson(message, ScopeMsg.class);
				player.setScope(player.getScope() + recevice.getScope());
				recevice.setScope(player.getScope());
				player.getRival().send(recevice);
			}
			break;
		case ready:// 玩家就绪
			if(player.isMatch()) {
				player.setReady(true);
				if (player.getRival().isReady()) {// 如果对手已经就绪
					player.send(new StartMsg());
					player.getRival().send(new StartMsg());
				}
			}
			break;
		case finish:
			finish_(player);
			break;
		default:
			break;
		}
	}
	
	//比赛正常
	private void finish() {
		if(this.state == RoomState.matching) {
			this.state = RoomState.finish;
			player1.send(new FinishMsg(player1.getScope(),player1.getScope()>player2.getScope()?1:player1.getScope() < player2.getScope()?0:2));
			player2.send(new FinishMsg(player2.getScope(),player2.getScope()>player1.getScope()?1:player2.getScope() < player1.getScope()?0:2));
			player1.close();
			player2.close();
			//调用对方奖励结算接口
		}
	}
	
	/**
	 * 比赛异常结束 有玩家退出
	 * @param player 退出的玩家
	 */
	private void finish_(Player player) {
		if(this.state == RoomState.matching) {
			this.state = RoomState.finish;
			player.send(new FinishMsg(player.getScope(),0));
			player.getRival().send(new FinishMsg(player.getRival().getScope(),1));
			player1.close();
			player2.close();
			//调用对方奖励结算接口
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

	public RoomState getState() {
		return state;
	}

	public void setState(RoomState state) {
		this.state = state;
	}
	
	
}
