package main.songdownloader;

import javax.swing.JProgressBar;

import main.structures.SongDataHolder;


public class MasterDownloaderGUI extends MasterDownloader {
	private JProgressBar progressBar;
	public MasterDownloaderGUI(JProgressBar progressBar, SongDataHolder song) {
		super(song);
		this.progressBar = progressBar;
		
		// TODO Auto-generated constructor stub
	}
	protected void setCurrProgress(int n) {
		progressBar.setValue(n);
	}
	
}
