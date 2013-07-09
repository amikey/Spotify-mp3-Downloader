package other;

import java.io.IOException;

public class Start {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	//TODO concurrency! otherwise very slow
	//Use additional sources..
	//1. http://mp3juices.com/search/my-chemical-romance-helena
	//2. http://www.mrtzcmp3.net/helena_1s.html
	//3. http://mp3skull.com/mp3/my_chemical_romance_helena.html
	//4. YouTube
	
	public static void main(String[] args) throws IOException {
		SPlaylist playlist = new SPlaylist("/Users/Manu/playlist.txt");
		for(int i=0; i<playlist.songs.size(); i++) {
			System.out.print("Started downloading file "+i+"...");
			Downloader.getFileForSong(playlist.songs.get(i));
			System.out.println("DONE");
		}
	}

}
