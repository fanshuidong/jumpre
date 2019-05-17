package org.gatlin.jumpre.http.response;

import org.gatlin.jumpre.http.HttpResponse;

public class BaseResponse implements HttpResponse {

	private static final long serialVersionUID = -8036207201605645667L;

	private String code;
	private String desc;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String desc() {
		return desc;
	}

	@Override
	public void verify() {

	}

}
