package main.listing_sources;


import java.io.IOException;
import java.util.NoSuchElementException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import main.structures.BinaryHeap;
import main.structures.DownloadData;
import main.structures.MultipleTry;
import main.structures.SongDownloadListing;
import main.structures.SongInfo;



public abstract class ListingSource {
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
	protected SongInfo song;
	public BinaryHeap<SongDownloadListing> downloadListingHeap = new BinaryHeap<SongDownloadListing>();

	public void generateListings() throws Exception {
		Document page = null;
		int num_cells = 0;
		String initURL = getInitURLForSong(song);
		page = loadPage(initURL);
		num_cells = getTotalCells(page);
		for (int i=0; i<num_cells; i++) {
			try {
				Element cell = getCell(page, i);
				String listingID = getListingID(cell);
				Connection conn = getDownloadConnection(cell);
				SongDownloadListing sdl = new SongDownloadListing(song, listingID, conn);
				if (!sdl.shouldReject()) {
					downloadListingHeap.add(sdl);
				}
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
		return doc;
	}
	
	abstract int getTotalCells(Document page) throws IOException;
	abstract Element getCell(Document page, int index) throws NoSuchElementException, IOException;
	abstract String getListingID(Element cell) throws IOException;
	abstract Connection getDownloadConnection(Element cell) throws IOException;
	abstract String getInitURLForSong(SongInfo song) throws Exception;

	protected Connection genericConnection(String url) {
		Connection conn = 	
			Jsoup
			.connect(url)
			.userAgent(USER_AGENT)
			.referrer("http://www.google.com")
			.method(Method.GET);
		return conn;
	}
	
	public ListingSource(SongInfo song) {
		this.song = song;
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
	public DownloadData getDownloadData(SongDownloadListing dl) throws Exception {
		String strUrl = dl.downloadLink;
		return new DownloadData(strUrl);
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