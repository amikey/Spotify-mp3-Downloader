package main.songdownloader;

import java.io.IOException;

import main.structures.BinaryHeap;
import main.structures.DownloadData;
import main.structures.DownloadListing;
import main.structures.SongDataHolder;



public abstract class SongDownloader {
	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";
	protected SongDataHolder song;
	public BinaryHeap<DownloadListing> downloadListingHeap = new BinaryHeap<DownloadListing>();

	abstract void generateDownloadListing();
	abstract String formatSongDataString(SongDataHolder song) throws Exception;
	
	public SongDownloader(SongDataHolder song) {
		this.song = song;
	}
	public BinaryHeap<DownloadListing> getDownloadListingsHeap() {
		return downloadListingHeap;
	}
	public void start() {
		generateDownloadListing();
	}
	public DownloadListing peakBestDownload() {
		return downloadListingHeap.peek(); //TODO there is an error if empty
	}
	public DownloadListing popBestDownload() {
		return downloadListingHeap.remove();
	}
	public DownloadData getDownloadData(DownloadListing dl) throws IOException {
		String strUrl = dl.downloadLink;
		return new DownloadData(strUrl);
	}
	
	//takes a string URL, downloads it to a given place, tracks download and prints stars as it downloads
/*
 	public static void test(String strUrl) throws IOException {
		//String strUrl = "http://something.to.download";  
		URL url = new URL(strUrl);  
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();  
		connection.connect();
		// Check if the request is handled successfully
		int contentLength = -1;
		if(connection.getResponseCode() / 100 == 2)  
		{  
		    // This should get you the size of the file to download (in bytes)  
		    contentLength = connection.getContentLength(); 
		    System.out.println(contentLength);
		}
		if (contentLength < 1) {
			//TODO means that there is an error
		}
		InputStream input = connection.getInputStream();
		
		File f=new File("test.mp3");
		OutputStream out=new FileOutputStream(f);
		byte buf[]=new byte[1024];
		int len;
		
		int maxCopyTimes = contentLength/1024;
		int copyTime = 0;
		int markersPrinted = 0;
		int maxMarkers = 15;
		maxMarkers--;
		while((len=input.read(buf))>0) {
			out.write(buf,0,len);
			double percentage = (double)copyTime / maxCopyTimes * 100;
			double currPercentage = (double)markersPrinted / maxMarkers * 100;
			if (percentage > currPercentage) {
				System.out.print("*");
				markersPrinted++;
			}
			copyTime++;
		}
		out.close();
		input.close();		
	}
	public boolean beginDownload(DownloadListing dl) throws IOException {
		String strUrl = dl.downloadLink;
		URL url = new URL(strUrl); //if this throws an error, no point in trying again.
		// tries 3 times to open and connect to URL
		int maxTimes = 3;
		Object[] input = {url};
		MultipleTry<HttpURLConnection> mt = new MultipleTry<HttpURLConnection>(maxTimes, input) {
			public HttpURLConnection tryThis() throws Exception {
				URL url = (URL)input[0];
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.connect();
				return connection;
			}
		};
		mt.start();
		//definitely good connection
		HttpURLConnection connection = mt.getData();
		int fileSize = getFileSize(connection); //if error is thrown, stop because, there was not a good response code
		//still a possibility of -1
		
		
		return false;
	}
	protected int getFileSize(HttpURLConnection connection) throws IOException {
		// Check if the request is handled successfully
		int contentLength = -1;
		if(connection.getResponseCode() / 100 == 2)  
		{  
		    // This should get you the size of the file to download (in bytes)  
		    contentLength = connection.getContentLength(); 
		    //System.out.println(contentLength);
		}
//		if (contentLength < 1) {
//			contentLength = -1;
//			//throw new IOException("was not able to get file size");
//		}
		return contentLength;
	}
	//protected void printDownloadListing
*/
	
}
   
//rank download listings
/*
1. song title			32
2. artist				16
3. NO {live, remix}		8
4. bitrate				4
5. size					2




*/