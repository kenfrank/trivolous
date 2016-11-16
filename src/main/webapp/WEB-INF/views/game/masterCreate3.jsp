<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:member_page>

<div id="fb-root"></div>
<div class="container" id="main">

	<ol class="list-unstyled list-inline progress-steps steps-complete">
	  <li class="active"><b>1</b> <small>Describe Your Game</small></li>
	  <li class="active"><b>2</b> <small>Make first question</small></li>
	  <li class="active"><b>3</b> <small>Invite Players</small></li>
	</ol>

	<section class="row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12">

				<!-- <header class="section-header"> -->
					<h1>Creating Game</h1>
					<h2 class="h2 text-center"><b>Step 3:</b> Invite Players</h2>
					<p class="text-center">Players may be personally invited through email, or you can share a link to allow players to join any way you wish.  If you are starting
					a game with new players its best to share a link in a personalized message by clicking the 'on' button in the shared link section below.</p>
				<!-- </header> -->

					
					
					<h3>Invite by Sharing Link <small>(The best way to invite new players. Click on to activate.)</small></h3>
					<div class="well">
						<div class="form-group form-inline">
						<div class="btn-group" role="group">
						  <button type="button" id="linkOn" class="btn btn-default btn-sm">On</button>
						  <button type="button" id="linkOff" class="btn btn-default btn-sm active">Off</button>
						</div>
						</div>
						<div id="shareLinkForm" class="hidden">
							<p> 
							Select the maximum players that you wish to play the game, and then invite them 
							by copying the link below and sharing it in an email, in Facebook or a text.</p>
							<div class="form-group form-inline">
								<label for="maxPlayers">Number of Players</label>
							<select id="maxPlayers">
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5" selected>5</option>
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
										<option value="11">11</option>
										<option value="12">12</option>
							</select>
							</div>
							<div class="form-group form-inline">
								<label for="shared-game-link"><span class="glyphicon glyphicon-link"></span> Share Link</label>
								<c:set var="req" value="${pageContext.request}" />
								<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}/openRegister" />
								<c:set var="sharedLink" value=""></c:set>
								<input id="shared-game-link" readonly type="text" 
									value="<c:url value="${baseURL}"><c:param name="key" value="${regkey}"/></c:url>">
							</div> <!-- .form-group -->					
							<p>For you convenience you can use the button below to send the link to your Facebook friends</p>
							<div class="fb-send" 
								data-href="<c:url value="${baseURL}"><c:param name="key" value="${regkey}"/></c:url>"></div>
						</div>
					</div>					

					<h3>Invite Selected Players</h3>
					<div class="well">
						<p>Players invited this way will automatically be sent an invitation email and daily reminders to register.
						The game will start after all players have responded.  Emails are not sent to players until the game is started.</p>
						<c:if test="${not empty invites}">
						<ul class="column-list column-list-with-actions game-admin-invite-players">
							<li class="column-list-header">
								<span class="column-list-label"><span class="glyphicon glyphicon-glyphicon glyphicon-user"></span> Player</span>
								<span class="column-list-label"><span class="glyphicon glyphicon-glyphicon glyphicon-envelope"></span> Email</span>
								<span class="column-list-label">&nbsp;</span>
								<span class="column-list-label column-list-action">&nbsp;</span>
							</li>
							<c:forEach items="${invites}" var="i">
								<li>
									<span class="column-list-name"><span class="glyphicon glyphicon-glyphicon glyphicon-user"></span> ${i.name}</span>
									<span class="column-list-name"><span class="glyphicon glyphicon-glyphicon glyphicon-envelope"></span> ${i.email}</span>
									<form action="remove" method="post">
										<input type="hidden" name="inviteId" value="${i.id}">
										<span class="column-list-label column-list-action"><button type="submit" class="btn btn-delete"><span class="glyphicon glyphicon-glyphicon glyphicon-trash"></span></button></span>
									</form>
								</li>
							</c:forEach>		
						</ul>	
						</c:if>
						<c:if test="${not empty friends}">
						<button type="button" class="btn btn-default btn-sm invite-from-previous" data-toggle="modal" data-target="#myModal">
 							 Invite Players from Previous Games
						</button>
						</c:if>
						<button class="btn btn-default btn-sm" id="emailinvitebut">Invite Players By Email</button>
						<button class="btn btn-default btn-sm hidden" id="fbinvitebut">Invite Facebook Friends!</button>
						<form method="post" action="email" class="hidden invite-by-email" id="emailinviteform">
							<input name="email" size="40" placeholder="Enter email address here seperated by commas"/>
							<input class="btn btn-default btn-sm" type="submit" value="Submit">
						</form>
					</div>



					<div class="well">
						<div class="form-group  form-inline">
							<label>Give players up to </label>
							<select class="form-control" id="countDown">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3" selected>3</option>
								<option value="4">4</option>
								<option value="5">5</option>
							</select>
							<label> days to register.</label>
						</div>
						<form:form commandName="game" method="post">
							<button type="submit" name="action_start" class="btn btn-lg btn-primary">Start the Game! <span class="glyphicon glyphicon-chevron-right"></span></button>
							<input type="submit" name="_delete" value="Delete" class="btn btn-lg btn-default"> 
						</form:form>
					<!-- </div> -->
				</div>
						
			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->
	
	<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">Invite Players</h4>
      </div>
	<form action="member" method="post">
      <div class="modal-body">
      	<div class="container-fluid">
      		<div class="row players-select-list">
				<c:forEach items="${friends}" var="f">				
				<div class="col-md-6"> 
					<div class="thumbnail player-info">
						<input type="checkbox" name="member" value="${f.id}">
						<c:set var="avatarSrc" value="${f.imageUrl}" />
						<c:if test="${empty avatarSrc}">
							<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
						</c:if>										
						<img class="avatar-sm inline-avatar" src="${avatarSrc}" alt="" width="50" height="50">
						<span class="player-name" title="<c:out value="${f.name}" escapeXml="true"/>">${f.name}</span>
					</div> <!-- .player-info -->
				</div> <!-- .col-md-* -->
				</c:forEach>
			</div> <!-- .row -->
		</div> <!-- .container-fluid -->
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
        <button type="submit" class="btn btn-primary">Add Selected</button>
      </div>
  	</form>
    </div>
  </div>
