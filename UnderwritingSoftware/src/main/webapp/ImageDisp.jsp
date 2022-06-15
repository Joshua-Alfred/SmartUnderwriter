<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    

<%@page import="java.sql.Blob"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.apache.commons.codec.binary.Base64"%>

<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="Gallery.css" type="text/css">
<script src="grid.js"></script>
<meta charset="ISO-8859-1">
<title>Displaying</title>
</head>
<body>


<div class="row">
  <div class="column">
<%

try { 
	
	String mysqlUrl = "jdbc:mysql://localhost/underwriter";
	Connection con = DriverManager.getConnection(mysqlUrl, "root", "Cathjp@273");
    PreparedStatement ps = con.prepareStatement("select * from images");
    ResultSet rs = ps.executeQuery();
    int i=1;
   
    
    ServletOutputStream os = response.getOutputStream();
    
    while(rs.next()) {
        Blob blob = rs.getBlob("image");
        String name  = rs.getString("imageName");
        byte byteArray[] = blob.getBytes(1, (int) blob.length());
        
        String encodedImg = Base64.encodeBase64String(byteArray);
        
        response.setContentType("image/jpeg");
        
        
        os.write(byteArray);
        os.flush();
        
        
        
  
    }
    os.close();
   
} catch (Exception e) {
    out.println(e);
}

%>


  </div>
</div>

</body>
</html>