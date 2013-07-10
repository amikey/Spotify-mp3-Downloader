package main.structures;

import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;

public class DownloadRequest {
	public HttpRequestBase httpRequest;
	public HttpContext httpContext;
	public DownloadRequest(HttpRequestBase requestBase, HttpContext httpContext) {
		this.httpRequest = requestBase;
		this.httpContext = httpContext;
	}
}
