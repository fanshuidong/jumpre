package org.gatlin.jumpre.websocket.realm;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.Session;

import org.gatlin.jumpre.websocket.WebScoketJumpre;
import org.gatlin.jumpre.websocket.msg.Message;

import com.google.gson.Gson;

public class Player {
	
	private static Gson gson = new Gson();

	public Player(Session session, String userId) {
		this.session = session;
		this.userId = userId;
		pushTime = System.currentTimeMillis() / 1000;
		startPing();
	}
	
	public void init() {//一般用于重连
		pushTime = System.currentTimeMillis() / 1000;
	}
	
	private Session session;
	private String userId;
	private long pushTime;// 最近一次接收到包的时间
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private int dif = 0;// 每次执行定时任务时间与pushTime的差值
	private int scope = 0;
	private boolean isMatch;
	private boolean isReady;
	private String matchUserId;// 匹配对手
	private Room room;

	public Player getBySession(Session session) {
		return session.equals(this.session) ? this : null;
	}

	public String getUserIdBySession(Session session) {
		return session.equals(this.session) ? this.userId : null;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getPushTime() {
		return pushTime;
	}

	public void setPushTime(long pushTime) {
		this.pushTime = pushTime;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public String getMatchUserId() {
		return matchUserId;
	}

	public void setMatchUserId(String matchUserId) {
		this.matchUserId = matchUserId;
	}

	public boolean isMatch() {
		return isMatch;
	}

	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void send(Message message) {
		try {
			this.session.getBasicRemote().sendText(gson.toJson(message));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//获取对手
	public Player getRival() {
		return matchUserId == null ? null :WebScoketJumpre.players.get(this.matchUserId);
	}

	public void close() {
		try {
			WebScoketJumpre.players.remove(userId);
			this.session.close();
			this.service.shutdown();
			this.room = null;
			this.matchUserId=null;
			this.isMatch = false;
			this.isReady = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closePing() {
		this.service.shutdown();
	}
	
	public void startPing() {
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				dif = (int) (System.currentTimeMillis() / 1000 - pushTime);
				if ((dif) > 11) {
//					WebScoketJumpre.quit(userId);
//					close();
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

}
