package main.structures;


public class SongDownloadListing implements Comparable<SongDownloadListing> {
	public SongInfo song;
	public String listingID;
	public String downloadLink;
	public int bitrate = 0;
	public int sizeInKB = 0;
	
	public SongDownloadListing(SongInfo song, String listingID, int bitrate, int sizeInKB, String downloadLink) {
		this.song = song;
		this.listingID = listingID;
		this.bitrate = bitrate;
		this.sizeInKB = sizeInKB;
		this.downloadLink = downloadLink;
	}
	
	public String toString() {
		String temp = "{" + listingID  + ", " + downloadLink + "}\n";
		return temp;
	}
	@Override
	public int compareTo(SongDownloadListing o) {
		assert(song == o.song);
		if (getAbsPoints() > o.getAbsPoints()) {
			return 1;
		}
		else if (getAbsPoints() < o.getAbsPoints()) {
			return -1;
		}
		else { //they must be equal at this point
			int thisRelPoints = 0;
			int oRelPoints = 0;
			//if (bitrate != 0 && o.bitrate != 0) {
			if (bitrate > o.bitrate || o.sizeInKB > 12*1024) thisRelPoints+=4;
			else if (bitrate < o.bitrate || sizeInKB > 12*1024) oRelPoints+=4;
			//}
//			if (sizeInKB > o.sizeInKB) thisRelPoints+=2;
//			else if (sizeInKB < o.sizeInKB) oRelPoints+=2;
			
			return thisRelPoints - oRelPoints;
		}
	}
	
	protected int getAbsPoints() {
		int points = 0;
		String[] badWords = {"remix", "cover", "live", "mash", "dj", "mix", "edit", "customized", "customised", "instrumental", "rework"};
		String matchString = (listingID+" "+downloadLink).toLowerCase().replaceAll("\\(", " LP ").replaceAll("\\)", " RP ");
		boolean titleMatch = matchString.matches(".*?"+song.title.toLowerCase()+".*?");
		boolean artistMatch = matchString.matches(".*?"+song.artist.toLowerCase()+".*?");
		boolean doesntHaveBadWord = true;
		for(int i=0; i<badWords.length; i++) {
			if (song.title.toLowerCase().matches(".*?"+badWords[i]+".*?")) {
				continue; //if looking for a badWord, then fine
			}
			if (matchString.matches(".*?"+badWords[i]+".*?")) {
				doesntHaveBadWord = false;
				break;
			}
		}
		if(titleMatch) 			points+=32;
		if(artistMatch) 		points+=16;
		if(doesntHaveBadWord) 	points+=8;

		return points;
	}
	
	protected boolean shouldReject() {
		boolean reject = false;
		String[] badWords = {"remix", "cover", "live", "mash", "dj", "mix", "edit", "customized", "customised", "instrumental", "rework"};
		String matchString = (listingID+" "+downloadLink).toLowerCase().replaceAll("\\(", " LP ").replaceAll("\\)", " RP ");
		boolean titleMatch = matchString.matches(".*?"+song.title.toLowerCase()+".*?");
		boolean artistMatch = matchString.matches(".*?"+song.artist.toLowerCase()+".*?");
		boolean doesntHaveBadWord = true;
		for(int i=0; i<badWords.length; i++) {
			if (song.title.toLowerCase().matches(".*?"+badWords[i]+".*?")) {
				continue; //if looking for a badWord, then fine
			}
			if (matchString.matches(".*?"+badWords[i]+".*?")) {
				doesntHaveBadWord = false;
				break;
			}
		}
		if(!titleMatch
				|| !artistMatch 
				|| !doesntHaveBadWord) reject = true;
		return reject;
	}

}

//rank download listings
/*
1. song title			32
2. artist				16
3. NO {live, remix}		8
4. bitrate				4
5. size					2




*/