package main.song_downloaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;


import main.SpotifyDownloader;
import main.structures.SongInfo;
//this will be the class that starts everything
public class MultiSongDownloader_Queue implements Runnable {
	ConcurrentLinkedQueue<String> spotifyLinkQueue = new ConcurrentLinkedQueue<String>();
	ConcurrentLinkedQueue<String> failedSongs = new ConcurrentLinkedQueue<String>();
	int numDone = 0;
	String filepath;
	int maxConcurrentDownloads;
	int currConcurrentDownloads = 0;
	public MultiSongDownloader_Queue(String filepath, int maxConcurrentDownloads) {
		this.maxConcurrentDownloads = maxConcurrentDownloads;
		this.filepath = filepath;
	}
	public void successfullyDownloaded(SongInfo song) {
		numDone++;
		System.out.print("("+numDone+"), ");
		System.out.println("successfully downloaded: " + song);
		currConcurrentDownloads--;
		tryStartNewDownload();
	}
	public void failedDownloaded(String songString) {
		numDone++;
		System.out.print("("+numDone+"), ");
		System.out.println("FAILED to download: " + songString);
		failedSongs.offer(songString);
		currConcurrentDownloads--;
		tryStartNewDownload();
	}
	public void tryStartNewDownload() {
		while (currConcurrentDownloads < maxConcurrentDownloads && !this.spotifyLinkQueue.isEmpty()) { //changed from if to while to ensure, max num of threads
			currConcurrentDownloads++;
			SongInfo song;
			String spotifyURL = this.spotifyLinkQueue.poll();
			song = SpotifyDownloader.getSongDataForSpotifyURLWithTries(3, spotifyURL);
			if (song == null) {
				failedDownloaded(spotifyURL);
			}
			else {
				SingleSongDownloader md = new SingleSongDownloader_Concurrent(this, song);
				new Thread(md).start();
			}
		}
		if (this.spotifyLinkQueue.isEmpty() && currConcurrentDownloads ==0) {
			finishedAllDownloads();
		}
	}
	public void finishedAllDownloads() {
		System.out.println();
		System.out.println();
		System.out.println("COMPLETE");
		System.out.println();
		System.out.println("Songs that failed to download: ");
		System.out.println(failedSongs);
	}
	@Override
	public void run() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(filepath));
		} catch (FileNotFoundException e) {
			return;
		}
		while(scanner.hasNextLine()) {
			String spotifySongURL = scanner.nextLine();
			if (!spotifySongURL.substring(24, 29).equals("local")) {
				spotifyLinkQueue.offer(spotifySongURL);
				tryStartNewDownload();
			}
		}
	}
}
