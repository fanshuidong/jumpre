package org.gatlin.jumpre.websocket.menu;

public enum MsgState {

	ping(1, "心跳"), 
	scope(2, "分数"), 
	match(3, "匹配成功的两玩家"), 
	ready(4, "玩家就绪"), 
	start(5, "比赛开始"), 
	finish(6, "服务端发起比赛结束,客户端发起连接断开"),
	recon(7, "重连进来之后发送比赛双方的数据"),
	quit(8, "玩家主动退出,游戏结束"),
	clear(10, "清空连胜"),
	cancel(11, "比赛开始前有玩家退出");

	private int mark;
	private String desc;

	private MsgState(int mark, String desc) {
		this.mark = mark;
		this.desc = desc;
	}
	
	public static MsgState match(int mark) {
		for(MsgState state:MsgState.values()) {
			if(state.getMark() == mark) {
				return state;
			}
		}
		return null;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
