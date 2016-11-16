<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:game_page>
	
	<section id="scoreboard" class="game-nav-content row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12"> 

				<header class="section-header">
					<h1><span class="glyphicon glyphicon-star"></span> Scoreboard</h1>
				</header>
				
				<div id="scoreboard-content">
					<div class="row players-list">
						<c:forEach items="${players}" var="p"> 
						<div class="col-sm-6 col-md-3"> 
							<div class="thumbnail player-info">
								<c:set var="avatarSrc" value="${p.memberImage}" />
								<c:if test="${empty avatarSrc}">
									<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
								</c:if>									
								<img class="avatar-sm inline-avatar" src="${avatarSrc}" alt="" width="50" height="50">
								<span class="player-name">${p.name}</span>
								<span class="label label-primary player-total"><small>Total</small> ${p.score}</span>
							</div> <!-- .player-info -->
						</div> <!-- .col-md-* -->
						</c:forEach>
					</div>				
				</div> <!-- #scoreboard-content -->
			
			</div> <!-- col-md-* -->
		</div> <!-- container-fluid -->
	</section> <!-- row -->

</k:game_page>
