package org.gatlin.jumpre.websocket.msg;

import org.gatlin.jumpre.websocket.menu.MsgState;

/**
 * 比赛结果数据
 * @author fansd
 * @date 2019年5月14日 下午3:26:02
 */

public class MatchMsg extends Message{

	public MatchMsg(String userId) {
		super(MsgState.match);
		this.userId = userId;
	}
	
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	

}
