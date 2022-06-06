function validate(){
	var username=document.getElementById("username").value;
	var password=document.getElementById("password").value;
	if(username=="admin" && password=="admin@123"){
		 window.open("DocUpload.html");
		return false;
		
		//TODO: take to main page
		
	}
	else{
		alert("wrong credentials, try again!");
	}
}