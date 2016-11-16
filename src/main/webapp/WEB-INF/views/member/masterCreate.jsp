<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<k:member_page>

<div class="container" id="main">

	<ol class="list-unstyled list-inline progress-steps">
	  <li class="active"><b>1</b> <small>Describe Your Game</small></li>
	  <li><b>2</b> <small>Make first question</small></li>
	  <li><b>3</b> <small>Invite Players</small></li>
	</ol>

	<section class="row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12">

				<!-- <header class="section-header"> -->
					<h1>Creating Game</h1>
					<h2 class="h2 text-center"><b>Step 1:</b> Describe Your Game</h2>
				<!-- </header> -->
			
				<form:form id="command" method="post" modelAttribute="gameDescriptionData">
	 
					<div class="form-group">
						<label>Name</label>
						<form:input id="name" path="name" type="text" maxlength="40" class="form-control"></form:input>
					</div>
	 
					<div class="form-group form-inline">
						<label>Number of questions made by each player </label>
						<form:input class="form-control" id="totalAuthorCycles" path="totalAuthorCycles" type="number" min="1" max="4"/>
					</div>
			
					<div class="form-group">
						<label>Description</label>
						<form:textarea class="form-control" id="description" path="description" rows="5" placeholder="Type a brief summary of the theme of the game or provide additional info for the players"/>
					</div>
	
					<div class="hidden form-group">
						<label>Privacy</label>
						<form:select id="privacy" path="privacy">
							<option value="PLAYERS_ONLY">PLAYERS ONLY</option>
							<option value="FREINDS_ONLY">FREINDS_ONLY</option>
							<option value="PUBLIC">PUBLIC</option>
						</form:select>
					</div>
					<c:if test="${gameDescriptionData.id >= 0}">
						<button type="submit" name="delete" class="btn btn-lg btn-primary">Delete</button>
					</c:if>
					<button type="submit" id="submit" name="action" class="btn btn-lg btn-primary">Create Game <span class="glyphicon glyphicon-chevron-right"></span></button>
				</form:form>
				
			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->

	<script>
	$(document).ready(function(){

		// Ken, check below - might be a little redundant, not sure.
		
		
		// disabling continue button until name is entered.
		
		var name = $(this).val();
	
		// Form field validation
		if(name.length == 0) {
			// $('#submit').attr('disabled', 'disabled');
			$('#submit').addClass('disabled');
		} else {
			// $('#submit').attr('disabled', '');
			$('#submit').removeClass('disabled');
		}		
		
		$('#name').keyup(function() {
			var name = $(this).val();
		
			// Form field validation
			if(name.length == 0) {
				$('#submit').addClass('disabled');
			} else {
				$('#submit').removeClass('disabled');
			}
		});

		$('#name').keyup();
		
		
	});
	</script>


</k:member_page>
