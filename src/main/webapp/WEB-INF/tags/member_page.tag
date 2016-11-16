<%@tag description="User Page template" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:basic_page>

	<nav class="navbar navbar-default navbar-fixed-top" id="masthead">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar-collapse">
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span> 
					<span class="icon-bar"></span>
				</button>
				<a href="/member/gameList" class="pull-left"><img id="logo" src="//s3.amazonaws.com/trivolous/img/logos/trivolous_logo_trans_bg.png" alt="Trivolous" height="42"></a>
			</div>
	
			<!-- Collect the nav links, forms, and other content for toggling -->
			<div id="navbar-collapse" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="/member/gameList"><span class="glyphicon glyphicon-home"></span> Home</a></li>
					<li><a href="/member/masterCreate"><span class="glyphicon glyphicon-plus"></span> New Game</a></li>
					<li><a href="/member/help"><span class="glyphicon glyphicon-question-sign"></span> Help</a></li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button"> <span
							class="glyphicon glyphicon-user"></span> ${userSession.memberFirstName} <span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="/member/editProfile">Profile</a></li>
							<!-- <li class="divider"></li> -->
							<!-- <li><a href="/member/help">Help</a></li> -->
							<li class="divider"></li>
							<li><a id="signoff" href="/member/signoff">Sign Out</a></li>
						</ul></li>
				</ul>
			</div>
		</div>
	</nav>

    <jsp:doBody/>
    



</t:basic_page>

