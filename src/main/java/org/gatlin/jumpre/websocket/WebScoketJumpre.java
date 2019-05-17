package org.gatlin.jumpre.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.gatlin.jumpre.websocket.menu.MsgState;
import org.gatlin.jumpre.websocket.menu.RoomState;
import org.gatlin.jumpre.websocket.msg.FinishMsg;
import org.gatlin.jumpre.websocket.msg.Message;
import org.gatlin.jumpre.websocket.msg.PingMsg;
import org.gatlin.jumpre.websocket.msg.ResultMsg;
import org.gatlin.jumpre.websocket.msg.Scope;
import org.gatlin.jumpre.websocket.realm.GameRunner;
import org.gatlin.jumpre.websocket.realm.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;


//@ServerEndpoint(value = "/webSocket/jumpre/{userId}", configurator = GetHttpSessionConfigurator.class)
@ServerEndpoint(value = "/webSocket/jumpre/{userId}")
@Component
public class WebScoketJumpre {

	private static Logger logger = LoggerFactory.getLogger(WebScoketJumpre.class);
	private static Gson gson = new Gson();
	
	private static AtomicInteger onlineCount = new AtomicInteger(0);

	public static ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();

	// 与某个客户端的连接会话，通过它实现定向推送(只推送给某个用户)
	private Player player;
	
	static {
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(()->GameRunner.INSTANCE.match(), 0, 2, TimeUnit.SECONDS);
	}

	/**
	 * 建立连接成功调用的方法
	 * 
	 * @param session
	 */
	@OnOpen
	public void onOpen(@PathParam("userId") String userId, Session session, EndpointConfig config) {
		if (players.containsKey(userId)) {
			this.player = players.get(userId);
			try {
				if(player.getSession().isOpen())
					player.getSession().close();//重连关闭原先的链接
				this.player.setSession(session);
				player.init();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (player.isMatch() && null != player.getRoom()) {
				// 重连进来 发送双方的比赛数据
				if(player.getRoom().getState() == RoomState.matching) {
					Scope scope1 = new Scope(userId,player.getScope());
					Scope scope2 = new Scope(player.getMatchUserId(),players.get(player.getMatchUserId()).getScope());
					List<Scope> list = new ArrayList<>();
					list.add(scope1);
					list.add(scope2);
					ResultMsg msg = new ResultMsg(list);
					sendMessage(msg,session);
				}else if(player.getRoom().getState() == RoomState.finish) {
					player.send(new FinishMsg(player.getScope(),player.getScope()>player.getRival().getScope()?1:player.getScope() < player.getRival().getScope()?0:2));
				}
				
			}
			logger.info("用户："+userId+"重连成功,当前在线人数为：" + addOnlineCount());
		}else {
			this.player = new Player(session, userId);
			players.put(userId, player); // 添加到map中
			GameRunner.INSTANCE.push(userId);
			logger.info("玩家"+userId+"加入，当前在线人数为：" + addOnlineCount());
		}
	}

	/**
	 * 连接关闭调用的方法
	 * 
	 * @param closeSession
	 */
	@OnClose
	public void onClose(@PathParam("userId") String userId, Session session) {
		logger.info("玩家"+userId+"离开，当前在线人数为：" + subOnlineCount());
	}
	
	public static void quit(String userId) {
		players.remove(userId);
	}

	/**
	 * 收到客户端调用
	 * 
	 * @param message
	 * @param mySession
	 */
	@OnMessage
	public void onMessage(String message, Session session, @PathParam("userId") String userId) throws Exception {
		try {
			JSONObject object = JSONObject.parseObject(message);
			MsgState state = MsgState.match(object.getIntValue("state"));
			if (null == state) {
				logger.info("异常消息：{}", message);
				return;
			}
			switch (state) {
			case ping:// 心跳包
				player.setPushTime(System.currentTimeMillis());
				sendMessage(new PingMsg(),session);
				break;
			case scope:// 比赛时时数据
			case ready:// 玩家就绪
				player.getRoom().action(state, message, player);
				break;
			case finish:// 比赛正常结束 一般由服务端发送比赛结果后 客户端返回 服务端关闭连接
				if(player.isMatch())
					player.getRoom().action(state, message, player);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(@PathParam("userId") String userId, Throwable throwable, Session session) {
		logger.info("Exception : userId = " + userId + " , throwable = " + throwable.toString() + "/"
				+ throwable.getMessage());
		if(player.isMatch()) {//正在比赛的玩家退出游戏 判输 对方赢
			if(players.contains(player.getMatchUserId()))
			sendMessage(new FinishMsg(players.get(player.getMatchUserId()).getScope(),1), players.get(player.getMatchUserId()).getSession());
		}
	}

	public static void sendMessage(Message message,Session session) {
		try {
			session.getBasicRemote().sendText(gson.toJson(message));
		} catch (Exception e) {
			logger.info("发送消息失败 ");
			e.printStackTrace();
		}
	}

	public void sendAllMessage(String message) throws IOException {
		this.player.getSession().getBasicRemote().sendText(message);
	}


	// 获取在线人数
	public static int getOnlineCount() {
		return onlineCount.get();
	}

	// 添加在线人数+1
	public static int addOnlineCount() {
		return onlineCount.incrementAndGet();
	}

	// 减少在线人数-1
	public static int subOnlineCount() {
		return onlineCount.decrementAndGet();
	}

}
