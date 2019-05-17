package org.gatlin.jumpre.websocket.msg;

import org.gatlin.jumpre.websocket.menu.MsgState;

public class PingMsg extends Message{

	public PingMsg() {
		super(MsgState.ping);
	}

}
