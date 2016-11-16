<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<k:game_page>
	
	<section id="settings" class="game-nav-content row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12">
				
				<header class="section-header">
					<h1><span class="glyphicon glyphicon-cog"></span> Game Settings</h1>
				</header>
<c:choose>
<c:when test="${gameDesc.status == 'REGISTERING'}">
				<div class="well" id="stateRegistration">
					<p class="text-muted text-center">Players are currently responding to invitation and joining the game.</p>

					<h3>Registration Status</h3>
					
					<p>These players have joined the game:</p>
					<div class="row players-list">
						<c:forEach items="${registeredPlayers}" var="p">
						<div class="col-sm-6 col-md-3"> 
							<div class="thumbnail player-info">
								<c:set var="avatarSrc" value="${p.memberImage}" />
								<c:if test="${empty avatarSrc}">
									<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
								</c:if>											
								<img class="avatar-sm inline-avatar" src="${avatarSrc}" alt="" width="50" height="50">
								<span class="player-name" title="<c:out value="${p.name}" escapeXml="true"/>">${p.name}</span>
							</div> <!-- .player-info -->
						</div> <!-- .col-md-* -->					
						</c:forEach>
					</div>			
					<c:if test="${not empty invitedPlayers}">		
						<p>These players invited by email have not responded yet:
						</p>
						<div class="row players-list">
							<c:forEach items="${invitedPlayers}" var="p">
							<div class="col-sm-6 col-md-3"> 
								<div class="thumbnail player-info">
									<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
									<img class="avatar-sm inline-avatar" src=${avatarSrc} alt="" width="50" height="50">
									<c:choose>
										<c:when test="${not empty p.name}">
											<span class="player-name" title="<c:out value="${p.name}" escapeXml="true"/>">${p.name}</span><br/>
										</c:when>
										<c:otherwise>
											<span class="player-name" title="<c:out value="${p.email}" escapeXml="true"/>">${p.email}</span>
										</c:otherwise>
									</c:choose>
								</div> <!-- .player-info -->
							</div> <!-- .col-md-* -->					
							</c:forEach>
						</div>					
					</c:if>
										
					<p>The game will automatically start in ${gameDesc.countDown} more days, or when
					${gameDesc.maxPlayers} players have joined.</p>
					<form method="post">
						<c:if test="${fn:length(registeredPlayers) >= 3}">
							<button class="btn btn-success" type="submit" name="start">Start the Game Now!</button>
						</c:if>
						<button class="btn btn-default" type="submit" name="extend">Extend Registration 1 Day</button>
					</form>
					<hr>
					
					<h3>Invite More Players</h3>
					<p>As long as the registration period is open you can continue to invite players using a share link or through Facebook</p>
	
  					<div class="form-group form-inline">
  						<label for="shared-game-link"><span class="glyphicon glyphicon-link"></span> Share Link</label>
  						<c:set var="req" value="${pageContext.request}" />
						<c:set var="baseURL" value="${req.scheme}://${req.serverName}:${req.serverPort}/openRegister" />
						<input id="shared-game-link" readonly type="text" value="<c:url value="${baseURL}"><c:param name="key" value="${regkey}"/></c:url>">
						<p class="text-center">
						<small>Click the link above to select it, then copy it to share. You can paste it on social media sites, forums, or anywhere you want to invite people to play your game.</small>
						</p>
					</div>					
					
					<div class="btn-group-separate hidden">
						<button class="btn btn-facebook">Invite Facebook Friends!</button>
					</div> <!-- .btn-group-separate -->
				</div>
</c:when>
<c:otherwise>
			
				<div class="well" id=""> <!-- id="stateActive" -->
				
					<h3>Game Status</h3>
					<c:choose>
						<c:when test="${gameDesc.status=='ACTIVE'}">
						The game is active.
						<form method="post">
							<div class="btn-group-separate">
								<button class="btn btn-warning" name="pause">Pause Game</button>
								<button class="btn btn-danger" name="end">End Game</button>
							</div> <!-- .btn-group-separate -->
						</form>
						</c:when>
						<c:otherwise>
						The game is paused.  During this time no notifications will be sent.
						<form method="post">
							<div class="btn-group-separate">
								<button class="btn btn-warning" name="resume">Resume Game</button>
								<button class="btn btn-danger" name="end">End Game</button>
							</div> <!-- .btn-group-separate -->
						</form>
						</c:otherwise>
					</c:choose>
					<hr>
					
					<h3>Manage Players</h3>			
					
					<ul class="column-list column-list-with-actions game-admin-invite-players">
						<li class="column-list-header">
							<span class="column-list-label"><span class="glyphicon glyphicon-glyphicon glyphicon-user"></span> Player</span>
							<span class="column-list-label"><span class="glyphicon glyphicon-glyphicon glyphicon-check"></span> Status</span>
							<span class="column-list-label">&nbsp;</span>
							<span class="column-list-label column-list-action">&nbsp;</span>
						</li>
						<c:forEach items="${players}" var="p">
						<li> 
							<span class="column-list-name"><span class="glyphicon glyphicon-glyphicon glyphicon-user"></span> ${p.name} <small>(${p.memberName})</small></span>
							<span class="column-list-name player-status"><span class="glyphicon glyphicon-glyphicon glyphicon-check"></span> ${p.status.string}</span>
							<span class="column-list-email"><a class="btn btn-default" href="mailto:${p.email}"><span class="glyphicon glyphicon-envelope"></span> ${p.email}</a></span>
							<c:if test="${gameDesc.numPlayers > 3}">
								<form action="/game/settings/removePlayer" method="post">
									<input type="hidden" name="id" value="${p.id}"/>
									<span class="column-list-label column-list-action">
										<button id="player-id-1" name="removePlayer" class="btn btn-delete">
											<span class="glyphicon glyphicon-glyphicon glyphicon-trash"></span>
										</button>
									</span>
								</form>
							</c:if>
						</li>
						</c:forEach>
					</ul>						
					
				</div> <!-- .well -->	
</c:otherwise>
</c:choose>			

			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	
	</section>
	
	
	
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
		
		
	});
	</script>
	
</k:game_page>

