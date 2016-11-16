<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:game_page>
	
	<section id="info" class="game-nav-content row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12"> 
				<header class="section-header">
					<h1><span class="glyphicon glyphicon-bookmark"></span> My Question</small></h1>
					<p>You can queue up a question here.  It will be played automatically when it is your turn
					to make a question.  You may come back to edit or remove your question anytime before then.</p>
				</header>
<%@include file="_questionForm.jsp"%>			
		
			</div> <!-- col-md-* -->
		</div> <!-- container-fluid -->
	</section> <!-- row -->

</k:game_page>
