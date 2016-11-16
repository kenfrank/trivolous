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
					<c:forEach items="${games}" var="g">
						<h4>${g.id} '${g.name}' by ${g.masterName} started ${g.created} ended ${g.endDate}</h4>
						<div>
						description = ${g.description},
						totalAuthorCycles = ${g.totalAuthorCycles},
						maxPlayers = ${g.maxPlayers},
						allowOpenRegistration = ${g.allowOpenRegistration},
						privacy = ${g.privacy},
						masterId = ${g.masterId},
						countDown = ${g.countDown},
						masterPlayerId = ${g.masterPlayerId},
						numPlayers = ${g.numPlayers},
						status = ${g.status}
						</div>							
					</c:forEach>
				<h1>Members</h1>
					<c:forEach items="${members}" var="m">
						<h4>${m.id} ${m.firstName} ${m.lastName}</h4>
						<div>
						email = ${m.email},
						facebookId = ${m.facebookId},
						defaultGameId = ${m.defaultGameId},
						imageUrl = ${m.imageUrl}	
						</div>						
					</c:forEach>
				
			</div> <!-- .col-md-* -->

		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->


    </jsp:body>
 </k:member_page>
