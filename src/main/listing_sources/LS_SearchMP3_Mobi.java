package main.listing_sources;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import main.structures.DownloadRequest;
import main.structures.SongInfo;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LS_SearchMP3_Mobi extends ListingSource {
	private String BASE_URL = "http://searchmp3.mobi/mp3/";
	
	public LS_SearchMP3_Mobi(SongInfo song) {
		super(song);
	}

	@Override
	Elements getCells(Document page) throws IOException {
		Elements cells = page.select(".wrapper ul li");
		return cells;
	}
	
	@Override
	String getListingID(Element cell) throws IOException {
		return cell.text().trim();
	}

	@Override
	DownloadRequest getDownloadRequest(Element cell) throws IOException,
			URISyntaxException {
		Element a = cell.select("a").get(0);
		String url = a.attr("href");
		return createDownloadRequest(url);
		
		//String downloadURL = url + "&download=1";
		//return genericDownloadRequest(downloadURL);
	}

	@Override
	String getInitURLForSong(SongInfo song) throws Exception {
		String songString = song.title + " " + song.artist;
		songString = songString.replaceAll(" ", "-");
		return BASE_URL + songString;
	}

	private DownloadRequest createDownloadRequest(String url) throws URISyntaxException {
		String downloadURL = url + "&download=1";
		HttpRequestBase request = new HttpGet(new URI(downloadURL));
		request.addHeader("User-Agent", USER_AGENT);
		request.addHeader("Referer", url);
		//request.addHeader("Host", "s14.searchmp3.mobi");
		DownloadRequest dreq = new DownloadRequest(request, httpContext);
		return dreq;
	}

}
