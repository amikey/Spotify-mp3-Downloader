package main.songdownloader;

import java.net.URLEncoder;



import main.structures.DownloadListing;
import main.structures.MultipleTry;
import main.structures.SongDataHolder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class SD_Dilandau extends SongDownloader {
	private static String BASE_URL = "http://en.dilandau.eu/download-songs-mp3/";
	private static String URL_END = "/1.html";
	private String COMBINED_URL;
	
	public SD_Dilandau(SongDataHolder song) throws Exception {
		super(song);
		COMBINED_URL = BASE_URL+formatSongDataString(song)+URL_END;
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
				
		//title
		Elements downloadListingsWithFiller = doc.select("table tbody tr");
		for (int i=0; i<downloadListingsWithFiller.size(); i++) {
			Element downloadListing = downloadListingsWithFiller.get(i);
			Elements listingFields = downloadListing.select("td");
			//ensures not working with filler rows
			try {
			if (listingFields.size() > 3) {
				String listingName = listingFields.get(0).text();
				String sizeString = listingFields.get(1).text();
				Element actionCol = listingFields.get(3);
				Element downloadAnchor = actionCol.select("a[download]").first();
				String downloadHref = downloadAnchor.attr("href");
				String downloadURL = downloadAnchor.attr("url");
				String downloadLink = downloadHref+downloadURL;
				String[] temp = sizeString.split(" ");
				int size = (int)((new Double(temp[0])) * 1024);
				DownloadListing dl = new DownloadListing(song, listingName, 0, size, downloadLink);
				downloadListingHeap.add(dl);
			}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Listing Parsing Error (Dilindau)");
			}
		}
	}
		

	@Override
	String formatSongDataString(SongDataHolder song) throws Exception {
		String temp = song.artist+" "+song.title;
		String urlEncoded = URLEncoder.encode(temp, "UTF-8");
		String finalString = urlEncoded.replaceAll("\\+", "%20");
		return finalString;
	}
	
	//-------------------//

}
