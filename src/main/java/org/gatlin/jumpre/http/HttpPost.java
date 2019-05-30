package org.gatlin.jumpre.http;

import java.io.Serializable;
import java.util.Map.Entry;

import org.gatlin.jumpre.http.exception.CodeException;
import org.gatlin.jumpre.util.Consts;
import org.gatlin.jumpre.util.SerializeUtil;
import org.gatlin.jumpre.util.menu.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpPost<RESPONSE extends HttpResponse, REQUEST extends HttpPost<RESPONSE, REQUEST>> extends HttpRequest<RESPONSE, REQUEST> {
	
	private static Logger logger = LoggerFactory.getLogger(HttpPost.class);
	
	protected Body body;
	private ContentType contentType;
	
	public HttpPost(String host, int port, String path) {
		super(host, port, path);
		this.contentType = ContentType.APPLICATION_FORM_URLENCODED_UTF_8;
	}
	
	public HttpPost(String host, int port, String path, ContentType contentType) {
		super(host, port, path);
		this.contentType = contentType;
	}
	
	public HttpPost(String url,ContentType contentType) {
		super(url);
		this.contentType = contentType;
	}
	
	public Body getBody() {
		return body;
	}

	public ContentType contentType() {
		return contentType;
	}
	
	/**
	 * post方式需要填充 request body
	 */
	@Override
	protected Request request() {
		Request.Builder rb = new Request.Builder().url(url());
		for (Entry<String, String> entry : headers.entrySet())
			rb.addHeader(entry.getKey(), entry.getValue());
		return rb.post(requestBody()).build();
	}
	
	@Override
	protected Request request_() {
		Request.Builder rb = new Request.Builder().url(url_());
		for (Entry<String, String> entry : headers.entrySet())
			rb.addHeader(entry.getKey(), entry.getValue());
		return rb.post(requestBody()).build();
	}
	
	protected RequestBody requestBody() {
		switch (contentType) {
		case APPLICATION_FORM_URLENCODED_UTF_8:
			FormBody.Builder fb = new FormBody.Builder(Consts.UTF_8);
			for (Entry<String, String> entry : params.entrySet())
				fb.add(entry.getKey(), entry.getValue());
			return fb.build();
		case APPLICATION_JSON_UTF_8:
			String body = SerializeUtil.GSON.toJson(this.body);
			logger.info("请求信息：{}",body);
			return RequestBody.create(MediaType.parse(contentType.mark()), body);
		default:
			throw new CodeException();
		}
	}
	
	public interface Body extends Serializable {
	}
}
