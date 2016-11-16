<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="">
<title>Trivolous</title>
</head>

<body>
<h1>Signing in...</h1>
<p id="msg"></p>

<script	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<script>

function signin(response) {
	// Logged into your app and Facebook.
	console.log('fbsignin');
	$.ajax({
	  	type: "POST",
	  	url: "/fbsignon",
		data: {accessToken: response.authResponse.accessToken},
		success: function(msg) {
			if (msg.indexOf("success") > -1) {
	   			// redirect to game
	   			console.log('redirecting to game');
	   			window.location.href = "/member/gameList";	
	   		} else {
	   			console.log('Trivolous loging failed :'+msg);
	   			$('#msg').html("Trivolous login failed : " + msg);
	   		}			
		}
	}); 			 
}

function register(response) {
	// Logged into your app and Facebook.
	console.log('fbregister');
	$.ajax({
	  	type: "POST",
	  	url: "/fbregister",
  		data: {accessToken: response.authResponse.accessToken},
		success: function(msg) {
			if (msg.indexOf("success") > -1) {
	   			// redirect to game
	   			console.log('redirecting to game');
	   			window.location.href = "/member/gameList";	
	   		} else {
	   			console.log('Trivolous register failed :'+msg);
	   			$('#msg').html("Trivolous login failed : " + msg);
	   		}			
		}
	}); 			 
}

	// FACEBOOK
	window.fbAsyncInit = function() {
		FB.init({
		  appId      : '434772353234899',
		  cookie     : true,  // enable cookies to allow the server to access 
		                      // the session
		  xfbml      : true,  // parse social plugins on this page
		  version    : 'v2.2' // use version 2.2
		});

		FB.getLoginStatus(function(response) {
		    console.log('init statusChangeCallback');
		    console.log(response);
		    if (response.status === 'connected') {
				signin(response);
		    } 
		    else {
		    	FB.login(function(response) {
				    console.log('LoginCallback');
				    console.log(response);
				    if (response.status === 'connected') {
						register(response);
				    }
				    else {
			   			$('#msg').html("Bad user state after login: " + response.status);
				    }
		    	}, {scope: 'email'});
		    }
		});

	};  

  // Load the SDK asynchronously
  (function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));
  

</script>
</body>
</html>
