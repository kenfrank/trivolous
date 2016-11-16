<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="t"      uri="http://trivolous.com/functions" %>

<!DOCTYPE>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta http-equiv="Cache-Control" content="max-age=0">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT">
<meta http-equiv="Pragma" content="no-cache">
<meta name="viewport" content="width=device-width, initial-scale=1"> 

<title>Trivolous</title>

<link href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/global.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/game.css" rel="stylesheet">
<link href="//s3.amazonaws.com/trivolous/css/mediaqueries.css" rel="stylesheet">

<script src="//s3.amazonaws.com/trivolous/js/countDown2.js"></script></head>

<body onload="cd(${question.timeout})" id="ansbody"> 

<div class="container" id="main">
	<section class="row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12">
					
				<form id="formId" name="cd" class="create-question-form" method="post">

					<header class="hidden section-header">
						<h1>Trivolous Answer Session</h1>
					</header>
					
					<div id="status">
						<div id="status-time">
							<input id="secondsLeft" name="secondsLeft" readonly="readonly" type="text" value="${question.secondsLeft}"> 
							seconds
						</div> <!-- .status-time -->
					
						<div class="progress">
							<div id="progressbar" class="progress-bar progress-bar-info progress-bar-striped" style="width: 100%"></div>
						</div> <!-- .progress -->
					</div> <!-- #status -->
					
					<br>

					<div class="well well-lg">
						<c:if test="${question.imageId != null && question.imageId != -1}">
							<div class="question-image">			
								<img src="/game/image?id=${question.imageId}"/>
							</div> <!-- .question-image -->
						</c:if>

						<div class="question-text">
							<span class="big-text-in-circle">Q</span>
							<p>
							${t:text2html(question.text)}
							</p>
						</div> <!-- .question-text -->


						<div class="answer-text">
							<span class="big-text-in-circle">A</span>

							<ul class="list-unstyled answer-list">
							<c:forEach items="${question.choiceList}" var="choice" varStatus="status" >
								<li>
									<label for="answer${status.count}">	
										<div class="list-bullet-icon">								
											<span>&nbsp;</span>							
											<input id="answer${status.count}" class="hidden" name="answer" type="radio" value="${status.count}">
										</div>
									
										<span class="answer-choice-text">									
											 ${choice}
										</span> <!-- .answer-choice-text -->
									</label>
								</li>	
							</c:forEach>
								<li class="no-bullet">
									<label for="answer99">
										<div class="list-bullet-icon">
											<span>&nbsp;</span>
											<input id="answer99" class="hidden" name="answer" type="radio" value="99">
										</div>
										<span>?:</span> 
										<span class="answer-choice-text">									
											I give up 
										</span> <!-- .answer-choice-text -->
									</label>
								</li>
							</ul>
							
						</div> <!-- .answer-text -->
						
					</div> <!-- .well -->
					
					<div id="answer-session-footer">
						<input type="submit" name="action" id="submitBut" value="Submit" onclick="done()" class="btn btn-lg btn-primary" disabled>
					</div> <!-- #answer-session-footer -->
				</form>
				
			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->




	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script	src="//ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
	<script src="//s3.amazonaws.com/trivolous/bootstrap/js/ie10-viewport-bug-workaround.js"></script>
	<script>
	$(document).ready(function(){

		// update progress bar color to alert player at set intervals
		setInterval(function() {
	
			var progressWidth = parseInt($('.progress-bar')[0].style.width, 10);
			// alert(progressWidth);
	
			if (progressWidth < 76 && progressWidth > 51) {
				$('.progress-bar').removeClass('progress-bar-info').addClass('progress-bar-warning');
			}

			if (progressWidth < 51) {
				$('.progress-bar').removeClass('progress-bar-warning').addClass('progress-bar-danger');
			}

		}, 1000);

		

		// Highlight form radio buttons
		var $radioButtons = $('.create-question-form .answer-list input[type="radio"]');
		$radioButtons.on('click focus blur change', function() {
			$radioButtons.each(function() {
				$(this).closest('li').toggleClass('correct-answer-highlight', this.checked);
				$('#submitBut').removeAttr('disabled');
				// $(this).closest('.glyphicon').toggleClass('glyphicon-unchecked glyphicon-ok', this.checked);
			}); 
		});


		  
	});	
	</script>


</body>
</html>