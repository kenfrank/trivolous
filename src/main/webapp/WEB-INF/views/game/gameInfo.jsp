<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:game_page>
	
	<section id="info" class="game-nav-content row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12"> 
				<header class="section-header">
					<h1><span class="glyphicon glyphicon-info-sign"></span> Game Info</h1>
				</header>
			
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-check"></span> Status</b> ${gameDesc.status}
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-user"></span> Mastered by</b> ${gameDesc.masterName}
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-time"></span> Created </b> <fmt:formatDate type="date" dateStyle="long" value="${gameDesc.created}" />
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-edit"></span> Description</b> "<i><c:out value="${gameDesc.description}"/></i>"
				</p>
				<c:if test="${not empty activeGameDesc and activeGameDesc.status=='ACTIVE'}">
				<p>
					<b class="info-label"> Round</b>  ${activeGameDesc.roundNumber} of  ${activeGameDesc.totalRounds}
				</p>
				
				<p>
					<b class="info-label">Players </b>  (In the order they make questions)
				</p>
				
				<ol class="row player-order-list">
					<c:forEach items="${players}" var="p"> 
					<li class="col-sm-6 col-md-3<c:if test='${not p.isActive}'> disabled</c:if>"> 
						<div class="thumbnail player-info<c:if test="${p.isAsker}"> selected-highlight</c:if>">
							<c:set var="avatarSrc" value="${p.memberImage}" />
							<c:if test="${empty avatarSrc}">
								<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
							</c:if>							
							<img class="avatar-sm inline-avatar" src="${avatarSrc}" alt="" width="50" height="50">
							<span class="player-name">${p.name}
							<c:if test="${p.isAsker}"><small>Current Author</small></c:if>
							<c:if test="${not p.isActive}"> <small>No longer playing</small></c:if>
							</span>
							<span class="hidden label label-primary player-total"><small>Total</small> ${p.score}</span>
						</div> <!-- .player-info -->
					</li> <!-- .col-md-* -->
					</c:forEach>
					
				</ol> <!-- .row -->			
				
				</c:if>
			</div> <!-- col-md-* -->
		</div> <!-- container-fluid -->
	</section> <!-- row -->

</k:game_page>
