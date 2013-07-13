package main.song_downloaders;

import java.awt.datatransfer.MimeTypeParseException;
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
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagConstant;
import org.farng.mp3.TagException;
import org.farng.mp3.TagOptionSingleton;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.listing_sources.LS_MP3Juices;
import main.listing_sources.LS_MP3Skull;
import main.listing_sources.LS_SearchMP3_Mobi;
import main.listing_sources.ListingSource;
import main.structures.MultipleTry;
import main.structures.SongDownloadListing;
import main.structures.SongInfo;
import main.structures.VariableNumberRetryHandler;
import main.structures.SpaceRedirectHandler;

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

		ListingSource sdl1 = new LS_SearchMP3_Mobi(song);
		sources.add(sdl1);
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
	protected final static SongInfo getSongDataForSpotifyURLWithTries(int maxTimes, String url) throws Exception {
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
	protected final static SongInfo getSongDataForSpotifyURL(String url) throws IOException {
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
		client.setRedirectHandler(new SpaceRedirectHandler());
		
		HttpResponse response = client.execute(sdl.request.httpRequest, sdl.request.httpContext);
		HttpEntity entity = response.getEntity();
		long contentSize = entity.getContentLength();
		String contentType = entity.getContentType().getValue();
		if (!contentType.equals("audio/mpeg")) {
			System.out.print("invalide MIME type: "+contentType);
			throw new IOException("Wrong MIME Type, probably HTML");
		}
		if (contentSize != -1) {
			if (contentSize/1024/1024 > 11 || contentSize/1024/1024 < 1) {
				System.out.println("content size was: " + contentSize/1024/1024);
				throw new IOException("NOT RIGHT SIZE PROB FAKE");
			}
		}
		else {
			System.out.println("size of file is unknown");
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
	
	protected final void setSongMetaData(String filename) throws IOException, TagException {
		setSongMetaData(this.song, filename);
	}
	protected final void setSongMetaData(File f) throws IOException, TagException {
		setSongMetaData(this.song, f);
	}
	protected final void setSongMetaData(SongInfo song, String filename) throws IOException, TagException {
		File f = new File(filename);
		setSongMetaData(song, f);
	}
	protected final void setSongMetaData(SongInfo song, File f) throws IOException, TagException {
		MP3File mp3file = new MP3File(f);
	    //TagOptionSingleton.getInstance().setDefaultSaveMode(TagConstant.MP3_FILE_SAVE_OVERWRITE);
	    AbstractMP3Tag tag;
	    if (mp3file.hasID3v1Tag()) {
	    	tag = new ID3v1();
	    	tag.setSongTitle(song.title);
	    	tag.setAlbumTitle(song.album);
	    	tag.setLeadArtist(song.artist);
	    	//ID3v1 t = new ID3v1();
	    	mp3file.setID3v1Tag(tag);
	    }
	    if (mp3file.hasID3v2Tag()) {
	    	//tag = mp3file.getID3v2Tag();
	    	tag = new ID3v2_2();
	    	tag.setSongTitle(song.title);
	    	tag.setAlbumTitle(song.album);
	    	tag.setLeadArtist(song.artist);
	    	mp3file.setID3v2Tag(tag);
	    }
	    if (mp3file.hasLyrics3Tag()) {
	    	tag = mp3file.getLyrics3Tag();
	    	tag.setSongLyric("");
	    	mp3file.setLyrics3Tag(tag);
	    }
	    mp3file.save();
	}
	
	protected void setCurrProgress(double percentage) {}
		
	public void successfulDownload() {}
	public void failedDownload() {}

	protected final void moveFile(String oldLocation, String newLocation) {
		File file = new File(oldLocation);
		moveFile(file, newLocation);
	}
	protected final void moveFile(File file, String newLocation) {
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
			//String doneFilename = createFilename(doneDir, song);
			boolean success = downloadSongToFile(tempFilename);
			if (success) {
				setSongMetaData(tempFilename);
				moveFile(tempFilename, doneDir);
				successfulDownload();
			}
			else failedDownload();
		} catch (Exception e) {
			failedDownload();
		}
	}

	public static void main(String[] args) {
		
		
	}
}
