package com.amazonaws.uwriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;

public class FaceMatching {

	public void imageValidate() throws Exception {
		int imageScore = 0;
		
		Float similarityThreshold = 70F;
		String sourceImage = "C:/Users/HP/Desktop/tomm.jpg";

		Regions clientRegion = Regions.AP_SOUTH_1;
		ByteBuffer sourceImageBytes = null;
		ByteBuffer targetImageBytes = null;

		String mysqlUrl = "jdbc:mysql://localhost/underwriter";
		Connection con = DriverManager.getConnection(mysqlUrl, "root", "Cathjp@273");
		Statement stmt = con.createStatement();

		AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder.standard().withRegion(clientRegion)
				.build();

		// Load source and target images and create input parameters

		InputStream inputStream = new FileInputStream(new File(sourceImage));
		sourceImageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
		Image source = new Image().withBytes(sourceImageBytes);

		ResultSet rs = stmt.executeQuery("select * from images");
		while (rs.next()) {
			int id = rs.getRow();
			int compare;
			boolean isGliding = false, isSmoking = false, isDrinking = false;
			String name = rs.getString(3);
			System.out.println(id + " -> " + name);
			Blob blob = rs.getBlob("image");
			byte byteArray[] = blob.getBytes(1, (int) blob.length());
			targetImageBytes = ByteBuffer.wrap(byteArray);

			Image target = new Image().withBytes(targetImageBytes);

			CompareFacesRequest request = new CompareFacesRequest().withSourceImage(source).withTargetImage(target)
				.withSimilarityThreshold(similarityThreshold);

			try {
				// Call operation
				CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);

				// Display results
				List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
				if (faceDetails.isEmpty()) {
					continue;
				} else {
					System.out.println("face detected, proceeding to label detection");

					DetectLabelsRequest labelrequest = new DetectLabelsRequest()
							.withImage(new Image().withBytes(targetImageBytes)).withMaxLabels(10)
							.withMinConfidence(77F);
					DetectLabelsResult result = rekognitionClient.detectLabels(labelrequest);
					List<Label> labels = result.getLabels();

					for (Label label : labels) {
						 System.out.println(label.getName() + ": " +
						 label.getConfidence().toString());

//						compare = Float.compare(label.getConfidence(), 50f);
//
//						if (compare > 0) {
//
//							if (label.getName().equalsIgnoreCase("gliding")
//									|| label.getName().equalsIgnoreCase("parachute")) { 
//								
//								imageScore += 10;
//
//							} 
//							
//							else if (label.getName().equalsIgnoreCase("smoking")) { 
//
//								isSmoking = true;
//							}
//
//							else if (label.getName().equalsIgnoreCase("bar counter")
//									|| label.getName().equalsIgnoreCase("alcohol")
//									|| label.getName().equalsIgnoreCase("beer")
//									|| label.getName().equalsIgnoreCase("liquor")) {
//								isDrinking = true;
//							}
//						}
					}
				}
				
				System.out.println("is user involved in paragliding: " + isGliding);
				System.out.println("is user involved in Smoking: " + isSmoking);
				System.out.println("is user involved in Drinking: " + isDrinking);
				
			}catch(com.amazonaws.services.rekognition.model.InvalidParameterException ex) {
				System.out.println("no face present in image.");
				
			}catch (Exception e) {
			
				e.printStackTrace();
			} finally {
				System.out.println("---------------------------------------------");

			}

		}

}

}
