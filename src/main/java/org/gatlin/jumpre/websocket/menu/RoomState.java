package org.gatlin.jumpre.websocket.menu;

public enum RoomState {

	matching(1, "比赛"), 
	finish(2, "结束"); 
	private int mark;
	private String desc;

	private RoomState(int mark, String desc) {
		this.mark = mark;
		this.desc = desc;
	}
	
	public static RoomState match(int mark) {
		for(RoomState state:RoomState.values()) {
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
