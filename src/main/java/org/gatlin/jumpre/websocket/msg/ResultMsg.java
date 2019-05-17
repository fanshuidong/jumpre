package org.gatlin.jumpre.websocket.msg;

import java.util.List;

import org.gatlin.jumpre.websocket.menu.MsgState;

/**
 * 比赛双方数据，主要针对重连
 * 
 * @author fansd
 * @date 2019年5月14日 下午3:26:02
 */

public class ResultMsg extends Message {

	public ResultMsg(List<Scope> scopes) {
		super(MsgState.recon);
		this.scopes = scopes;
	}

	private List<Scope> scopes;

	public List<Scope> getScopes() {
		return scopes;
	}

	public void setScopes(List<Scope> scopes) {
		this.scopes = scopes;
	}

}
