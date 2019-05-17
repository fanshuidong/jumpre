package org.gatlin.jumpre.http.request;

import java.io.IOException;

import org.gatlin.jumpre.http.HttpPost;
import org.gatlin.jumpre.http.exception.RequestFailException;
import org.gatlin.jumpre.http.response.BaseResponse;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.menu.ContentType;

import okhttp3.Response;


public class BaseRequest <RESPONSE extends BaseResponse, REQUEST extends BaseRequest<RESPONSE, REQUEST>> extends HttpPost<RESPONSE, REQUEST>{
	
	public BaseRequest(String url,ContentType contentType) {
		super(url,contentType);
		this.contentType();
	}
	
	
	@Override
	protected RESPONSE response(Response response) {
		try {
			RESPONSE resp = SerializeUtil.GSON.fromJson(response.body().string(), clazz);
			resp.verify();
			return resp;
		} catch (IOException e) {
			throw new RequestFailException(e);
		}
	}
	
	public static abstract class Builder<REQUEST, BUILDER extends Builder<REQUEST, BUILDER>> implements HttpPost.Body {

		private static final long serialVersionUID = 7491685951697117463L;
		
		public abstract REQUEST build();
	}

}
