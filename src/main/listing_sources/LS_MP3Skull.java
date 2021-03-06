package main.listing_sources;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;


import main.structures.DownloadRequest;
import main.structures.SongInfo;

import org.apache.http.client.methods.HttpRequestBase;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LS_MP3Skull extends ListingSource {
	private static String BASE_URL = "http://mp3skull.com/mp3/";
	private static String URL_END = ".html";
	public LS_MP3Skull(SongInfo song) {
		super(song);
		//COMBINED_URL = "http://mp3skull.com/mp3/my_chemical_romance_helena.html";
	}
	
	@Override
	Elements getCells(Document page) throws IOException {
		Elements cells = page.select("#song_html");
		return cells;
	}

	@Override
	String getInitURLForSong(SongInfo song) {
		String temp = song.artist+"_"+ song.title;
		String songString = temp.replaceAll(" ", "_");
		return BASE_URL + songString + URL_END;		
	}

	@Override
	String getListingID(Element cell) throws IOException {
		Elements cellParts = cell.select("div");
		Element nameAndDownloadInfo = cellParts.get(2);
		Element nameDiv = nameAndDownloadInfo.select("div").get(1);
		String listingID = nameDiv.text();
		return listingID;
	}

	@Override
	DownloadRequest getDownloadRequest(Element cell) throws IOException, URISyntaxException {
		Elements cellParts = cell.select("div");
		Element nameAndDownloadInfo = cellParts.get(2);
		
		Element downloadDiv = nameAndDownloadInfo.select("div").get(3);
		Element downloadAnchor = downloadDiv.select("div div a").get(0);
		String downloadURL = downloadAnchor.attr("href");
		
		return genericDownloadRequest(downloadURL);
	}

}
