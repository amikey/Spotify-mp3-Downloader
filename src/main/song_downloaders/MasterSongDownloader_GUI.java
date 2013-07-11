package main.song_downloaders;

import javax.swing.JProgressBar;

import main.PlayListDownloaderWindow;
import main.structures.SongInfo;

public class MasterSongDownloader_GUI extends MasterSongDownloader implements Runnable {
	PlayListDownloaderWindow window;
	JProgressBar progressBar;
	int totalSongs = 0;
	public MasterSongDownloader_GUI(PlayListDownloaderWindow window, String songsListFilepath, String logFilepath, int maxConcurrentDownloads) {
		super(songsListFilepath, logFilepath, maxConcurrentDownloads);
		this.progressBar = window.progressBar;
		this.window = window;
	}
	public void createAndExecuteThreads() {
		while (!spotifyLinkQueue.isEmpty()) {
			String spotifyURL = this.spotifyLinkQueue.poll();
			SingleSongDownloader ssd = new SingleSongDownloader_Concurrent(this, spotifyURL);
			executor.execute(ssd);
		}
		executor.shutdown();
	}
	public void successfulDownload(SongInfo song) {
		super.successfulDownload(song);
		//double percentage = ((double)numDone) / ((double) totalSongs);
		setCurrProgress(numDone);
	}
	public void failedDownload(String spotifyLink, SongInfo song) {
		super.failedDownload(spotifyLink, song);
		//double percentage = ((double)numDone) / ((double) totalSongs);
		setCurrProgress(numDone);
	}
	
	protected void setCurrProgress(int n) {
		progressBar.setValue(n);
		progressBar.repaint();
		progressBar.validate();
	}
	
	public void startDownloadingAllSongs() {
		queueAllSpotifyLinks();
		totalSongs = this.spotifyLinkQueue.size();
		progressBar.setMaximum(totalSongs);
		createAndExecuteThreads();
		while (!executor.isTerminated()) {

		}
		finishedAllThreads();
	}
	@Override
	public void run() {
		window.btnNewButton.setEnabled(false);
		//window.btnNewButton.repaint();
		//window.btnNewButton.invalidate();
		startDownloadingAllSongs();
		window.btnNewButton.setEnabled(true);
		//window.btnNewButton.repaint();
		//window.btnNewButton.invalidate();
	}
}
