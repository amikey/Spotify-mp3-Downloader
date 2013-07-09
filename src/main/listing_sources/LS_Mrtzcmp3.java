package main.listing_sources;


import java.io.IOException;

import main.structures.DownloadData;
import main.structures.SongDownloadListing;
import main.structures.MultipleTry;
import main.structures.SongInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LS_Mrtzcmp3 extends ListingSource {
	private static String BASE_URL = "http://www.mrtzcmp3.net/";
	private static String URL_END = "_1s.html";
	private String COMBINED_URL;
	
	public LS_Mrtzcmp3(SongInfo song) {
		super(song);
		COMBINED_URL = BASE_URL + formatSongDataString(song) + URL_END;
	}
	@Override
	void generateDownloadListing() {
		String url = COMBINED_URL;
		Document doc = null;
		
		int maxTimes = 3;
		Object[] input = {url};
		MultipleTry<Document> mt = new MultipleTry<Document>(maxTimes, input) {
			public Document tryThis() throws Exception {
				String url = (String)input[0];
				return Jsoup.connect(url)
						.userAgent(USER_AGENT)
						.referrer("http://www.google.com")
						.get();
			}
		};
		mt.start();
		doc = mt.getData();
		
		
		Elements downloadListings = doc.select("#myTable tbody tr");
		//looping thru listings
		for (int i=0; i<downloadListings.size(); i++) {
			Element downloadListing = downloadListings.get(i);
			try {
				Elements listingFields = downloadListing.select("td");
				String listingID = listingFields.get(1).text() + " " + listingFields.get(2).text(); //artist + song
				//TODO replace JSoup with HTMLUnit for this one
				//Element qualityInfoCell = listingFields.get(4);
				//Elements tokens = qualityInfoCell.select("span span");
				//String bitrate = tokens.get(0).text();
				//String size = tokens.get(2).text();
				String[] qualityTokens = {}; //{bitrate, size};
				Element downloadCell = listingFields.get(5);
				Element downloadAnchor = downloadCell.select("a").first();
				String downloadHref = downloadAnchor.attr("href");
				String downloadLink = BASE_URL + downloadHref;
				SongDownloadListing dl = new SongDownloadListing(song, listingID, 0, 0, downloadLink); //TODO either get size and bit or leave as is
				downloadListingHeap.add(dl);
			}
			catch (Exception e) {
				System.out.println("Listing Parsing Error (Mrtzcmp3)");
			}
		}
	}
	@Override
	String formatSongDataString(SongInfo song) {
		String temp = song.artist+"_"+ song.title;
		String finalString = temp.replaceAll(" ", "_");
		return finalString;
	}
	public DownloadData getDownloadData(SongDownloadListing dl) throws IOException {
		String temp = dl.downloadLink.substring(26);
		int underscoreIndex = temp.indexOf('_');
		String key = temp.substring(0, underscoreIndex);
		String dlEnd = ".mrtzcmp3";
		String strUrl = BASE_URL + key + dlEnd;
		//need to have download happen with same UserAgent
		DownloadData dd = new DownloadData(strUrl);
		//String prelimLink = dl.downloadLink;
		//TODO follow link to next page, and find real link
		return dd;
	}
}
