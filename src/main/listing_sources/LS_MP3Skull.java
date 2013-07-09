package main.listing_sources;


import main.structures.SongDownloadListing;
import main.structures.MultipleTry;
import main.structures.SongInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LS_MP3Skull extends ListingSource {
	private static String BASE_URL = "http://mp3skull.com/mp3/";
	private static String URL_END = ".html";
	private String COMBINED_URL;
	public LS_MP3Skull(SongInfo song) {
		super(song);
		COMBINED_URL = BASE_URL + formatSongDataString(song) + URL_END;
		//COMBINED_URL = "http://mp3skull.com/mp3/my_chemical_romance_helena.html";
	}

	@Override
	public void generateListings() throws Exception {
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
			
		Elements downloadListings = doc.select("#song_html"); //#content div div
		
		for (int i=0; i<downloadListings.size(); i++) {
			Element downloadListing = downloadListings.get(i);
			//the try catch is a temporary fix
			try {
				Elements cellParts = downloadListing.select("div");
				Element qualityInfo = cellParts.get(1); //TODO maynot be there
				Element nameAndDownloadInfo = cellParts.get(2);
				Element nameDiv = nameAndDownloadInfo.select("div").get(1);
				String listingID = nameDiv.text();
				
				Element downloadDiv = nameAndDownloadInfo.select("div").get(3);
				Element downloadAnchor = downloadDiv.select("div div a").get(0);
				String downloadHref = downloadAnchor.attr("href");
				String downloadLink = downloadHref;
				
				String qualityString = qualityInfo.text();
				String[] tempQualityTokens  = qualityString.split(" ");
				String bitrateString = "";
				String sizeString = "";
				int bitrate = 0;
				int size = 0;
				for (int j=1; j<tempQualityTokens.length; j++) {
					if (tempQualityTokens[j].toLowerCase().equals("kbps")) {
						bitrateString = tempQualityTokens[j-1];
					}
					else if (tempQualityTokens[j].toLowerCase().equals("mb")) {
						sizeString = tempQualityTokens[j-1];
					}
				}
				if (bitrateString.length() > 0)
					bitrate = (int)(new Integer(bitrateString));
				if (sizeString.length() > 0)
					size = (int)((new Double(sizeString)) * 1024);
								
				SongDownloadListing dl = new SongDownloadListing(song, listingID, bitrate, size, downloadLink);
				downloadListingHeap.add(dl);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("Listing Parsing Error (MP3Skull)");
			}
		}
	}

	@Override
	String formatSongDataString(SongInfo song) {
		String temp = song.artist+"_"+ song.title;
		String finalString = temp.replaceAll(" ", "_");
		return finalString;
	}

}
