package org.gatlin.jumpre.websocket.msg;

import org.gatlin.jumpre.websocket.menu.MsgState;

/**
 * 比赛结果数据
 * 
 * @author fansd
 * @date 2019年5月14日 下午3:26:02
 */

public class FinishMsg extends Message {

	public FinishMsg(int scope,int win) {
		super(MsgState.finish);
		this.scope = scope;
		this.win = win;//1赢0输2平局
	}

	private int scope;
	private int win;// 谁赢了

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int isWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

}
