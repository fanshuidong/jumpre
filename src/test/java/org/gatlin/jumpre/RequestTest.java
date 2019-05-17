package org.gatlin.jumpre;

import java.util.Map;
import java.util.TreeMap;

import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.gatlin.jumpre.http.request.BaseRequest;
import org.gatlin.jumpre.http.response.BaseResponse;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.menu.ContentType;

public class RequestTest extends BaseRequest<BaseResponse, RequestTest>{
	
	public RequestTest(Map<String, String> params,String url,ContentType contentType,Body body) {
		super(url,contentType);
		this.params = params;
		this.body = body;
	}
	
	public static class Builder extends BaseRequest.Builder<RequestTest, Builder>{

		private static final long serialVersionUID = 2995830835856789084L;

		@Override
		public RequestTest build() {
			Map<String, String> map = SerializeUtil.JSON.beanToMap(SerializeUtil.GSON_ANNO, this);
			return new RequestTest(new TreeMap<String, String>(map), 
					"http://localhost:8089/weiqianjin/repel/cash/bankCode",
					ContentType.APPLICATION_JSON_UTF_8,this);
		}
		
	}
	public static void main(String[] args) {
		RequestTest.Builder builder = new RequestTest.Builder();
		
		BaseResponse response = builder.build().sync_();
		System.out.println(response.code());
	}
}
