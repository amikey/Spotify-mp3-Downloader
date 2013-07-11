package main.song_downloaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.listing_sources.LS_Dilandau;
import main.listing_sources.LS_MP3Juices;
import main.listing_sources.LS_MP3Skull;
import main.listing_sources.ListingSource;
import main.structures.MultipleTry;
import main.structures.SongDownloadListing;
import main.structures.SongInfo;
import main.structures.VariableNumberRetryHandler;

public class SingleSongDownloader implements Runnable {
	private ArrayList<ListingSource> sources = new ArrayList<ListingSource>();
	protected SongInfo song;
	protected String spotifyLink;
	
	protected final String tempDir = "./temp/";
	protected final String doneDir = "./Music/";
	
	public SingleSongDownloader(String spotifyLink) {
		this.spotifyLink=spotifyLink;
	}
	
	public void initializeSources() throws Exception {
		this.song = getSongDataForSpotifyURLWithTries(3, spotifyLink);
//		try {
//			ListingSource sdl1 = new LS_Dilandau(song);
//			sources.add(sdl1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		

		ListingSource sdl2 = new LS_MP3Juices(song);
		sources.add(sdl2);
		ListingSource sdl3 = new LS_MP3Skull(song);
		sources.add(sdl3);

	}
	public void populateListingSources() {
		System.out.println("starting to download: "+song);
		for(int i=0; i<sources.size(); i++) {
			try {
				sources.get(i).generateListings();
			} catch (Exception e) {
				sources.set(i, null);
			}
		}
		for(int i=0; i<sources.size(); i++) {
			if (sources.get(i) == null) {
				sources.remove(i);
			}
		}
	}
	private SongDownloadListing popBestDownloadData() throws Exception {
		for(int i=sources.size()-1; i>=0; i--) {
			if (sources.get(i).downloadListingHeap.isEmpty()) {
				sources.remove(i);
			}
		}
		if (sources.size() == 0) throw new IOException("NO DOWNLOAD LINKS");
		
		int bestListingSourceIndex = 0;
		SongDownloadListing dl = sources.get(0).peakListing();
		for(int i=1; i<sources.size(); i++) {
			SongDownloadListing dl2 = sources.get(i).peakListing();
			if (dl.compareTo(dl2) < 1) {
				dl = dl2;
				bestListingSourceIndex = i;
			}
		}
		SongDownloadListing bestSDL = sources.get(bestListingSourceIndex).popListing();
		
		return bestSDL;
	}
	
	//Bullet proof data collection from Spotify
	public static SongInfo getSongDataForSpotifyURLWithTries(int maxTimes, String url) throws Exception {
		//SpotifyDownloader sd = new SpotifyDownloader();
		Object[] input = {url};
		SongInfo sdh;
		MultipleTry<SongInfo> mt = new MultipleTry<SongInfo>(maxTimes, input) {
			public SongInfo tryThis() throws Exception {
				String url = (String)input[0];
				return SingleSongDownloader.getSongDataForSpotifyURL(url);
			}
		};
		mt.start();
		sdh = mt.getData();
		return sdh;
	}
	private static SongInfo getSongDataForSpotifyURL(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		//title
		Elements tempTitleElems = doc.select("h1[itemprop=name]");
		Element titleElem = tempTitleElems.first();
		String title = titleElem.text();
		//sdh.title = title;
		//artist
		Elements tempArtistElems = doc.select("h2 a");
		Element artistElem = tempArtistElems.first();
		String artist = artistElem.text();
		//sdh.artist = artist;
		//album
		Elements tempAlbumElems = doc.select("h3 a");
		Element albumElem = tempAlbumElems.first();
		String album = albumElem.text();
		//sdh.album = album;
		//System.out.println(sdh);
		return new SongInfo(title, album, artist);
	}

	public boolean downloadSongToFile(String filepath) { 
		int timesTried = 0;
		int maxTries = 5;
		boolean success = false;
		SongDownloadListing sdl = null;
		while (!success && timesTried < maxTries) {
			try {
				sdl = popBestDownloadData();
				//dd is initialized if error is not caught
				download(filepath, sdl);
				success = true;
			} catch (Exception e) {
				timesTried++;
				System.out.println("error downloading, trying again: "+song);
				e.printStackTrace();
			}
		}
		return success;
	}
	private void download(String filepath, SongDownloadListing sdl) throws IOException {
		//set up
		DefaultHttpClient client = new DefaultHttpClient();
		VariableNumberRetryHandler retryHandler = new VariableNumberRetryHandler(3);
		client.setHttpRequestRetryHandler(retryHandler);
		//do stuff
		HttpResponse response = client.execute(sdl.request.httpRequest, sdl.request.httpContext);
		HttpEntity entity = response.getEntity();
		long contentSize = entity.getContentLength();
		if (contentSize/1024/1024 > 11 || contentSize/1024/1024 < 1) {
			throw new IOException("NOT RIGHT SIZE PROB FAKE");
		}
		InputStream in = entity.getContent();
		download(filepath, in);
		//clean up
		EntityUtils.consume(entity);
		sdl.request.httpRequest.releaseConnection();
	}
	private void download(String filepath, InputStream input) throws IOException {
		File f=new File(filepath);
		//f.getParentFile().mkdirs(); //TODO this line will add folders that dont exist, but will crash if no folders needed
		OutputStream out=new FileOutputStream(f);
		byte buf[]=new byte[1024];
		int len;
		while((len=input.read(buf))>0) {
			out.write(buf,0,len);
		}
		out.close();
		input.close();
	}
//	private void download(String filepath, InputStream input, int size) throws IOException {
//		File f=new File(filepath);
//		//f.getParentFile().mkdirs(); //TODO this line will add folders that dont exist, but will crash if no folders needed
//		OutputStream out=new FileOutputStream(f);
//		byte buf[]=new byte[1024];
//		int len;
//		
//		int maxCopyTimes = size/1024; 
//		int copyTime = 0;
//				
//		while((len=input.read(buf))>0) {
//			out.write(buf,0,len);
//			double percentage = (double)copyTime / maxCopyTimes * 100;
//			setCurrProgress(percentage);
//		}
//		out.close();
//		input.close();
//	}
	protected void setCurrProgress(double percentage) {}
		
	public void successfulDownload() {}
	public void failedDownload() {}

	protected void moveFile(String oldLocation, String newLocation) {
		File file = new File(oldLocation);
		moveFile(file, newLocation);
	}
	protected void moveFile(File file, String newLocation) {
		try{
			if(file.renameTo(new File(newLocation + file.getName()))){
				System.out.println("File is moved successful!");
			} else {
				System.out.println("File is failed to move!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	protected String createFilename(String directory, SongInfo song) {
		String basic = (song.artist + "-" + song.album + "-" + song.title + ".mp3");
		String encoded = basic.replaceAll("/", "-");
		return directory+encoded;
	}
	
	@Override
	public void run() {
		try {
			initializeSources();
			populateListingSources();
			String tempFilename = createFilename(tempDir, song);
			String doneFilename = createFilename(doneDir, song);
			boolean success = downloadSongToFile(tempFilename);
			if (success) {
				moveFile(tempFilename, doneFilename);
				successfulDownload();
			}
			else failedDownload();
		} catch (Exception e) {
			failedDownload();
		}
	}
}
