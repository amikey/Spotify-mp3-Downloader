package main.song_downloaders;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import main.listing_sources.LS_Dilandau;
import main.listing_sources.LS_MP3Skull;
import main.listing_sources.ListingSource;
import main.structures.DownloadData;
import main.structures.SongDownloadListing;
import main.structures.SongInfo;

import org.cmc.music.common.ID3ReadException;
import org.cmc.music.myid3.*;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.util.Debug;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;


public class SingleSongDownloader implements Runnable {
	private ArrayList<ListingSource> childrenDownloaders = new ArrayList<ListingSource>();
	protected SongInfo song;
	public SingleSongDownloader(SongInfo song) {
		this.song=song;
		try {
			ListingSource sdl1 = new LS_Dilandau(song);
			childrenDownloaders.add(sdl1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ListingSource sdl2 = new LS_MP3Skull(song);
		childrenDownloaders.add(sdl2);
		//Does not work
//		SongDownloader sdl3 = new SD_Mrtzcmp3(song);
//		childrenDownloaders.add(sdl3);
	}
	public void start() {
		System.out.println("starting to download: "+song);
		for(int i=0; i<childrenDownloaders.size(); i++) {
			childrenDownloaders.get(i).start();
		}
	}
	private DownloadData popBestDownloadData() throws IOException {
		for(int i=childrenDownloaders.size()-1; i>=0; i--) {
			if (childrenDownloaders.get(i).downloadListingHeap.isEmpty()) {
				childrenDownloaders.remove(i);
			}
		}
		if (childrenDownloaders.size() == 0) throw new IOException("NO DOWNLOAD LINKS");
		
		int bestSDL = 0;
		SongDownloadListing dl = childrenDownloaders.get(0).peakBestDownload();
		for(int i=1; i<childrenDownloaders.size(); i++) {
			SongDownloadListing dl2 = childrenDownloaders.get(i).peakBestDownload();
			if (dl.compareTo(dl2) < 1) {
				dl = dl2;
				bestSDL = i;
			}
		}
		SongDownloadListing bestDL = childrenDownloaders.get(bestSDL).popBestDownload();
		DownloadData dd = childrenDownloaders.get(bestSDL).getDownloadData(bestDL);
		return dd;
	}
	public boolean download(String filepath) { 
		int timesTried = 0;
		int maxTries = 5;
		boolean success = false;
		DownloadData dd = null;
		while (!success && timesTried < maxTries) {
			try {
				dd = popBestDownloadData();
				//dd is initialized if error is not caught
				InputStream input = dd.input;
				File f=new File(filepath);
				//f.getParentFile().mkdirs(); //TODO this line will add folders that dont exist, but will crash if no folders needed
				OutputStream out=new FileOutputStream(f);
				byte buf[]=new byte[1024];
				int len;
				
				int maxCopyTimes = dd.size/1024; 
				int copyTime = 0;
				int markersPrinted = 0;
				int maxMarkers = 100;
				
				int times = 0;
				
				maxMarkers--;
				while((len=input.read(buf))>0) {
					out.write(buf,0,len);
					double percentage = (double)copyTime / maxCopyTimes * 100;
					setCurrProgress((int)percentage);
					double currPercentage = (double)markersPrinted / maxMarkers * 100;
					if (times % 1024 == 0) {
						//System.out.println("*");
					}
					times++;
				}
				out.close();
				input.close();
				
//				MP3File mp3file = new MP3File(f);
//				if (mp3file != null) {
//					if (mp3file.getID3v1Tag() != null) {
//						String title = mp3file.getID3v1Tag().getTitle();
//						String artist = mp3file.getID3v1Tag().getArtist();
//						String album = mp3file.getID3v1Tag().getAlbum();
//					}
//					if (mp3file.getID3v2Tag() != null) {
//						String title2 = mp3file.getID3v2Tag().getSongTitle();
//						String artist2 = mp3file.getID3v2Tag().getLeadArtist();
//						String album2 = mp3file.getID3v2Tag().getAlbumTitle();
//					}
//				}
				success = true;
			} catch (IOException e) {
				timesTried++;
				System.out.println("error downloading, trying again: "+song);
				//e.printStackTrace();
			}
		}
		return success;
	}
	
	protected void setCurrProgress(int percentage) {}
	@Override
	public void run() {
		start();
		download(song.artist + "-" + song.album + "-" + song.title + ".mp3"); 
	}
}
