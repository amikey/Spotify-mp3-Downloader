package main.song_downloaders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import main.structures.SongInfo;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MasterSongDownloader {
	ConcurrentLinkedQueue<String> spotifyLinkQueue = new ConcurrentLinkedQueue<String>();
	ExecutorService executor;
	String songsListFilepath;
	int numDone = 0;
	File logFile;
	public MasterSongDownloader(String songsListFilepath, String logFilepath, int maxConcurrentDownloads) {
		this.songsListFilepath = songsListFilepath;
		logFile = new File(logFilepath);
		if (logFile.exists()){
			logFile.delete();
		}		
		executor = Executors.newFixedThreadPool(maxConcurrentDownloads);
	}

	public void queueAllSpotifyLinks() {
		Scanner scanner;
		try {
			scanner = new Scanner(new File(songsListFilepath));
		} catch (FileNotFoundException e) {
			return;
		}
		while(scanner.hasNextLine()) {
			String spotifySongURL = scanner.nextLine();
			if (!spotifySongURL.substring(24, 29).equals("local")) {
				spotifyLinkQueue.offer(spotifySongURL);
			}
			else {
				failedDownload(spotifySongURL, null);
			}
		}
	}
	public void createAndExecuteThreads() {
		while (!spotifyLinkQueue.isEmpty()) {
			String spotifyURL = this.spotifyLinkQueue.poll();
			SingleSongDownloader ssd = new SingleSongDownloader_Concurrent(this, spotifyURL);
			executor.execute(ssd);
		}
		executor.shutdown();
	}
	public void finishedAllThreads() {
		System.out.println("Finished all threads");
	}
	
	public void successfulDownload(SongInfo song) {
		numDone++;
		System.out.print("("+numDone+"), ");
		System.out.println("successfully downloaded: " + song);
	}
	public void failedDownload(String spotifyLink, SongInfo song) {
		String songString = spotifyLink;
		if (song != null) songString = songString + " -- " + song.toString();
		numDone++;
		System.out.print("("+numDone+"), ");
		System.out.println("FAILED to download: " + songString);
		try {
			FileWriter fstream = new FileWriter(logFile, true);
	        BufferedWriter out = new BufferedWriter(fstream);
	        out.write(songString);
	        out.newLine();
	        out.close();
	        fstream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void startDownloadingAllSongs() {
		queueAllSpotifyLinks();
		createAndExecuteThreads();
		while (!executor.isTerminated()) {

		}
		finishedAllThreads();
	}
}