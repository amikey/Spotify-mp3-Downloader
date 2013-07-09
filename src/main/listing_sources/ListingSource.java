package main.listing_sources;

import java.io.IOException;

import main.structures.BinaryHeap;
import main.structures.DownloadData;
import main.structures.SongDownloadListing;
import main.structures.SongInfo;



public abstract class ListingSource {
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
	protected SongInfo song;
	public BinaryHeap<SongDownloadListing> downloadListingHeap = new BinaryHeap<SongDownloadListing>();

	public abstract void generateListings();
	abstract String formatSongDataString(SongInfo song) throws Exception;
	
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
	public DownloadData getDownloadData(SongDownloadListing dl) throws IOException {
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