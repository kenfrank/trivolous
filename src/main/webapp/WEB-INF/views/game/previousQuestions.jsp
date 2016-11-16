<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:forEach items="${rounds}" var="lastRound">

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
					<c:if test="${not empty lastRound.comments}">
						<h3>Comments</h3>
						<c:forEach items="${lastRound.comments}" var="c">
								<div class="comment">
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
					</c:if>
				</div> <!-- .game-content -->	
</c:forEach>
				
