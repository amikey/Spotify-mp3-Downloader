package main;

import java.io.IOException;

import main.song_downloaders.MasterSongDownloader;
import main.structures.MultipleTry;
import main.structures.SongInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class SpotifyDownloader {
	//Handle error in the calling method. try three times before giving up
	
	//TODO remove these methods from here. moved to single song downloader
	//Bullet proof data collection from Spotify
	public static SongInfo getSongDataForSpotifyURLWithTries(int maxTimes, String url) throws Exception {
		//SpotifyDownloader sd = new SpotifyDownloader();
		Object[] input = {url};
		SongInfo sdh;
		MultipleTry<SongInfo> mt = new MultipleTry<SongInfo>(maxTimes, input) {
			public SongInfo tryThis() throws Exception {
				String url = (String)input[0];
				return SpotifyDownloader.getSongDataForSpotifyURL(url);
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
		
		MasterSongDownloader msd = new MasterSongDownloader("/users/manu/desktop/rest.txt", "/users/manu/desktop/spotify_downloader_log.txt", 20);
		msd.startDownloadingAllSongs();
	}
	
}
