package main.listing_sources;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;

import main.structures.DownloadRequest;
import main.structures.SongInfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LS_MP3Juices extends ListingSource {
	private static String BASE_URL = "http://mp3juices.com/search/";
	private static String URL_END = "";
	
	public LS_MP3Juices(SongInfo song) {
		super(song);
	}
	
	@Override
	Elements getCells(Document page) throws IOException {
		Elements cells = page.select(".search_container > table tr.mpres");
		return cells;
	}
	
	@Override
	String getListingID(Element cell) throws IOException {
		String listingID = cell.select(".song_title").get(0).text();
		return listingID;
	}

	@Override
	DownloadRequest getDownloadRequest(Element cell) throws IOException, URISyntaxException {
		String downloadURL = cell.select(".controls input").get(1).attr("value");
		return genericDownloadRequest(downloadURL);
	}
	
	@Override
	String getInitURLForSong(SongInfo song) throws Exception {
		String temp = song.artist+"-"+ song.title;
		String songString = temp.replaceAll("/", " ");
		songString = songString.replaceAll(" ", "-");
		return BASE_URL + songString + URL_END;
	}
	
}
