<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<t:basic_page>
	<jsp:attribute name="header">
		<meta property="og:title"              content="${game.masterName}'s Trivolous Game - ${game.name}" />
		<meta property="og:description"        content="Come play the casual conversational insider trivia game!" />
		<meta property="og:image"              content="https://s3.amazonaws.com/trivolous/img/trivolous_icon_200.png" />			
    </jsp:attribute>
	<jsp:body>
	<nav class="navbar navbar-default navbar-fixed-top" id="masthead">
		<div class="container-fluid">
			<div class="navbar-header">
				<a href="#" class="pull-left"><img id="logo" src="//s3.amazonaws.com/trivolous/img/logos/trivolous_logo_trans_bg.png" alt="Trivolous" height="42"></a>
			</div>
		</div>
	</nav>


<div class="container" id="main">
	<section class="row theme-game-content">
		<div class="container-fluid">
		
			<div class="col-md-12 error-page-content">

			<h1>Open Link Registration</h1>
<c:choose>
	<c:when test="${empty game || (game.status!='INVITING' && game.status!='REGISTERING') }">
		<p>Sorry this game is no longer open for registration.  Why not  
		<a href="/member/masterCreate">start your own</a>?</p> 
	</c:when>
	<c:otherwise>
			<div class="well">
				<header class="section-header">
					<h1><span class="glyphicon glyphicon-info-sign"></span> Game Info</h1>
				</header>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-tag"></span> Name</b> ${game.name}
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-user"></span> Mastered by</b> ${game.masterName}
				</p>
				<p>
					<b class="info-label"><span class="glyphicon glyphicon-edit"></span> Description</b> "<i><c:out value="${game.description}"/></i>"
				</p>
			</div>
			<a href="/member/gameList" class="btn btn-primary btn-lg">Join the game!</a> 
			<p>(if you are not logged in or registered you will be directed to that first)</p>
	</c:otherwise>
</c:choose>

		</div>
	</div>
	</section>
</div>
	</jsp:body>


</t:basic_page>