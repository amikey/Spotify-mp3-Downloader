package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.JButton;

import main.song_downloaders.MasterSongDownloader;
import main.song_downloaders.MasterSongDownloader_GUI;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PlayListDownloaderWindow {

	private JFrame frmSpotifyPlaylistDownloader;
	private JTextField logFileTextField;
	private JTextField playlistFileTextField;
	public JProgressBar progressBar;
	public JButton btnNewButton;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PlayListDownloaderWindow window = new PlayListDownloaderWindow();
					window.frmSpotifyPlaylistDownloader.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PlayListDownloaderWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		final PlayListDownloaderWindow self = this;
		
		frmSpotifyPlaylistDownloader = new JFrame();
		frmSpotifyPlaylistDownloader.setTitle("Spotify Playlist Downloader");
		frmSpotifyPlaylistDownloader.setBounds(100, 100, 540, 138);
		frmSpotifyPlaylistDownloader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSpotifyPlaylistDownloader.getContentPane().setLayout(null);
		
		logFileTextField = new JTextField();
		logFileTextField.setBounds(112, 39, 278, 28);
		frmSpotifyPlaylistDownloader.getContentPane().add(logFileTextField);
		logFileTextField.setColumns(10);
		
		playlistFileTextField = new JTextField();
		playlistFileTextField.setBounds(112, 11, 278, 28);
		frmSpotifyPlaylistDownloader.getContentPane().add(playlistFileTextField);
		playlistFileTextField.setColumns(10);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(21, 79, 498, 20);
		frmSpotifyPlaylistDownloader.getContentPane().add(progressBar);
		
		JLabel lblPlaylistFile = new JLabel("Playlist File");
		lblPlaylistFile.setBounds(21, 17, 108, 16);
		frmSpotifyPlaylistDownloader.getContentPane().add(lblPlaylistFile);
		
		JLabel lblLogFile = new JLabel("Log File");
		lblLogFile.setBounds(21, 45, 61, 16);
		frmSpotifyPlaylistDownloader.getContentPane().add(lblLogFile);
		
		btnNewButton = new JButton("Start Download");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String playlistFileLocation = playlistFileTextField.getText();
				String logFileLocation = logFileTextField.getText();
				int concurrentDownloads = 10;
				if (logFileLocation.isEmpty()) {
					logFileLocation = playlistFileLocation.replaceFirst(".txt", "--LOG.txt"); 
				}
				MasterSongDownloader_GUI msd = new MasterSongDownloader_GUI(self, playlistFileLocation, logFileLocation, concurrentDownloads);
				Thread t = new Thread(msd);
				t.start();
				
			}
		});
		btnNewButton.setBounds(402, 11, 117, 56);
		frmSpotifyPlaylistDownloader.getContentPane().add(btnNewButton);
	}
}
