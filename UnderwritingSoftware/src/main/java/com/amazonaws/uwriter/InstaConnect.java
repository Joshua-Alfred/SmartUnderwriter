package com.amazonaws.uwriter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InstaConnect {
	public void InstaExtract(String username) throws IOException, JSONException, SQLException {
		OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS) // connect timeout
				.readTimeout(30, TimeUnit.SECONDS).build();

		String endCursor = "";

		String mysqlUrl = "jdbc:mysql://localhost/underwriter";
		Connection con = DriverManager.getConnection(mysqlUrl, "root", "Cathjp@273");

		String url = "https://instagram-scraper-2022.p.rapidapi.com/ig/posts_username/?user="+username;

		boolean has_next_page = true;
		int count = 1;
		int photocount =1;

		while (count < 4) {

			if (!endCursor.isEmpty()) {
				url = url + "&end_cursor=" + endCursor;
			}

			Request request = new Request.Builder().url(url).get()
					.addHeader("X-RapidAPI-Key", "43b7dca013mshdd80f828e71b4a9p179fa7jsn5ddd8d9f3d72")
					.addHeader("X-RapidAPI-Host", "instagram-scraper-2022.p.rapidapi.com").build();

			Response response = client.newCall(request).execute();

			String jsonData = response.body().string();

			System.out.println(jsonData);
			JSONObject Jobject = new JSONObject(jsonData);

			JSONObject nextpage = Jobject.getJSONObject("data").getJSONObject("user")
					.getJSONObject("edge_owner_to_timeline_media").getJSONObject("page_info");
			JSONArray photos = Jobject.getJSONObject("data").getJSONObject("user")
					.getJSONObject("edge_owner_to_timeline_media").getJSONArray("edges");

			has_next_page = nextpage.getBoolean("has_next_page");

			if (has_next_page)
				endCursor = nextpage.getString("end_cursor");

			System.out.println(endCursor);

			System.out.println("-----------page " + count);

			for (int i = 0; i < photos.length(); i++) {
				JSONObject jsonobject = (JSONObject) photos.getJSONObject(i).getJSONObject("node");
				System.out.println(i + "->" + jsonobject.get("display_url"));

				URL photoUrl = new URL(jsonobject.getString("display_url"));
				
				BufferedImage img = ImageIO.read(photoUrl);

				int scaledWidth = (int) (img.getWidth() * 0.7);
				int scaledHeight = (int) (img.getHeight() * 0.7);

				// resizing
				BufferedImage resizedImg = new BufferedImage(scaledWidth, scaledHeight, img.getType());

				Graphics2D g2d = resizedImg.createGraphics();
				g2d.drawImage(img, 0, 0, scaledWidth, scaledHeight, null);
				g2d.dispose();

				

				File file = new File("C:/Users/HP/git/SmartUnderwriter/UnderwritingSoftware/pictures"
						+ "/instaImg" + photocount + ".jpg");
				ImageIO.write(resizedImg,"jpg", file);

				String filePath = file.getAbsolutePath();
				System.out.println(filePath);
				try {
					PreparedStatement pstmt = con.prepareStatement("INSERT INTO images VALUES(?, ?, ?)");
					pstmt.setInt(1, i);
					// Inserting Blob type
					InputStream input = new FileInputStream(filePath);
					pstmt.setBlob(2, input);
					pstmt.setString(3, "instaImg" + photocount + ".jpg");
					pstmt.execute();
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				} catch (SQLException e) {

					e.printStackTrace();
				}
				photocount++;
			}

			count++;
			
			if (has_next_page)
				endCursor = nextpage.getString("end_cursor");
			else
				break;

			System.out.println(endCursor);
			
			
		} // end of while loop

	}
}
