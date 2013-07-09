package main.song_downloaders;

public class SingleSongDownloader_Concurrent extends SingleSongDownloader {
	MultiSongDownloader_Queue sq;
	public SingleSongDownloader_Concurrent(MultiSongDownloader_Queue sq, String spotifyLink) {
		super(spotifyLink);
		this.sq = sq;
	}
	@Override
	public void successfulDownload() {
		sq.successfulDownload(song);
	}
	@Override
	public void failedDownload() {
		sq.failedDownload(spotifyLink, song);
	}

}
