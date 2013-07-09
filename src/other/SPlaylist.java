package other;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SPlaylist {
	public ArrayList<Song> songs;
	public SPlaylist(String filename) {
		Scanner scanner;
		songs = new ArrayList<Song>();
		try {
			scanner = new Scanner(new File(filename));
		} catch (FileNotFoundException e) {
			return;
		}
		while(scanner.hasNextLine()) {
			String spotifySongURL = scanner.nextLine();
			if (!spotifySongURL.substring(24, 29).equals("local")) {
				Song s;
				try {
					s = new Song(spotifySongURL);
					songs.add(s);
				} catch (IOException e) {
					System.out.println("* SPOFITY URL ERROR *");
				}
			}
		}
	}
}
