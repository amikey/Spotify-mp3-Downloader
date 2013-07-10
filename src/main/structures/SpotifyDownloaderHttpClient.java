package main.structures;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

public class SpotifyDownloaderHttpClient extends DefaultHttpClient {
		
	public SpotifyDownloaderHttpClient() {
		super();
		CookieStore cookieStore = new BasicCookieStore();
		this.setCookieStore(cookieStore);
		VariableNumberRetryHandler retryHandler = new VariableNumberRetryHandler(3);
		this.setHttpRequestRetryHandler(retryHandler);
	}
}
