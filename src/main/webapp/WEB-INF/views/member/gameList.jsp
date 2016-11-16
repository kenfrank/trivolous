<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>

<k:member_page>
	<jsp:body>


<div class="container" id="main">
	<section class="row theme-game-content">
		<div class="container-fluid">
		
			<div class="col-md-12">
			
				<h1>Games</h1>

				<c:if test="${openRegFailed}">
					<div class="alert alert-info alert-with-icon" role="alert">
						<span class="glyphicon glyphicon-alert"></span>
						<c:choose>
							<c:when test="${openRegFailedGame != null}">
								<div class="title">Sorry, registration link failed for '${openRegFailedGame.name}' by 
								${openRegFailedGame.masterName}</div>
							</c:when>
							<c:otherwise>
								<div class="title">Sorry, registration link failed</div>
							</c:otherwise>
						</c:choose>
						<c:choose>
							<c:when test="${openRegFailedReason == 'ALREADY_MEMBER'}">
								<p> 
									You followed a link to join a game, but you have already joined the game.
								</p>
							</c:when>
							<c:otherwise>
								<p> 
									You followed a link to join a game, but that game is no longer open
									for registration. 
								</p>
							</c:otherwise>
						</c:choose>
					</div> <!-- .alert -->
				</c:if>

				<c:if test="${(empty invitesGameList) && (empty activeGameList)}">
					<div class="alert alert-info alert-with-icon" role="alert">
						<span class="glyphicon glyphicon-alert"></span>
						<div class="title">You have no invites or active games.</div>
						<p> 
							Why not <b><a href="/member/masterCreate">start a new game now!</a></b>
						</p>
					</div> <!-- .alert -->
				</c:if>
				
				<c:if test="${not empty invitesGameList}">
				<h2>Invites</h2>
				<ul class="list-unstyled">
					<c:forEach items="${invitesGameList}" var="g">
						<li class="alert alert-info alert-with-icon" role="alert">
							<span class="glyphicon glyphicon-star"></span>
							<div class="title">You have been invited by <b>${g.masterName}</b> to play the game: '${g.name}'</div>
							
							<c:if test="${not empty g.description}">
							<p>
							<b class="info-label"><span class="glyphicon glyphicon-info-sign"></span> Info</b> <i>"${g.description}"</i> 
							</p>
							</c:if>
							
							<form method="post" action="/member/inviteResponse">
								<input type="hidden" name="gameId" value="${g.id}">
								<span class="column-list-label"><button name="accept" type="submit" class="btn btn-primary btn-sm">Accept</button></span>
								<span class="column-list-label"><button name="decline" type="submit" class="btn btn-default btn-sm">Decline</button></span>
							</form>
						</li>	
					</c:forEach>
				</ul>
				</c:if>
			
				<c:if test="${not empty activeGameList}">
				<h2>Active</h2>
				<ul class="column-list">
					<li class="column-list-header">
						<span class="column-list-label"><span class="glyphicon glyphicon-glyphicon glyphicon-star"></span> Game</span>
						<span class="column-list-label"><span class="glyphicon glyphicon-check"></span> Status</span>
						<span class="column-list-label"><span class="glyphicon glyphicon-time"></span> Waiting since...</span>
						<span class="column-list-label">&nbsp;</span>
					</li>
					<c:forEach items="${activeGameList}" var="a">
						<li>
							<span class="column-list-name"><span class="glyphicon glyphicon-glyphicon glyphicon-star"></span> ${a.desc.name}</span>
							<span class="column-list-status"><span class="glyphicon glyphicon-check"></span> ${a.status.statusString}</span>
							<span class="column-list-date"><span class="glyphicon glyphicon-time"></span> ${t:timeAgo(a.status.waitDate)}</span>
							<c:choose>
								<c:when test="${a.player.isActionRequired}">
									<span class="column-list-"><a href="/game/game?gameid=${a.desc.id}" class="btn btn-info btn-sm">Take Your Turn</a></span>
								</c:when>
								<c:when test="${a.player.isMaster && a.desc.status == 'CREATING'}">
									<span class="column-list-label"><a href="/game/create/description?gameid=${a.desc.id}" class="btn btn-default btn-sm">Finish Creating</a></span>
								</c:when>
								<c:when test="${a.player.isMaster && a.desc.status == 'INVITING'}">
									<span class="column-list-label"><a href="/game/create/invite/home?gameid=${a.desc.id}" class="btn btn-default btn-sm">Finish Inviting</a></span>
								</c:when>
								<c:when test="${a.player.isMaster}">
									<span class="column-list-label"><a href="/game/game?gameid=${a.desc.id}" class="btn btn-default btn-sm">Manage or View Game</a></span>
								</c:when>
								<c:otherwise>
									<span class="column-list-label"><a href="/game/game?gameid=${a.desc.id}" class="btn btn-default btn-sm">View Game</a></span>
								</c:otherwise>
							</c:choose>
						</li>
					</c:forEach>
				</ul>
				</c:if>

				<c:if test="${not empty completedGameList}">
				<h2>Completed</h2>
				<ul class="column-list">
					<li class="column-list-header">
						<span class="column-list-label"><span class="glyphicon glyphicon-glyphicon glyphicon-star"></span> Game</span>
						<span class="column-list-label"><span class="glyphicon glyphicon-certificate"></span> Winner</span>
						<span class="column-list-label"><span class="glyphicon glyphicon-time"></span> Ended</span>
						<span class="column-list-label">&nbsp;</span>
					</li>
					<c:forEach items="${completedGameList}" var="g">
						<li>
							<span class="column-list-name"><span class="glyphicon glyphicon-glyphicon glyphicon-star"></span> <a href="/game/game?gameid=${g.desc.id}">${g.desc.name}</a></span>
							<span class="column-list-winners"><span class="glyphicon glyphicon-glyphicon glyphicon-certificate"></span> ${g.winnerName}</span>
							<span class="column-list-date"><span class="glyphicon glyphicon-glyphicon glyphicon-time"></span>  <fmt:formatDate type="date" value="${g.desc.endDate}" /></span>
							<span class="column-list-label"><a href="/game/game?gameid=${g.desc.id}" class="btn btn-default btn-sm">View Game</a></span>
						</li>	
					</c:forEach>
				</ul>
				</c:if>							
			</div> <!-- .col-md-* -->

		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->

<div id="fb-root"></div>
<script>


function deleteRequest(requestId) {
		FB.api(requestId, 'delete', function(response) {
		console.log("delete response : " + response);
		});
}   

function checkRequests() {
	FB.getLoginStatus(function(response) {
	    console.log('statusChangeCallback');
	    console.log(response);
	    if (response.status === 'connected') {
		    var refresh = false;
			FB.api('me/apprequests', function(response){
		   		console.log("Got apprequests");
		        console.log(response);
		        if (response.hasOwnProperty('data')) {
			        $.each(response.data, function(index, value) {
			        	console.log('++++' + index + 'msg=' + value.message);
			        	console.log('Delete id = ' +  value.id);
			        	deleteRequest(value.id);
			        });
		        }
			});
			if (refresh) {
				location.reload();
			}
	    }
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
	  checkRequests();
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




    </jsp:body>
 </k:member_page>
