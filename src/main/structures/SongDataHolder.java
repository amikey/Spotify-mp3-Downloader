package main.structures;



public class SongDataHolder {
	public String title;
	public String album;
	public String artist;
	public String toString() {
		return artist+" - "+album+" - "+title;
	}
	public SongDataHolder(String title, String album, String artist) {
		this.title = title;
		this.album = album;
		this.artist = artist;
	}
//	public String urlString() throws UnsupportedEncodingException {
//		//String temp = artist+" "+album+" "+title;
//		String temp = artist+" "+title;
//		String urlEncoded = URLEncoder.encode(temp, "UTF-8");
//		String finalString = urlEncoded.replaceAll("\\+", "%20");
//		return finalString;
//	}
}
