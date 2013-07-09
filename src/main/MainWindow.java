package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JProgressBar;

import main.song_downloaders.SingleSongDownloader;
import main.song_downloaders.SingleSongDownloader_GUI;
import main.structures.SongInfo;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow {

	private JFrame frmSpotifyDownloader;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmSpotifyDownloader.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSpotifyDownloader = new JFrame();
		frmSpotifyDownloader.setTitle("Spotify Downloader");
		frmSpotifyDownloader.setBounds(100, 100, 450, 127);
		frmSpotifyDownloader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpotifyDownloader.getContentPane().setLayout(new MigLayout("", "[][grow][]", "[][][]"));
		
		JLabel lblSpotifyLinkGoes = new JLabel("Spotify Link Goes Here");
		frmSpotifyDownloader.getContentPane().add(lblSpotifyLinkGoes, "cell 0 0 2 1");
		
		textField = new JTextField();
		frmSpotifyDownloader.getContentPane().add(textField, "cell 0 1 2 1,growx");
		textField.setColumns(10);
		
		final JProgressBar progressBar = new JProgressBar();
		frmSpotifyDownloader.getContentPane().add(progressBar, "cell 0 2 3 1,growx");
		
		final JButton btnNewButton = new JButton("Download");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnNewButton.invalidate();
				String spotifyURL = textField.getText();
				SongInfo song = null;
				//http://open.spotify.com/track/5dTHtzHFPyi8TlTtzoz1J9 ---helena mcr
				//http://open.spotify.com/track/1UX6IpGYtIOP7J40NeY5pp ---blink 182
				//http://open.spotify.com/track/1GErReAT0swX36x3O8GyQn -- to the end mcr
				//http://open.spotify.com/track/4VbDJMkAX3dWNBdn3KH6Wx -- helena beat foster the people
				try {
					song = SpotifyDownloader.getSongDataForSpotifyURLWithTries(3, spotifyURL);
					SingleSongDownloader md = new SingleSongDownloader_GUI(progressBar, song);
					new Thread(md).start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		frmSpotifyDownloader.getContentPane().add(btnNewButton, "cell 2 1");
	}

}
