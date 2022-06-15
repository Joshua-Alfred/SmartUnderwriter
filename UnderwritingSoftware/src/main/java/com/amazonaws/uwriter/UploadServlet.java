package com.amazonaws.uwriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Collection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;

class StatusPage extends HttpServlet implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpServletResponse response;

	public StatusPage(HttpServletRequest request, HttpServletResponse response) {
		this.response = response;
		this.request = request;
	}

	public void run() {
		try {
			RequestDispatcher dd = request.getRequestDispatcher("AutoRefresh.jsp");
			dd.forward(request, response);
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ServletException e) {

			e.printStackTrace();
		}
	}
}

@WebServlet("/Uploader")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Thread object = new Thread(new StatusPage(request, response));
		object.start();

		// google cloud form parser credentials
		String projectId = "597732289824";
		String location = "us";
		String processerId = "d98a23cddf118abe";

		String name = String.format("projects/%s/locations/%s/processors/%s", projectId, location, processerId);

		Part filePart = request.getPart("pdf");
		String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		out.println(fileName);
		InputStream fileContent = filePart.getInputStream();

		// converting to ByteArray
		int nRead;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[16384];

		while ((nRead = fileContent.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		byte[] fileBytes = buffer.toByteArray();

		buffer.close();

		response.sendRedirect(request.getContextPath() + "/AutoRefresh.jsp");

		try {
			DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create();

			// Convert the image data to a Buffer and base64 encode it.
			ByteString content = ByteString.copyFrom(fileBytes);

			RawDocument document = RawDocument.newBuilder().setContent(content).setMimeType("application/pdf").build();

			// Configure the process request.
			ProcessRequest parserequest = ProcessRequest.newBuilder().setName(name).setRawDocument(document).build();

			// Recognizes text entities in the PDF document
			ProcessResponse result = client.processDocument(parserequest);
			com.google.cloud.documentai.v1.Document documentResponse = result.getDocument();

			String text = documentResponse.getText();

			out.println("Document processing complete.");

			Collection<com.google.cloud.documentai.v1.Document.Page> pages = documentResponse.getPagesList();
			System.out.printf("There are %s page(s) in this document.\n", pages.size());

			for (com.google.cloud.documentai.v1.Document.Page page : pages) {
				Collection<com.google.cloud.documentai.v1.Document.Page.FormField> formFields = page
						.getFormFieldsList();
				System.out.printf("Found %d form fields:\n", formFields.size());
				for (com.google.cloud.documentai.v1.Document.Page.FormField formField : formFields) {
					String fieldName = getLayoutText(formField.getFieldName().getTextAnchor(), text);
					String fieldValue = getLayoutText(formField.getFieldValue().getTextAnchor(), text);
					System.out.printf("    * '%s': '%s'\n", removeNewlines(fieldName), removeNewlines(fieldValue));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		out.println("extracting user's images from twitter");
		Twitter obj = new Twitter(); // parallel from fb api and wearable api
		FaceMatching obj2 = new FaceMatching();
		// InstaConnect obj3 = new InstaConnect();
		try {
			obj.extract("tomcruise");
			// obj3.InstaExtract("tomcruise");//assume extracted from document
			out.println("extracted and uploaded to db");
			obj2.imageValidate();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			out.println("validation partially complete");
		}

//		response.sendRedirect(request.getContextPath() + "/ImageDisp.jsp");

	}

	private static String getLayoutText(com.google.cloud.documentai.v1.Document.TextAnchor textAnchor, String text) {
		if (textAnchor.getTextSegmentsList().size() > 0) {
			int startIdx = (int) textAnchor.getTextSegments(0).getStartIndex();
			int endIdx = (int) textAnchor.getTextSegments(0).getEndIndex();
			return text.substring(startIdx, endIdx);
		}
		return "[NO TEXT]";
	}

	private static String removeNewlines(String s) {
		return s.replace("\n", "").replace("\r", "");
	}

}