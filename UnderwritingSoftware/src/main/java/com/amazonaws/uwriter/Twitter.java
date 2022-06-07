package com.amazonaws.uwriter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Twitter {

	public void extract(String user) throws TwitterException, IOException, SQLException, ClassNotFoundException {

		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setDebugEnabled(true).setOAuthConsumerKey("7HVJ84Cqpc4gWvMjSimUUQXxp")
				.setOAuthConsumerSecret("1Rw9vhXTKD0p3H4HBA61yeweUy4KeFUMs4ZripOvrzdiaP7Vl3")
				.setOAuthAccessToken("1414458680888623108-6f54bTxZUQRaeW9FaOsYUc0kN7zjXU")
				.setOAuthAccessTokenSecret("Z7Qw8lHhuNsvzoKF0E8IIgD4DEoZo1pEmpsXLDyD8N6tu");

		TwitterFactory tf = new TwitterFactory(configurationBuilder.build());
		twitter4j.Twitter twitter = tf.getInstance();
		int page = 1;

		int counter = 0;

		
		
		String mysqlUrl = "jdbc:mysql://localhost/underwriter";
		Connection con = DriverManager.getConnection(mysqlUrl, "root", "Cathjp@273");

		
		while (page <= 3) {
			Paging paging = new Paging(page, 20);
			List<Status> userStatus = twitter.getUserTimeline(user, paging);

			List<String> userPhotos = new ArrayList<String>();

			System.out.println("Showing @" + user + "'s user timeline.");
			for (Status stObj : userStatus) {
				if (stObj.isRetweet())
					continue;
				else {
					MediaEntity[] media = stObj.getMediaEntities();
					for (MediaEntity m : media) {
						userPhotos.add(m.getMediaURL());
					}

					System.out.println("@" + stObj.getUser().getScreenName() + " - " + stObj.getText());

				}
			}

			for (String str : userPhotos) {
				URL url = new URL(str);
				String fileName = url.getFile();
				BufferedImage img = ImageIO.read(url);
				
				int scaledWidth = (int) (img.getWidth() * 0.7);
		        int scaledHeight = (int) (img.getHeight() * 0.7);
		        
				// resizing
				BufferedImage resizedImg = new BufferedImage(scaledWidth,
		                scaledHeight, img.getType());
				
				Graphics2D g2d = resizedImg.createGraphics();
		        g2d.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
		        g2d.dispose();
		        
				String fileType = fileName.substring(fileName.lastIndexOf(".")).substring(1);
				
				File file = new File(
						"C:\\Users\\HP\\git\\SmartUnderwriter\\UnderwritingSoftware\\pictures" + fileName.substring(fileName.lastIndexOf("/")));
				ImageIO.write(resizedImg, fileType, file);

				String filePath = file.getAbsolutePath();
				System.out.println(filePath);
				PreparedStatement pstmt = con.prepareStatement("INSERT INTO images VALUES(?, ?, ?)");
				pstmt.setInt(1, counter);
				counter++;
				// Inserting Blob type
				InputStream input = new FileInputStream(filePath);
				pstmt.setBlob(2, input);
				pstmt.setString(3, fileName.substring(fileName.lastIndexOf("/")).substring(1));
				pstmt.execute();

			}

			page++;
		}

		System.out.println("done, and uploaded to db");

	}

}
