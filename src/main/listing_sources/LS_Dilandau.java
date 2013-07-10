package main.listing_sources;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.NoSuchElementException;



import main.structures.SongDownloadListing;

import main.structures.SongInfo;

import org.jsoup.Connection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class LS_Dilandau extends ListingSource {
	private static String BASE_URL = "http://en.dilandau.eu/download-songs-mp3/";
	private static String URL_END = "/1.html";
	
	public LS_Dilandau(SongInfo song) {
		super(song);
	}

	@Override
	public void generateListings() {
		Document page = null;
		int num_cells = 0;
		try {
			String initURL = getInitURLForSong(song);
			page = loadPage(initURL);
			num_cells = getTotalCells(page);
		} catch (IOException e) {
			//error getting num_cells
			return;
		} catch (Exception e) {
			//error formatting url or error loading page
			return;
		}
		for (int i=0; i<num_cells; i++) {
			try {
				Element cell = getCell(page, i);
				String listingID = getListingID(cell);
				Connection conn = getDownloadConnection(cell);
				SongDownloadListing sdl = new SongDownloadListing(song, listingID, conn);
				if (!sdl.shouldReject()) {
					downloadListingHeap.add(sdl);
				}
			} catch (NoSuchElementException e) {
				//could not find cell at index
				continue;
			} catch (IOException e) {
				//error in parsing
				continue;
			}
		}
	}
		

	@Override
	String getInitURLForSong(SongInfo song) throws Exception {
		String temp = song.artist+" "+song.title;
		String urlEncoded = URLEncoder.encode(temp, "UTF-8");
		String songString = urlEncoded.replaceAll("\\+", "%20");
		return BASE_URL+songString+URL_END;
	}

	@Override
	int getTotalCells(Document page) throws IOException {
		Elements downloadListingsWithFiller = page.select("table tbody tr");
		int size = downloadListingsWithFiller.size();
		return size;
	}

	@Override
	Element getCell(Document page, int index) throws NoSuchElementException, IOException {
		Elements downloadListingsWithFiller = page.select("table tbody tr");
		Element cell = downloadListingsWithFiller.get(index);
		Elements listingFields = cell.select("td");
		if (listingFields.size() > 3) {
			return cell;
		}
		else {
			throw new NoSuchElementException();
		}
	}

	@Override
	String getListingID(Element cell) throws IOException {
		Elements listingFields = cell.select("td");
		String listingID = listingFields.get(0).text();
		return listingID;
	}

	@Override
	Connection getDownloadConnection(Element cell) throws IOException {
		Elements listingFields = cell.select("td");
		Element actionCol = listingFields.get(3);
		Element downloadAnchor = actionCol.select("a[download]").first();
		String downloadURLBase = downloadAnchor.attr("href");
		String downloadURLEnd = downloadAnchor.attr("url");
		String downloadURL = downloadURLBase+downloadURLEnd;
		
		return genericConnection(downloadURL);
	}
	

}
