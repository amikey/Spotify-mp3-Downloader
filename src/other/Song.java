package other;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Song {
	public String title;
	public String album;
	public String artist;
	
	public Song(String url) throws IOException {
		Document doc = Jsoup.connect(url).get();
		//title
		Elements tempTitleElems = doc.select("h1[itemprop=name]");
		Element titleElem = tempTitleElems.first();
		String title = titleElem.text();
		this.title = title;
		//artist
		Elements tempArtistElems = doc.select("h2 a");
		Element artistElem = tempArtistElems.first();
		String artist = artistElem.text();
		this.artist = artist;
		//album
		Elements tempAlbumElems = doc.select("h3 a");
		Element albumElem = tempAlbumElems.first();
		String album = albumElem.text();
		this.album = album;
		System.out.println(this);
	}
	
	public String toString() {
		return artist+" - "+album+" - "+title;
	}
	
	public String urlString() throws UnsupportedEncodingException {
		//String temp = artist+" "+album+" "+title;
		String temp = artist+" "+title;
		String urlEncoded = URLEncoder.encode(temp, "UTF-8");
		String finalString = urlEncoded.replaceAll("\\+", "%20");
		return finalString;
	}
}
