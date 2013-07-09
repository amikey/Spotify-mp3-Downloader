package main.song_downloaders;

import javax.swing.JProgressBar;

import main.structures.SongInfo;


public class SingleSongDownloader_GUI extends SingleSongDownloader {
	private JProgressBar progressBar;
	public SingleSongDownloader_GUI(JProgressBar progressBar, SongInfo song) {
		super(song);
		this.progressBar = progressBar;
		
		// TODO Auto-generated constructor stub
	}
	protected void setCurrProgress(int n) {
		progressBar.setValue(n);
	}
	
}
