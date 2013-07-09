package main;

import java.io.IOException;

import main.songdownloader.MasterDownloader;
import main.songdownloader.SongQueue;
import main.structures.MultipleTry;
import main.structures.SongDataHolder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SpotifyDownloader {
	//Handle error in the calling method. try three times before giving up
	
	
	
	//Bullet proof data collection from Spotify
	public static SongDataHolder getSongDataForSpotifyURLWithTries(int maxTimes, String url) {
		//SpotifyDownloader sd = new SpotifyDownloader();
		Object[] input = {url};
		SongDataHolder sdh;
		MultipleTry<SongDataHolder> mt = new MultipleTry<SongDataHolder>(maxTimes, input) {
			public SongDataHolder tryThis() throws Exception {
				String url = (String)input[0];
				return SpotifyDownloader.getSongDataForSpotifyURL(url);
			}
		};
		mt.start();
		sdh = mt.getData();
		return sdh;
	}
	private static SongDataHolder getSongDataForSpotifyURL(String url) throws IOException {
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
		return new SongDataHolder(title, album, artist);
	}
	
	public static void main(String[] args) {
//		SongDataHolder song;
//		//http://open.spotify.com/track/5dTHtzHFPyi8TlTtzoz1J9 ---helena mcr
//		//http://open.spotify.com/track/1UX6IpGYtIOP7J40NeY5pp ---blink 182
//		//http://open.spotify.com/track/1GErReAT0swX36x3O8GyQn -- to the end mcr
//		//http://open.spotify.com/track/4VbDJMkAX3dWNBdn3KH6Wx -- helena beat foster the people
//		song = SpotifyDownloader.getSongDataForSpotifyURLWithTries(3, "http://open.spotify.com/track/1GErReAT0swX36x3O8GyQn");
//		MasterDownloader md = new MasterDownloader(song);
//		md.start();
//		md.download(song.artist + "-" + song.album + "-" + song.title + ".mp3"); 
//		//TODO, this may lead to confusion when it is not right
		
		SongQueue sq = new SongQueue("/users/manu/desktop/spotifyLinks.txt", 10);
		new Thread(sq).start();
		
	}
	
}
