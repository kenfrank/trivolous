<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:member_page>



<div class="container" id="main">
	<div id="game-header">
		<header>
			<h1 class="game-title">
				<a href="#">${userSession.gameDesc.name}</a>
				<span class="hidden game-author">
				by ${userSession.gameDesc.masterName}
				</span>
			</h1> 	
		</header>
	</div> <!-- #game-header -->
	
<c:choose>
	<c:when test="${answered =='right'}">
	<div id="status-correct" class="alert alert-success alert-with-icon" role="alert">
			<span class="glyphicon glyphicon-ok-sign"></span> 
			<div class="title">You got it right!</div>
		<p>
		You were the last to answer, and now the game is over.  Look below to see the results.
		</p>
	</div> <!-- .alert -->
	</c:when>
					
	<c:when test="${answered == 'wrong'}">
	<div id="status-wrong" class="alert alert-danger alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-remove-sign"></span> 
		<div class="title">Oops! Better luck next time!</div>
		<p> 
		You were the last to answer, and now the game is over.  Look below to find the right answer and see the results.
		</p>
	</div> <!-- .alert -->	
	</c:when>
</c:choose>

<div class="alert alert-info alert-with-icon" role="alert">
	<span class="glyphicon glyphicon-alert"></span> 
	<div class="title">Game Over</div>

	<p> 
	See the final scores, browse past questions, or make a final comment below.  
	</p>
</div> <!-- .alert -->


	<section id="info" class="game-nav-content row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12"> 
				<header class="section-header">
					<h1><span class="glyphicon glyphicon-info-sign"></span> Game Info</h1>
				</header>
			
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-check"></span> Status</b> ${gameDesc.status.string}
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-user"></span> Mastered by</b> ${gameDesc.masterName}
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-time"></span> Created </b> <fmt:formatDate type="date" dateStyle="long" value="${gameDesc.created}" />
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-time"></span> Finished </b> <fmt:formatDate type="date" dateStyle="long" value="${gameDesc.endDate}" />
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-edit"></span> Description</b> "<i><c:out value="${gameDesc.description}"/></i>"
				</p>
				<c:if test="${not empty activeGameDesc}">
				<p>
					<b class="info-label"> Round</b>  ${activeGameDesc.roundNumber} of  ${activeGameDesc.totalRounds}
				</p>
				</c:if>
			
			</div> <!-- col-md-* -->
		</div> <!-- container-fluid -->
	</section> <!-- row -->

	<section class="game-nav-content row theme-game-content">
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
					
	<section id="previous-questions" class="row theme-game-content">
	
		<div class="container-fluid">
			<div class="col-md-12"> 
				<header class="section-header">
					<h1>Questions</h1>
					<div id="previousQsToggle" class="toggle-all">
						<span class="glyphicon glyphicon-resize-small"></span>			
					</div>		
				</header>	
				<div id="previousQuestionSummary">
				<c:forEach items="${rounds}" var="prevRound">
					<header class="question-header clearfix">
						<h1 class="question-title">
							Question # ${prevRound.roundNumber} <span class="question-author"> by ${prevRound.askerPlayerName}</span>
						</h1>		
					</header>	
					<div class="game-content">
						<div class="well well-lg">
							<div class="question-text">
								<span class="big-text-in-circle">Q</span>
								<p><c:out value= "${prevRound.question.text}"/></p>
							</div> <!-- .question-text -->
						</div>
					</div>		
				</c:forEach>
				</div>		
				<div id="previousQuestionContent"></div>
			</div> <!-- .col-md-* -->			
		</div> <!-- .container -->
	</section> 


	<section class="row question">
	
		<div class="container-fluid">
			<div class="col-md-12"> 
				<h3 id="current-comments">Game Comments</h3>
				<div id="comments">
				<c:forEach items="${comments}" var="c">
					<div id="comments" class="comment">
						<c:set var="avatarSrc" value="${c.playerImage}" />
						<c:if test="${empty avatarSrc}">
							<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
						</c:if>		
						<img class="avatar-sm" src="${avatarSrc}" alt="" width="30" height="30">
						<span class="commentor-name">${c.playerName}</span>
						<span class="commentor-text">
						"<c:out value="${c.text}"/>"
						</span> <!-- .comment-text -->
					</div> <!-- .comment -->
				</c:forEach>
				</div>
				<form method="post" id="commentForm">
				<div class="comment add-comment">
					<c:set var="avatarSrc" value="${userSession.playerData.memberImage}" />
					<c:if test="${empty avatarSrc}">
						<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
					</c:if>		
					<img class="avatar-sm" src="${avatarSrc}" alt="" width="30" height="30">
					<span class="commentor-name commentor-you">You</span>
					<span class="commentor-text form-inline">
						<input class="form-control add-comment-field" type="text" name="text" placeholder="Write a comment">
						<button class="btn btn-primary btn-sm" type="submit">Add</button>
					</span> <!-- .comment-text -->
				</div> <!-- .comment -->			
				</form>
			</div> <!-- .col-md-* -->			
		</div> <!-- .container -->
	</section> <!-- .row -->	

</div> <!-- #main -->



	<script>
	$(document).ready(function(){

		$("#previousQsToggle").click(function() {
			$.get("/game/over/previousQuestions",function(data) {
				if ($("#previousQuestionSummary").hasClass("hidden")) {
					$("#previousQuestionSummary").removeClass("hidden");
					$("#previousQuestionContent").addClass("hidden");
				}
				else {
					$("#previousQuestionSummary").addClass("hidden");
					if ($('#previousQuestionContent').is(':empty')) {
						$("#previousQuestionContent").html(data);
					}
					else {
						$("#previousQuestionContent").removeClass("hidden");
					}
				}
			});
		});
		
		$('#commentForm').on( 'submit', function(e) {
			e.preventDefault(); 
			var subber = $(this).find('[type="submit"]');
			subber.text("Adding...").addClass("disabled");
		    $.post( '/game/over/comment', $('#commentForm').serialize(), function(data) {
		        	$("#comments").html(data); 
		        	$('#commentForm').each(function() {this.reset();});
					subber.text("Add").removeClass("disabled");
		       });
		});
				
				
	
	});
	
	
	</script>


</k:member_page>
