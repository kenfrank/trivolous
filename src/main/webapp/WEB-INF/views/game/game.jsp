<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:game_page>



<c:if test="${not empty firstTimer}">
<div class="alert alert-info alert-with-icon" role="alert">
	<span class="glyphicon glyphicon-question-sign"></span> 
	<div class="title">Welcome!  Here's how things work...</div>
	<p> 
	At the top of this page you will get current information about the game through various messages (sometimes more than one):<br>
	<span class="glyphicon glyphicon-alert text-warning"></span>  You need to take action to answer a question or make a question.<br>
	<span class="glyphicon glyphicon-ok-sign text-success"></span> You answered the question correctly.<br>  
	<span class="glyphicon glyphicon-remove-sign text-danger"></span> You answered the question incorrectly.<br>
	<span class="glyphicon glyphicon-hourglass text-info"></span> You are waiting for the other players to answer or make a question.<br>
	After the first question is answered, you will see the results below.
	After more questions are answered, they will be listed below also.
	Follow the icons below the game name above to get the game details, see the score, queue up a question, or adjust the settings.
	Click on the game name to go to the main page.  
	Right now look below to see that you need to answer a question. Have fun!
	</p>
</div> <!-- .alert -->
</c:if>

<c:choose>
	<c:when test="${answered =='right'}">
	<div id="status-correct" class="alert alert-success alert-with-icon" role="alert">
			<span class="glyphicon glyphicon-ok-sign"></span> 
			<div class="title">You got it right!</div>
		<p>
		Look below to see the results.
		</p>
	</div> <!-- .alert -->
	</c:when>
					
	<c:when test="${answered == 'wrong'}">
	<div id="status-wrong" class="alert alert-danger alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-remove-sign"></span> 
		<div class="title">Oops! Better luck next time!</div>
		<p> 
		Look below to find the right answer and see the results.
		</p>
	</div> <!-- .alert -->	
	</c:when>
</c:choose>

	
	
<c:choose>




<c:when test="${player.status=='NEEDS_TO_MAKE_QUESTION'}">
	<div id="status-make" class="alert alert-warning alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-alert"></span> 
		<div class="title">Its your turn to make the next question!</div>
		<p>
		The other players are waiting for you to make a question.  Don't sweat it if you can't think of the perfect question.  It's
		best to make any question and keep having fun.  Your questions are always better than you think!  
		<br><br>
		<a class="btn btn-primary btn-sm" href="/game/queue/make">Make your question</a>
		<button id="stuckButton" type="button" class="btn btn-default btn-sm">
			Help, I'm stuck!</button>
		</p>
		
		<div id="questionHelp" class="well hidden">
			<div class="h3">Need some help?</div>
			<p>Here are some tips on how to make a question</p>
			<ol>
				<li>Troll the internet and wikipedia for ideas.</li>
				<li>Your facebook stream, Pinterest, and Instagram can also be
					good sources of inspiration.</li>
				<li>Try asking a question about yourself</li>
				<li>Try making a question using an image</li>
				<li>Try creating a true/false question</li>
				<li>Any question is better than skipping, and you will find
					that your questions are much better than you thought they were!</li>
			</ol>
			<p>If you are still stuck you may skip your turn using the
				button below. When you skip your turn the turn is completed by
				automatically generating your question and the other players
				answers. The other players will get the maximum score of 25 points
				and you will not get any points.</p>
			<form action="/game/skip" method="post">
				<label>If you're skipping your turn, you can add a comment to let other players know why:</label><br>
				<textarea name="text" rows="2" cols="50"></textarea>
				<br>
				<a href="/game/game" class="btn btn-default btn-sm">Cancel</a>
				
				 <INPUT type="submit"
					onClick="return confirm('Are you sure you want to skip making your question?')"
					value="Skip My Quesiton" class="btn btn-danger btn-sm" /> 
			</form>
		</div> <!-- #questionHelp --->
	</div> <!-- .alert -->	
</c:when>


<c:when test="${player.status=='WAITING_FOR_QUESTION'}">
	<div class="alert alert-info alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-hourglass"></span> 
		<div class="title">You are waiting for a question.</div>
	
		<p> 
		It is <b>${activeGame.askerPlayerName}</b>'s turn to make the question.  You will be notified when it is ready.
		</p>
	</div> <!-- .alert -->
</c:when>

<c:when test="${player.status=='FINISHED'}">
	<div id="status-over" class="alert alert-success alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-alert"></span> 
		<div class="title">You are finished playing this game.</div>
	
		<p> 
		<b>What happens now?</b> See the results of the last question below, 
		and <b><a href="#current-comments">make a final comment</a></b> if you wish. You will always be able to come back to this game 
		and <b><a class="trigger-content" href="#scoreboard">view the scoreboard</a></b> or 
		<b><a href="#previous-questions">browse the questions</a></b>. Why not <b><a href="/member/masterCreate">start a new game now!</a></b>
		</p>
	</div> <!-- .alert -->
