package org.gatlin.jumpre.http.exception;

public class RequestFailException extends RuntimeException{

	private static final long serialVersionUID = 9215097846462644009L;
	
	private int code;
	private String desc;
	
	public RequestFailException(Throwable cause) {
		super(cause);
	}
	
	public RequestFailException(int code, String desc) {
		super(desc);
		this.code = code;
		this.desc = desc;
	}
	
	public int code() {
		return code;
	}
	
	public String desc() {
		return desc;
	}

}
