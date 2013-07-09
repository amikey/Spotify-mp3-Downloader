package main.song_downloaders;

import javax.swing.JProgressBar;

public class SingleSongDownloader_GUI extends SingleSongDownloader {
	private JProgressBar progressBar;
	public SingleSongDownloader_GUI(JProgressBar progressBar, String spotifyLink) {
		super(spotifyLink);
		this.progressBar = progressBar;		
	}
	protected void setCurrProgress(int n) {
		progressBar.setValue(n);
	}
}
