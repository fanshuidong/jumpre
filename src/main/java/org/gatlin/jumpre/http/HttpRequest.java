package org.gatlin.jumpre.http;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gatlin.jumpre.http.exception.RequestFailException;
import org.gatlin.jumpre.util.Consts;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.SpringContextUtil;
import org.gatlin.jumpre.util.StringUtil;
import org.gatlin.jumpre.util.menu.Protocol;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

@SuppressWarnings("unchecked")
public abstract class HttpRequest<RESPONSE extends HttpResponse, REQUEST extends HttpRequest<RESPONSE, REQUEST>> {

	// 请求地址
	protected int port;
	protected String host;
	protected String path;
	protected String url;
	protected Protocol protocol;
	protected Class<RESPONSE> clazz;
	protected HttpService httpService;
	protected Map<String, String> params = new HashMap<String, String>();
	protected Map<String, String> headers = new HashMap<String, String>();

	protected HttpRequest(String host, int port, String path) {
		this.port = port;
		this.host = host;
		this.path = path;
		this.protocol = Protocol.HTTP;
		Type superType = getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) superType).getActualTypeArguments();
		this.clazz = (Class<RESPONSE>) generics[0];
		this.httpService = SpringContextUtil.getBean("httpService", HttpService.class);
//		this.httpService = new HttpService();
		httpService.init();
	}

	protected HttpRequest(String url) {
		this.url = url;
		Type superType = getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) superType).getActualTypeArguments();
		this.clazz = (Class<RESPONSE>) generics[0];
		this.httpService = SpringContextUtil.getBean("httpService", HttpService.class);
//		this.httpService = new HttpService();
		httpService.init();
	}

	public void async(Callback<RESPONSE> callback) {
		callback.request = this;
		this.httpService.async(request(), callback);
	}

	public RESPONSE sync() {
		Response response = this.httpService.sync(request());
		if (!response.isSuccessful())
			requestFailure(response);
		return response(response);
	}

	// 直接用完整请求地址
	public RESPONSE sync_() {
		Response response = this.httpService.sync(request_());
		if (!response.isSuccessful())
			requestFailure(response);
		return response(response);
	}

	// 直接用完整请求地址
	public void async_(Callback<RESPONSE> callback) {
		callback.request = this;
		this.httpService.async(request_(), callback);
	}

	protected void requestFailure(Response response) {
		String errorContent = null;
		try {
			errorContent = response.body().string();
		} catch (Exception e) {
		}
		String error = response.message();
		if (StringUtil.hasText(errorContent))
			error += " - [" + errorContent + "]";
		throw new RequestFailException(response.code(), error);
	}

	protected Request request() {
		Request.Builder rb = new Request.Builder().url(url());
		for (Entry<String, String> entry : headers.entrySet())
			rb.addHeader(entry.getKey(), entry.getValue());
		return rb.build();
	}

	// 直接用完整请求地址
	protected Request request_() {
		Request.Builder rb = new Request.Builder().url(url_());
		for (Entry<String, String> entry : headers.entrySet())
			rb.addHeader(entry.getKey(), entry.getValue());
		FormBody.Builder fb = new FormBody.Builder(Consts.UTF_8);
		for (Entry<String, String> entry : params.entrySet())
			fb.add(entry.getKey(), entry.getValue());
		return rb.post(fb.build()).build();
	}

	protected HttpUrl url() {
		HttpUrl.Builder builder = new HttpUrl.Builder().scheme(protocol.name());
		builder.host(host).port(port).addPathSegments(path);
		for (Entry<String, String> entry : params.entrySet())
			builder.addQueryParameter(entry.getKey(), entry.getValue());
		return builder.build();
	}

	// 直接用完整请求地址
	protected HttpUrl url_() {
		HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
		// for (Entry<String, String> entry : params.entrySet())
		// builder.addQueryParameter(entry.getKey(), entry.getValue());
		return builder.build();
	}

	/**
	 * 默认直接使用序列化类来序列化
	 */
	protected RESPONSE response(Response response) {
		try {
			RESPONSE resp = SerializeUtil.GSON.fromJson(response.body().string(), clazz);
			resp.verify();
			return resp;
		} catch (IOException e) {
			throw new RequestFailException(e);
		}
	}

	public REQUEST param(String name, String value) {
		this.params.put(name, value);
		return (REQUEST) this;
	}

	public REQUEST header(String name, String value) {
		this.headers.put(name, value);
		return (REQUEST) this;
	}

	public Map<String, String> params() {
		return params;
	}
}
