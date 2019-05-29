package org.gatlin.jumpre.http.request;

import java.util.Map;
import java.util.TreeMap;

import org.gatlin.jumpre.JumperConfig;
import org.gatlin.jumpre.http.response.BaseResponse;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.menu.ContentType;

import okhttp3.Response;

public class GameEndRequest extends BaseRequest<BaseResponse, GameEndRequest> {

	public GameEndRequest(Map<String, String> params) {
		super(JumperConfig.gameEndUrl(), ContentType.APPLICATION_JSON_UTF_8);
		this.params = params;
	}
	
	@Override
	protected BaseResponse response(Response response) {
		System.out.println(response.message());
		return null;
	}

	public static class Builder extends BaseRequest.Builder<GameEndRequest, Builder> {

		private static final long serialVersionUID = 3769116924795413764L;

		public Builder(String gameId, String userIdA, int scoreA,String userIdB,int scoreB,String appType, String endTime,int loseReason) {
			this.gameId = gameId;
			this.userIdA = userIdA;
			this.scoreA = scoreA;
			this.userIdB = userIdB;
			this.scoreB = scoreB;
			this.appType = appType;
			this.endTime = endTime;
			this.loseReason = loseReason;
		}

		protected String gameId;
		protected String userIdA;
		protected int scoreA;
		protected String userIdB;
		protected int scoreB;
		protected String appType;
		protected String endTime;
		protected int loseReason;

		@Override
		public GameEndRequest build() {
			Map<String, String> map = SerializeUtil.JSON.beanToMap(SerializeUtil.GSON_ANNO, this);
			return new GameEndRequest(new TreeMap<String, String>(map));
		}

	}

}