</div>
	

	<script>
	$(document).ready(function(){
			
		// select all of shared URL on input click
		$('#shared-game-link').on('focus', function() {
			var $this = $(this)
				.one('mouseup.mouseupSelect', function() {
					$this.select();
					return false;
				})
				.one('mousedown', function() {
					// compensate for untriggered 'mouseup' caused by focus via tab
					$this.off('mouseup.mouseupSelect');
				})
				.select();
		});	
		
		// Highlight selected player in Previous Players modal 
		// (not working fully yet - want to select by clicking checkbox parent div [.player-info])
		$(document).on('click, focus, blur, change', '.players-select-list .player-info', function() {
			var player_checkbox = $(this).find('input[type="checkbox"]'); // .filter(':checked');
			
			if( $(this).hasClass('selected-highlight') ) {			
				$(this).removeClass('selected-highlight');
				player_checkbox.prop('checked',false); 
			} else {
				$(this).addClass('selected-highlight');
				player_checkbox.prop('checked',true); 
			}
		});

		
		// toggle all checkboxes
		$("#invite-player-actions").on('click touchstart', function () {
			$(".game-admin-invite-players .multi-check").prop('checked', $(this).prop('checked'));
		});
	
		$('.game-admin-invite-players .multi-check').on('change', function() {
			if (!$(this).prop("checked")){
				$("#invite-player-actions").prop("checked",false);
			}
		});

		function setMaxPlayers(v) {
	    	$.post( "share", {"max": v}). done ( function( data ) {
	    		console.log("updated max players to " + v);
	    	});
		}

		$("#linkOn").click(function() {
			$("#shareLinkForm").removeClass("hidden");
			$("#linkOn").addClass("active");
			$("#linkOff").removeClass("active");
			setMaxPlayers($("#maxPlayers").val());
		});	
		$("#linkOff").click(function() {
			$("#shareLinkForm").addClass("hidden");
			$("#linkOff").addClass("active");
			$("#linkOn").removeClass("active");
			setMaxPlayers(0);
		});	
		$("#countDown").change(function () {
	    	$.post( "days", {"days": $("#countDown").val()}). done ( function( data ) {
	    		console.log("updated days to " + $("#countDown").val());
	    	});
		} );
		$("#maxPlayers").change(function () {
			setMaxPlayers($("#maxPlayers").val());
		});
		   
		$("#emailinvitebut").click(function() {
			$("#emailinviteform").removeClass("hidden");
		});
		
	});
	</script>
<script>

function fblogin(onConnected) {
	FB.getLoginStatus(function(response) {
		    console.log('statusChangeCallback');
		    console.log(response);
		    // The response object is returned with a status field that lets the
		    // app know the current login status of the person.
		    // Full docs on the response object can be found in the documentation
		    // for FB.getLoginStatus().
		    if (response.status === 'connected') {
		      // Logged into your app and Facebook.
		      onConnected();
		    } else { 
		      // The person is logged into Facebook, but not your app.  Or not logged into FB.
		    	FB.login(function(response) {
				    console.log('LoginCallback');
				    console.log(response);
			        if (response.status === 'connected') {
			          // Logged into your app and Facebook.
			          onConnected();
			        }
		    	}, {scope: 'email'});
		    }
	});
}	

function fbinvite() {
   FB.ui({method: 'apprequests',
	      message: 'Join my Trivolous game about us titled "${userSession.gameDesc.name}"'
//	      data: '${regkey}',
	    }, function(response){
	    	console.log("apprequest response");
	        console.log(response);
	        var numInvited = response.to.length;
	        console.log("Num fb players added = " + numInvited);
	        //$('#fbInviteCount').html(parseInt($('#fbInviteCount').html(), 10)+numInvited)
	        var invites = [];
	        $.each(response.to, function(index,uid) {
	        	console.log("invited uid=" + uid);
	        	FB.api(uid, function(uinfo) {
	        		console.log(uinfo);
	        		console.log("Invite : " + uinfo.id + " " + uinfo.name);
	    	        $.ajax({
	    	      	   	type: "POST",
	    	      	   	url: "fbinvite",
	    	      		data: uinfo,
	    	      		success: function (r) {
	    	      			console.log("fbinvite response = " + r); 
	    	    	        if (--numInvited == 0) {
	    	    	        	location.reload();
	    	    	        }
	    	      		}
	    	        });
	        	});
	        });
	    });
}

window.fbAsyncInit = function() {
	  FB.init({
	    appId      : '434772353234899',
	    cookie     : true,  // enable cookies to allow the server to access 
	                        // the session
	    xfbml      : true,  // parse social plugins on this page
	    version    : 'v2.2' // use version 2.2
	  });
	  
		$("#fbinvitebut").click(function() {
			fblogin(fbinvite);
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
</k:member_page>
	
