package org.gatlin.jumpre.websocket.msg;

import org.gatlin.jumpre.websocket.menu.MsgState;

public class CancelMsg extends Message{

	public CancelMsg() {
		super(MsgState.cancel);
	}

}
