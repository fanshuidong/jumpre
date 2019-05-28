package org.gatlin.jumpre.http.response;

import org.gatlin.jumpre.http.HttpResponse;

public class BaseResponse implements HttpResponse {

	private static final long serialVersionUID = -8036207201605645667L;

	private String returnFlag;
	private String returnText;

	public String getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}

	public String getReturnText() {
		return returnText;
	}

	public void setReturnText(String returnText) {
		this.returnText = returnText;
	}

	@Override
	public String code() {
		return returnFlag;
	}

	@Override
	public String desc() {
		return returnText;
	}

	@Override
	public void verify() {

	}

}
