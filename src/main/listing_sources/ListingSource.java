package main.listing_sources;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.NoSuchElementException;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import main.structures.BinaryHeap;
import main.structures.DownloadRequest;
import main.structures.SongDownloadListing;
import main.structures.SongInfo;
import main.structures.VariableNumberRetryHandler;


import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;





public abstract class ListingSource {
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
	protected SongInfo song;
	public BinaryHeap<SongDownloadListing> downloadListingHeap = new BinaryHeap<SongDownloadListing>();
	
	protected HttpContext httpContext;
	
	public ListingSource(SongInfo song) {
		this.song = song;
		CookieStore cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	
	
	public void generateListings() throws Exception{
		Document page = null;
		int num_cells = 0;
		String initURL = getInitURLForSong(song);
		page = loadPage(initURL);
		num_cells = getTotalCells(page);
		for (int i=0; i<num_cells; i++) {
			try {
				Element cell = getCell(page, i);
				String listingID = getListingID(cell);
				DownloadRequest req = getDownloadRequest(cell);
				SongDownloadListing sdl = new SongDownloadListing(song, listingID, req);
				if (!sdl.shouldReject()) {
					downloadListingHeap.add(sdl);
				}
				
			} catch (URISyntaxException e) {
				//error in encoding the uri
				continue;
			} catch (NoSuchElementException e) {
				//could not find cell at index
				continue;
			} catch (IOException e) {
				//error in parsing
				continue;
			}
		}
	}
	Document loadPage(String url) throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		//client.setHttpRequestRetryHandler(new VariableNumberRetryHandler(3));
		HttpRequestBase request = genericHttpRequest(url);
		
		HttpResponse response = client.execute(request, httpContext);
		InputStream in = response.getEntity().getContent();
		String htmlString = inputStreamToString(in);
		Document doc = Jsoup.parse(htmlString);

		return doc;
	}
	
	abstract int getTotalCells(Document page) throws IOException;
	abstract Element getCell(Document page, int index) throws NoSuchElementException, IOException;
	abstract String getListingID(Element cell) throws IOException;
	abstract DownloadRequest getDownloadRequest(Element cell) throws IOException, URISyntaxException;
	abstract String getInitURLForSong(SongInfo song) throws Exception;
	
	protected HttpRequestBase genericHttpRequest(String url) throws URISyntaxException {		
		HttpRequestBase request = new HttpGet(new URI(url));
		request.addHeader("User-Agent", USER_AGENT);
		request.addHeader("Referer", "http://www.google.com");
		return request;
	}
	protected DownloadRequest genericDownloadRequest(String url) throws URISyntaxException {
		
		HttpRequestBase request = genericHttpRequest(url);
		DownloadRequest dreq = new DownloadRequest(request, httpContext);
		return dreq;
	}
	final protected String inputStreamToString(InputStream is) throws IOException {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    
	    // Wrap a BufferedReader around the InputStream
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	    }
	    
	    // Return full string
	    	    
	    return total.toString();
	}
	
	public BinaryHeap<SongDownloadListing> getListingsHeap() {
		return downloadListingHeap;
	}

	public SongDownloadListing peakListing() throws IllegalStateException {
		return downloadListingHeap.peek(); //TODO there is an error if empty
	}
	public SongDownloadListing popListing() {
		return downloadListingHeap.remove();
	}
	
}
   
//rank download listings
/*
1. song title			32
2. artist				16
3. NO {live, remix}		8
4. bitrate				4
5. size					2
*/