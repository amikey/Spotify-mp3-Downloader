package other;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Downloader {
	private static String BASE_URL = "http://en.dilandau.eu/download-songs-mp3/";
	private static String URL_END = "/1.html";
	private static String DOWNLOAD_DIR = "/Users/Manu/SpotifyDownloaderSongs/Music/";
	private static ArrayList<DownloadListing> downloadListings = new ArrayList<DownloadListing>();
	
	
	public static void getFileForSong(Song s) throws IOException {
		String url = BASE_URL+s.urlString()+URL_END;
		downloadListings = getListingsForURL(url);
		for(int i=0; i<downloadListings.size(); i++) {
			rankDownloadListings(s, downloadListings.get(i));
		}
		Collections.sort(downloadListings, new Comparator<DownloadListing>() {
			@Override
			public int compare(DownloadListing o1, DownloadListing o2) {
				return o2.quality - o1.quality;
			}
		});
		
		boolean success = false;
		int index = 0;
		File f = new File(DOWNLOAD_DIR+s.artist+"/"+s.album+"/"+URLEncoder.encode(s.title, "UTF-8")+".mp3");
		f.getParentFile().mkdirs();
		while (!success && index < downloadListings.size()) {
			URL downloadURL;
			try {
				downloadURL = new URL(downloadListings.get(index).downloadLink);
				FileUtils.copyURLToFile(downloadURL, f);
				if (f.length()/1024/1024 > 1) {
					success = true;
				}
				else {
					f.delete();
				}
			} catch (Exception e) {
				System.out.print("dl,");
				index++;
			}
		}
		if (success==true) {
			System.out.println("DONE");
		}
		else {
			System.out.println("ERROR");
		}
	}
	
	private static ArrayList<DownloadListing> getListingsForURL(String url) {
		Document doc = null;
		boolean success = false;
		int tries = 0;
		while (!success && tries < 3) {
			try {
				doc = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31")
						.referrer("http://www.google.com")
						.get();
				success = true;
			} catch (Exception e) {
				System.out.print("url,");
				tries++;
			}
		}
		if (tries>=3) {
			return new ArrayList<DownloadListing>();
		}
				
		ArrayList<DownloadListing> DLListings = new ArrayList<DownloadListing>();
		//title
		Elements downloadListingsWithFiller = doc.select("table tbody tr");
		for (int i=0; i<downloadListingsWithFiller.size(); i++) {
			Element downloadListing = downloadListingsWithFiller.get(i);
			Elements listingFields = downloadListing.select("td");
			//ensures not working with filler rows
			try {
			if (listingFields.size() > 1) {
				String listingName = listingFields.get(1).text();
				String sizeString = listingFields.get(2).text();
				Element actionCol = listingFields.get(4);
				Element downloadAnchor = actionCol.select("a[download]").first();
				String downloadHref = downloadAnchor.attr("href");
				String downloadURL = downloadAnchor.attr("url");
				String downloadLink = downloadHref+downloadURL;
				DownloadListing dl = new DownloadListing(listingName, sizeString, downloadLink);
				DLListings.add(dl);
			}
			} catch (Exception e) {
				System.out.print("listing,");
			}
		}
		return DLListings;
	}
	private static void rankDownloadListings(Song s, DownloadListing dl) {
		int quality = 0;
		boolean artistMatch = dl.listingName.matches(".*?"+s.artist+".*?");
		boolean titleMatch = dl.listingName.matches(".*?"+s.title+".*?");
		boolean albumMatch = dl.listingName.matches(".*?"+s.album+".*?");
		boolean remix = dl.listingName.matches(".*?remix.*?");
		Scanner sc = new Scanner(dl.sizeString);
		if (sc.hasNextDouble()) {
			double d = sc.nextDouble();
			quality+=d*10;
		}
		//good to have
		quality+= albumMatch  ? 10000 : 0;
		quality+= artistMatch ? 1000000 : 0;
		//must haves
		quality= titleMatch  ? 100000 + quality : 0;
		quality = remix? 0 : quality;
		dl.quality = quality;
	}
	private static class DownloadListing {
		String listingName;
		String sizeString;
		String downloadLink;
		int quality;
		public DownloadListing(String listingName, String sizeString, String downloadLink) {
			this.listingName = listingName;
			this.sizeString = sizeString;
			this.downloadLink = downloadLink;
		}
	}
	
	/*File f = new File("C:/a/b/test.txt");
f.getParentFile().mkdirs();
*/

}
