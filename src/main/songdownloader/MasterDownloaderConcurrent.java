package main.songdownloader;

import main.structures.SongDataHolder;

public class MasterDownloaderConcurrent extends MasterDownloader {
	SongQueue sq;
	public MasterDownloaderConcurrent(SongQueue sq, SongDataHolder song) {
		super(song);
		this.sq = sq;
	}
	@Override
	public void run() {
		start();
		boolean success = download("music/" + song.artist + "-" + song.album + "-" + song.title + ".mp3");
		if (success) sq.successfullyDownloaded(song);
		else sq.failedDownloaded(song.toString());
	}

}
