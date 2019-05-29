package org.gatlin.jumpre.http.request;

import java.util.Map;
import java.util.TreeMap;

import org.gatlin.jumpre.JumperConfig;
import org.gatlin.jumpre.http.response.BaseResponse;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.menu.ContentType;

import okhttp3.Response;

public class GameStartRequest extends BaseRequest<BaseResponse, GameStartRequest> {

	public GameStartRequest(Map<String, String> params) {
		super(JumperConfig.gameStartUrl(), ContentType.APPLICATION_JSON_UTF_8);
		this.params = params;
	}
	
	@Override
	protected BaseResponse response(Response response) {
		System.out.println(response.message());
		return null;
	}

	public static class Builder extends BaseRequest.Builder<GameStartRequest, Builder> {

		private static final long serialVersionUID = 6301835650003998037L;

		public Builder(String gameId, String userIdA, String userIdB, String appType, String startTime) {
			this.gameId = gameId;
			this.userIdA = userIdA;
			this.userIdB = userIdB;
			this.appType = appType;
			this.startTime = startTime;
		}

		protected String gameId;
		protected String userIdA;
		protected String userIdB;
		protected String appType;
		protected String startTime;

		@Override
		public GameStartRequest build() {
			Map<String, String> map = SerializeUtil.JSON.beanToMap(SerializeUtil.GSON_ANNO, this);
			return new GameStartRequest(new TreeMap<String, String>(map));
		}

	}

}
