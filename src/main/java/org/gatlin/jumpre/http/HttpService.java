package org.gatlin.jumpre.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.gatlin.jumpre.http.exception.RequestFailException;
import org.springframework.stereotype.Component;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class HttpService {
	
	private OkHttpClient client;
	
	@PostConstruct
	public void init() {
		this.client = new OkHttpClient.Builder()
				.readTimeout(5, TimeUnit.SECONDS)
				.writeTimeout(5, TimeUnit.SECONDS)
				.connectTimeout(5, TimeUnit.SECONDS).build();
	}
	
	public void requestAsync(Request request, Callback callback) throws RequestFailException {
		client.newCall(request).enqueue(callback);
	}
	
	/**
	 * 异步请求
	 * 
	 * @param url
	 * @param callback
	 */
	public void async(Request request, Callback callback) { 
		client.newCall(request).enqueue(callback);
	}
	
	/**
	 * 同步请求
	 */
	public <RESPONSE extends HttpResponse> Response sync(Request request) throws RequestFailException {
		try {
			return client.newCall(request).execute();
		} catch (IOException e) {
			throw new RequestFailException(e);
		}
	}
}
