package main.structures;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import main.listing_sources.ListingSource;


public class DownloadData {
	public InputStream input;
	public int size = 0;
	public DownloadData(String strUrl) throws Exception { //if throws error, stop because IDK what to do with it
		URL url = new URL(strUrl); //if this throws an error, no point in trying again.
		// tries 3 times to open and connect to URL
		int maxTimes = 3;
		Object[] inputData = {url};
		MultipleTry<HttpURLConnection> mt = new MultipleTry<HttpURLConnection>(maxTimes, inputData) {
			public HttpURLConnection tryThis() throws Exception {
				URL url = (URL)input[0];
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setRequestProperty("User-Agent", ListingSource.USER_AGENT);
				connection.connect();
				return connection;
			}
		};
		mt.start();
		//definitely good connection if no error
		HttpURLConnection connection = mt.getData();
		size = getFileSize(connection); //if error is thrown, stop because, there was not a good response code
		//still a possibility of -1
		if (size < 0) {
			System.out.print("FILE NOT VALID, throwing error, ");
			throw new IOException();
		}
		this.input = connection.getInputStream();
		
	}
	protected int getFileSize(HttpURLConnection connection) throws IOException {
		//connection guaranteed to not be null
		//Check if the request is handled successfully
		int contentLength = -1;
		if(connection !=null && connection.getResponseCode() / 100 == 2)  
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
	public DownloadData(InputStream input, int size) {
		this.input = input;
		this.size = size;
	}
}
