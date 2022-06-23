<%@ page import = "java.io.*,java.util.*" %>

<html>
   <head>
      <title>Evaluation Results</title>
   </head>
   
   <body>
      <center>
         <h2>Evaluation Results</h2>
       
         
         fitness score: <%=session.getAttribute("fitScore") %>
         <br><br>
         <strong>Final Rating of User (0-6): null </strong><%=session.getAttribute("Rating") %>
      </center>
   
   </body>
</html>