package org.gatlin.jumpre.websocket.msg;

public class Scope {
	private String userId;
	private int scope;

	public Scope() {
		
	}
	
	public Scope(String userId,int scope) {
		this.scope  = scope;
		this.userId  = userId;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}
}
