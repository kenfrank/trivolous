<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>

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
						<span class="comment-date">
						<span class="glyphicon glyphicon-time" title="<fmt:formatDate type="both" dateStyle="medium" timeStyle="short" value="${c.created}" />"></span> ${t:timeAgo(c.created)} 
						<!-- <fmt:formatDate type="both" dateStyle="medium" timeStyle="short" value="${c.created}" /> -->
						</span> <!-- .comment-date -->
					</div> <!-- .comment -->
</c:forEach>
					