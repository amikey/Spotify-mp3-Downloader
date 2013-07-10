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


public class LS_Mrtzcmp3 extends ListingSource {
	private static String BASE_URL = "http://www.mrtzcmp3.net/";
	private static String URL_END = "_1s.html";
	
	public LS_Mrtzcmp3(SongInfo song) {
		super(song);
	}
	
	@Override
	String getInitURLForSong(SongInfo song) {
		String temp = song.artist+"_"+ song.title;
		String songString = temp.replaceAll(" ", "_");
		return BASE_URL + songString + URL_END;
	}
	
	@Override
	int getTotalCells(Document page) throws IOException {
		Elements cells = page.select("#myTable tbody tr");
		int size = cells.size();
		return size;
	}
	
	@Override
	Element getCell(Document page, int index) throws NoSuchElementException, IOException {
		Elements cells = page.select("#myTable tbody tr");
		Element cell = cells.get(index);
		return cell;
	}

	@Override
	String getListingID(Element cell) throws IOException {
		Elements listingFields = cell.select("td");
		String listingID = listingFields.get(1).text() + " " + listingFields.get(2).text(); //artist + song
		return listingID;
	}
	
	@Override
	DownloadRequest getDownloadConnection(Element cell) throws IOException, URISyntaxException {
		Elements listingFields = cell.select("td");
		Element downloadCell = listingFields.get(5);
		Element downloadAnchor = downloadCell.select("a").first();
		String downloadURLEnd = downloadAnchor.attr("href");
		String initDownloadURL = BASE_URL + downloadURLEnd;
		
		String temp = initDownloadURL.substring(26);
		int underscoreIndex = temp.indexOf('_');
		String key = temp.substring(0, underscoreIndex);
		String dlEnd = ".mrtzcmp3";
		String finalDownloadURL = BASE_URL + key + dlEnd;
		
		return genericDownloadRequest(finalDownloadURL);
	}
}
