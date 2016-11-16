<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:basic_page>
<div id="fb-root"></div>
<script>
	function fbLogin() {
		FB.login(function(response) {
		   if (response.authResponse) {
		     FB.api('/me', function(response) {
				document.getElementById('inputFirstName').value=response.first_name;
				document.getElementById('inputLastName').value=response.last_name;
				document.getElementById('inputEmail').value=response.email;
				document.getElementById('facebookId').value=response.id;
		     });
		   }
		});	
	}
	
	window.fbAsyncInit = function() {
		FB.init({
			appId : '434772353234899', // App ID
			channelUrl : '//www.trivolous.com/channel.htm', // Channel File
			status : true, // check login status
			cookie : true, // enable cookies to allow the server to access the session
			xfbml : true
		// parse XFBML
		});
	    //FB.Event.subscribe('auth.statusChange', fbLoginCheck);		
	};
	// Load the SDK Asynchronously
	(function(d) {
		var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
		if (d.getElementById(id)) {
			return;
		}
		js = d.createElement('script');
		js.id = id;
		js.async = true;
		js.src = "//connect.facebook.net/en_US/all.js";
		ref.parentNode.insertBefore(js, ref);
	}(document));
</script>

<div class="container">
	<div class="row-fluid">
		<div class="span4">
			<c:if test="${!empty message}">
				<div class="alert">
				  ${message}
				</div>				
			</c:if>
			<form class="form-horizontal" action="registration.html" method="post">
			  <input type="hidden" name="gameid" value="${param.gameid}" />
			  <input type="hidden" name="gamecode" value="${param.gamecode}" />
			  <input type="hidden" name="fbid" value="" />		
			  <fieldset>
			    <legend>Member Registration</legend>

			    <div class="control-group">
			      <label class="control-label" for="inputFirstName">First Name</label>
			      <div class="controls">
					<input name="firstName" class="input-xlarge" id="inputFirstName"/>
			      </div>
			    </div>

			    <div class="control-group">
			      <label class="control-label" for="inputLastName">Last Name</label>
			      <div class="controls">
					<input name="lastName" class="input-xlarge" id="inputLastName"/>
			      </div>
			    </div>

			    <div class="control-group">
			      <label class="control-label" for="inputEmail">Email</label>
			      <div class="controls">
					<input name="email" class="input-xlarge" id="inputEmail" 
						<c:if test="${!empty param.email}">value="${param.email}" readonly </c:if> />
			      </div>
			    </div>

			    <div class="control-group">
			      <label class="control-label" for="input04">Password</label>
			      <div class="controls">
					<input type="password" name="password" class="input-xlarge" id="input04"/>
			      </div>
			    </div>

			    <div class="control-group">
			      <label class="control-label" for="input04">Re-enter Password</label>
			      <div class="controls">
					<input type="password" name="password2" class="input-xlarge" id="input04"/>
			      </div>
			    </div>

				<p class="terms">
                By registering as a member, you agree to our <a href="terms.html" title="Link to Terms and Conditions" target="_blank">terms &amp; conditions</a>.
                </p>

				<div class="form-actions">
            		<button type='submit' name='action' class='btn btn-primary'>Register</button>
					<a class="btn" onclick="fbLogin()">Fill In via Facebook</a>
          		</div>

			  </fieldset>
			</form>
		</div>
	</div>
</div>
</t:basic_page>