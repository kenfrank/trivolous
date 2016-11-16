<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:member_page>
	
	

<div class="container" id="main">

	<ol class="list-unstyled list-inline progress-steps">
	  <li class="active"><b>1</b> <small>Describe Your Game</small></li>
	  <li class="active"><b>2</b> <small>Make first question</small></li>
	  <li><b>3</b> <small>Invite Players</small></li>
	</ol>

	<section class="row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12">
			
				<!-- <header class="section-header"> -->
					<h1>Creating Game</h1>
					<h2 class="h2 text-center"><b>Step 2:</b> Make the first question</h2>
					<p class="text-center">You create the first question now so that players can answer it as soon as they join the game</p>
				<!-- </header> -->
					
<%@include file="_questionForm.jsp"%>			
			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->

</k:member_page>