</c:when>

	
<c:when test="${player.status=='NEEDS_TO_ANSWER'}">

	<div id="status-ready" class="alert alert-warning alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-alert"></span> 
		<div class="title">It your turn to answer <b>${currentRound.askerPlayerName}'s</b> question</div>
		<p>

		<h2 class="text-center">Choose your bid to see the question:</h2>
					
		<div class="bid-choices center-block text-center clearfix">					
			<a class="game-token copper" href="/game/answer?bet=5">
				<div class="game-token-label">5</div>
			</a>
	
			<a class="game-token silver" href="/game/answer?bet=10">
				<div class="game-token-label">10</div>
			</a>
	
			<a class="game-token gold" href="/game/answer?bet=25">
				<div class="game-token-label">25</div>
			</a>
		</div>

		<div class="center-block answer-latest-question">

			 <p>
			 <span class="glyphicon glyphicon-time"></span>  
			 <b>Time limit:</b> ${currentRound.question.timeout} seconds
			 </p>
	
			<c:if test="${not empty currentRound.question.hint}">
				<p>
				<span class="glyphicon glyphicon-comment"></span>   
				<b>Hint: </b> ${currentRound.question.hint}
				</p>
			</c:if>
		</div>
	</div> <!-- .staus-ready -->

</c:when>

<c:when test="${gameDesc.status=='REGISTERING' || gameDesc.status=='INVITING'}">
	<div class="alert alert-info alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-hourglass"></span> 
		<div class="title">You are waiting for more players to join.</div>
		<p> 
		Registration stays open until a total of ${gameDesc.maxPlayers} players have joined or for ${gameDesc.countDown} more days.  You will be notified when the registration is complete. As players join they answer the first question.  You can see the status below.
		</p>
	</div> <!-- .alert -->
</c:when>

<c:when test="${player.status=='ANSWERED'}">
	<div class="alert alert-info alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-hourglass"></span> 
		<div class="title">You are waiting for the others to answer...</div>
		<p> You can see the current status below. When all players have answered you will be notified.</p>
		<c:if test="${activeGame.status=='ACTIVE'}">
			<p>You are waiting for these players to answer:</p>
			<ul>
				<c:forEach items="${playersToAnswer}" var="pta">
					<li>${pta.name}</li>
				</c:forEach>
			</ul> 		
		</c:if>

	</div> <!-- .alert -->
</c:when>

<c:when test="${player.status=='MADE_QUESTION'}">
	<div class="alert alert-info alert-with-icon" role="alert">
		<span class="glyphicon glyphicon-hourglass"></span> 
		<div class="title">You are waiting for the others to answer your question...</div>
		<p>You can see the current status below. When all players have answered you will be notified.</p>
		<c:if test="${activeGame.status=='ACTIVE'}">
			<p>You are waiting for these players to answer:</p>
			<ul>
				<c:forEach items="${playersToAnswer}" var="pta">
					<li>${pta.name}</li>
				</c:forEach>
			</ul> 		
		</c:if>
	</div> <!-- .alert -->
</c:when>

</c:choose>


<c:if test="${not empty lastRound}">
	<section id="current" class="row question">
	
		<div class="container-fluid">
			<div class="col-md-12"> 




				<header class="question-header clearfix">
					<h1 class="question-title">
						Question # ${lastRound.roundNumber} <span class="question-author">by ${lastRound.askerPlayerName}</span>
					</h1>
					
					<a class="question-previous" href="">
						<span class="glyphicon glyphicon-chevron-left"></span>
					</a>
					<a class="question-next" href="">
						<span class="glyphicon glyphicon-chevron-right"></span>			
					</a>
				</header>
				
				<div class="game-content">

				<%@include file="_question.jsp"%>	
					<h3 id="current-comments">Comments</h3>

					<div id="comments">
						<jsp:include page="_comments.jsp" />	
					</div>
					<form method="post" id="commentForm">
					<div class="comment add-comment">
						<c:set var="avatarSrc" value="${player.memberImage}" />
						<c:if test="${empty avatarSrc}">
							<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg"  />
						</c:if>							
						<img class="avatar-sm" src="${avatarSrc}"  alt="" width="30" height="30">
						<span class="commentor-name commentor-you">You</span>
						<span class="commentor-text form-inline">
							<input class="form-control add-comment-field" type="text" name="text" placeholder="Write a comment">
							<button class="btn btn-primary btn-sm" type="submit">Add</button>
						</span> <!-- .comment-text -->
					</div> <!-- .comment -->			
					</form>
				</div> <!-- .game-content -->
				
				
				
				
			</div> <!-- .col-md-* -->			
		</div> <!-- .container -->
	</section> <!-- .row -->	
</c:if>




<c:if test="${not empty rounds}">
	<section id="previous-questions" class="row theme-game-content">
	
		<div class="container-fluid">
			<div class="col-md-12"> 
				<header class="section-header">
					<h1>Previous Questions</h1>
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
</c:if>




	<script>
	$(document).ready(function(){

		$('#commentForm').on( 'submit', function(e) {
			e.preventDefault(); 
			var subber = $(this).find('[type="submit"]');
			subber.text("Adding...").addClass("disabled");
		    $.post( '/game/comment', $('#commentForm').serialize(), function(data) {
		        	$("#comments").html(data); 
		        	$('#commentForm').each(function() {this.reset();});
					subber.text("Add").removeClass("disabled");
		       });
		});
		
		
		$("#stuckButton").click(function() {
			$("#questionHelp").removeClass("hidden");
		});
		
		$("#previousQsToggle").click(function() {
			if ($("#previousQuestionSummary").hasClass("hidden")) {
				$("#previousQuestionSummary").removeClass("hidden");
				$("#previousQuestionContent").addClass("hidden");
			}
			else {
				if ($('#previousQuestionContent').is(':empty')) {
					$.get("/game/previousQuestions",function(data) {
						$("#previousQuestionContent").html(data);
					});
				}
				$("#previousQuestionSummary").addClass("hidden");
				$("#previousQuestionContent").removeClass("hidden");
			}
		});	
	});
	
	
	</script>


</k:game_page>
