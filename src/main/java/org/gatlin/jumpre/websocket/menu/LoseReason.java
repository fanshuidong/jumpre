package org.gatlin.jumpre.websocket.menu;
/**
 * 比赛失败原因
 * @author fansd
 * @date 2019年5月28日 上午11:37:30
 */
public enum LoseReason {

	normal(1,"正常失败"),
	quit(2,"玩家主动退出游戏"),
	timeOut(3,"连接超时断开"),
	exception(4,"连接异常断开");
	
	private LoseReason(int mark,String desc) {
		this.desc = desc;
		this.mark = mark;
	}
	
	private int mark;
	private String desc;
	
	public int mark() {
		return this.mark;
	}
	
	public String desc() {
		return this.desc;
	}
}
