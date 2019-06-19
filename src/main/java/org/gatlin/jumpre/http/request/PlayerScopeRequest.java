package org.gatlin.jumpre.http.request;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.gatlin.jumpre.JumperConfig;
import org.gatlin.jumpre.http.exception.RequestFailException;
import org.gatlin.jumpre.http.response.BaseResponse;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.menu.ContentType;

import okhttp3.Response;

public class PlayerScopeRequest extends BaseRequest<BaseResponse, PlayerScopeRequest> {

	public PlayerScopeRequest(Map<String, String> params,Body body) {
		super(JumperConfig.userScoreQueryUrl(), ContentType.APPLICATION_JSON_UTF_8);
		this.params = params;
		this.body = body;
	}
	
	@Override
	protected BaseResponse response(Response response) {
		try {
			BaseResponse resp = SerializeUtil.GSON.fromJson(response.body().string(), clazz);
			resp.verify();
			return resp;
		} catch (IOException e) {
			throw new RequestFailException(e);
		}
	}

	public static class Builder extends BaseRequest.Builder<PlayerScopeRequest, Builder> {

		private static final long serialVersionUID = 3769116924795413764L;

		public Builder(String userId) {
			this.userId = userId;
		}

		protected String userId;
	

		@Override
		public PlayerScopeRequest build() {
			Map<String, String> map = SerializeUtil.JSON.beanToMap(SerializeUtil.GSON_ANNO, this);
			return new PlayerScopeRequest(new TreeMap<String, String>(map),this);
		}

	}

}
