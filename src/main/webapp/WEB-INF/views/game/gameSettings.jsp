<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:game_page>
	
	<section id="settings" class="game-nav-content row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12">
				
				<header class="section-header">
					<h1><span class="glyphicon glyphicon-cog"></span> Game Settings</h1>
				</header>

				<form method="post">
					<p>If you can no longer play the game you may quit.  But if you are just stuck making a question please
					see help for some ideas, and then if you are still stuck, you may skip your turn.</p>
					<button class="btn btn-danger" type="submit" name="quit"
					onClick="return confirm('Are you sure you want to quit?')">
					Quit Playing</button>
				</form>
				<hr>
				<form method="post" id="commentForm">
				<p>Send the game host, <b>${userSession.gameDesc.masterName}</b>, a private message</p>
				<div class="comment add-comment">
					<c:set var="avatarSrc" value="${userSession.playerData.memberImage}" />
					<c:if test="${empty avatarSrc}">
						<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
					</c:if>						
					<img class="avatar-sm" src="${avatarSrc}" alt="" width="30" height="30">
					<span class="commentor-name commentor-you">You</span>
					<span class="commentor-text form-inline">
						<input class="form-control add-comment-field" type="text" name="text" placeholder="Send game host a message">
						<button class="btn btn-primary btn-sm" type="button">Send</button>
					</span> <!-- .comment-text -->
				</div> <!-- .comment -->			
				</form>

			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	
	</section>
	
	<script>
	$(document).ready(function(){

		$('#commentForm button').click( function() {
		    $.post( '/game/hostcomment', $('#commentForm').serialize(), function(data) {
		        	$('#commentForm').each(function() {this.reset();});
					alert("Your message was sent to the host.");
		    });
		});
	});
	</script>		
</k:game_page>

