<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:member_page>
	<jsp:body>

<div class="container" id="main">


	<section class="row theme-game-content clearfix">

		<div class="container-fluid">
			<div class="col-md-12"> 
	
				<h1>User Profile</h1>

				<div class="row">
					<div class="col-xs-8 col-sm-7 col-md-7"> 

						<form:form  id="frm1" method="post" commandName="memberData">
						  <fieldset>

							<div class="form-group">
							  <label class="control-label" for="input01">First Name</label>
							  <div class="controls">
								<form:input cssClass="form-control" path="firstName" id="input01" />
							  </div>
							</div>

							<div class="form-group">
							  <label class="control-label" for="input02">Last Name</label>
							  <div class="controls">
								<form:input cssClass="form-control" path="lastName" id="input02" />
							  </div>
							</div>

							<div class="form-group">
							  <label class="control-label" for="input03">Email</label>
							  <div class="controls">
								<form:input cssClass="form-control" path="email"  id="input03" />
							  </div>
							</div>

							<div class="form-group">
							  <label class="control-label" for="input04">Password</label>
							  <div class="controls">
							  <c:choose>
								<c:when test="${memberData.password == 'Facebook Only'}">
										<button id="showPassword" type="button" class='btn btn-default btn-sm'>Enable Email/Password Login</button>
										<form:input cssClass="form-control hidden" path="password"  id="input04"  />
								</c:when>
								<c:otherwise>
										<form:input cssClass="form-control" path="password"  id="input04" />
								</c:otherwise>	
						       </c:choose>		
							  </div>
							</div>

							<div class="form-group">
							  <label class="control-label" for="input03">Facebook</label>
							  <div class="controls">
								<form:hidden path="facebookId" id="inputFbid"/>
								<c:choose>
										<c:when test="${empty memberData.facebookId}">
											<button id="fbbut" type='button' class='btn btn-default btn-sm'>Associate with Facebook</button>
										</c:when>
										<c:otherwise>
											<p>Associated with Facebook</p>
										</c:otherwise>	
								</c:choose>		
									  </div>
							</div>

	
				

						  </fieldset>
				  		  <hr>
							<div class="form-actions">
								<button type='submit' name='action' class='btn btn-primary'>Save changes</button>
								<!-- <button type='submit' name='_cancel' class='btn'>Cancel</button> -->
							</div>		  
				  
						</form:form >
			
					</div> <!-- .col-md-* -->
					
					
					<div class="col-xs-4 col-sm-5 col-md-5">

						<div class="form-group">
							<c:set var="avatarSrc" value="${memberData.imageUrl}" />
							<c:if test="${empty avatarSrc}">
								<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
							</c:if>
							<img class="avatar-lg inline-avatar" src="${avatarSrc}" alt="" width="100" height="100">
						</div>
					
					</div> <!-- .col-md-* -->
				</div> <!-- row -->
				
			</div> <!-- .col-md-* -->
			
		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	


</div> <!-- #main -->

<script>
  window.fbAsyncInit = function() {
	  FB.init({
	    appId      : '434772353234899',
	    cookie     : true,  // enable cookies to allow the server to access 
	                        // the session
	    xfbml      : true,  // parse social plugins on this page
	    version    : 'v2.2' // use version 2.2
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

  
	function fblogin(onAccessToken) {
		FB.getLoginStatus(function(response) {
			    console.log('statusChangeCallback');
			    console.log(response);
			    // The response object is returned with a status field that lets the
			    // app know the current login status of the person.
			    // Full docs on the response object can be found in the documentation
			    // for FB.getLoginStatus().
			    if (response.status === 'connected') {
			      // Logged into your app and Facebook.
			      onAccessToken(response.authResponse.userID);
			    } else { 
			      // The person is logged into Facebook, but not your app.  Or not logged into FB.
			    	FB.login(function(response) {
					    console.log('LoginCallback');
					    console.log(response);
				        if (response.status === 'connected') {
				          // Logged into your app and Facebook.
				          onAccessToken(response.authResponse.userID);
				        }
			    	}, {scope: 'email'});
			    }
		});
	}	

	function fbassoc(userid) {
	    console.log('fbassoc id='+ userid);
 		$('#inputFbid').val(userid);
 		$('#frm1').submit();
	}
	
	$("#fbbut").click(function() {
		   console.log('fbbut click');
		   fblogin(fbassoc);
	   });
	
	$('#showPassword').click(function() {
		$(this).addClass('hidden');		
		$('#input04').val('');
		$('#input04').removeClass('hidden');
	});
  
</script>  





</jsp:body>
</k:member_page>