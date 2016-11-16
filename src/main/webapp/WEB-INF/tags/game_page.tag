<%@tag description="User Page template" pageEncoding="UTF-8"%>
<%@taglib prefix="k" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<k:member_page>
<div class="container" id="main">
	<div id="game-header">
		<header>
			<h1 class="game-title">
				<a href="/game/game">${userSession.gameDesc.name}</a>
				
				<div class="game-nav">
					<a href="/game/info" title="Game Info">
					<span class="glyphicon glyphicon-info-sign"></span> 
					</a>

					<a href="/game/score" title="Scoreboard">
					<span class="glyphicon glyphicon-star"></span> 
					</a>
<c:choose>
<c:when test="${userSession.playerData.queuedQuestionCount > 0}">
					<a href="/game/queue/make" title="My Queue">
					<span class="glyphicon glyphicon-bookmark queuedIcon"></span>
					</a>
</c:when>
<c:otherwise>
					<a href="/game/queue/make" title="My Queue">
					<span class="glyphicon glyphicon-bookmark"></span>
					</a>
</c:otherwise>
</c:choose>
					
					<a  href="/game/settings" title="Game Settings">
					<span class="glyphicon glyphicon-cog"></span>
					</a>
				</div>
				
				<span class="hidden game-author">
				by ${userSession.gameDesc.masterName}
				</span>
			</h1>
		</header>
	</div> <!-- #game-header -->
      <jsp:doBody/>
      </div>
	<script>
      $(document).ready(function(){
		    var path = window.location.pathname;
		    console.log("MenuPath="+path);
		    $(".game-nav a[href='" + path + "']").addClass('selected');
	    }); 
	</script>		    
      
</k:member_page>

