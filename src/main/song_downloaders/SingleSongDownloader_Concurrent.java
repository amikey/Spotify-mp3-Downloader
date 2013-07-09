package main.song_downloaders;

public class SingleSongDownloader_Concurrent extends SingleSongDownloader {
	MasterSongDownloader sq;
	public SingleSongDownloader_Concurrent(MasterSongDownloader sq, String spotifyLink) {
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
