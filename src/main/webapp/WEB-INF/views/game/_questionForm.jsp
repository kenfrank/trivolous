
<div id="showImage" class="question-image hidden">
	<img id="pic" src="/game/image"/>
	<a id="removePic"><span class="glyphicon glyphicon-trash"></span> Remove</a>
</div>

<div class="add-photo-button"><span class="glyphicon glyphicon-camera"></span> Add photo</div>
<form id="myAwesomeDropzone" action="/game/image" enctype="multipart/form-data" method="post" class="hidden dropzone">
	<div class="fallback">
	    <input name="file" type="file" multiple />
	    <button type="submit">Submit</button>
	</div>
</form>




      				<form:form modelAttribute="question" id="questionForm" class="create-question-form" method="post" >
					<form:hidden id="id" path="id"/>
					<form:hidden id="imageId" path="imageId"/>
					<div class="well well-lg">

						<div class="question-text">
							<span class="big-text-in-circle">Q</span>							
							<div class="form-group">
								<label class="sr-only" for="input01">Question:</label>
								<form:textarea cssClass="form-control question-input" id="input01" path="text" rows="4" placeholder="Type your question. Make it a good one!"></form:textarea>
							</div>
						</div> <!-- .question-text -->


						<div class="answer-text">
							<span class="big-text-in-circle">A</span>

							<ul id="choices" class="list-unstyled answer-list">
								<c:forEach items="${question.choiceList}" var="choice" >
									<li>
										<div class="list-bullet-icon">						
											<span class="glyphicon glyphicon-unchecked choiceadd"></span>								
										</div>
										<span class="answer-choice-text">									
											<textarea class="form-control" name="choiceList" rows="1" required>${choice}</textarea>
										</span> <!-- .answer-choice-text -->
										<span class="glyphicon glyphicon-remove choiceremove"></span>								
									</li>	
								</c:forEach>							
							</ul>
							
							<div class="form-group">
								<form:hidden id="answer" path="answer"/>
								<button id="addbutton" type="button" class="btn btn-default btn-xs" onclick="addChoice();"><span class="glyphicon glyphicon-plus"></span> Add choice</button>
							</div>
							
						</div> <!-- .answer-text -->

						<hr>
						
						<div class="form-group form-inline">
							<label for="inputTimeout">Time Limit:</label>
							<form:select cssClass="form-control" id="inputTimeout" path="timeout">
								<form:option value="15">15 seconds</form:option>
								<form:option value="30">30 seconds</form:option>
								<form:option value="45">45 seconds</form:option>
								<form:option value="60">60 seconds</form:option>
								<form:option value="90">90 seconds</form:option>
								<form:option value="120">120 seconds</form:option>
							</form:select>
						</div>						
						
						<div class="form-group">
							<label  for="inputHint">Hint:</label>
							<form:input id="inputHint" path="hint" cssClass="form-control" type="text" value="" maxlength="255"/>
							<span class="help-block">Optional - shown before question is given.</span>
						</div>
						<div class="form-group">
							<label  for="inputExplain">Explanation:</label>
							<form:textarea id="inputExplain" path="explanation" cssClass="form-control"  rows="3"></form:textarea>
							<span class="help-block">Optional - shown after question is answered.</span>
						</div>
						
					</div> <!-- .well -->

					<div class="alert alert-info alert-with-icon">
						<span class="glyphicon glyphicon glyphicon-alert"></span> 			
						<p>
						<b>You will need to verify your question by answering it correctly.</b> 
						Click verify to preview your question. After answering correctly you may edit it again, or submit it.
						</p>
					</div> <!-- .alert -->
					
					<input type="submit" name="action" value="Verify" class="btn btn-lg btn-primary">
					<c:choose>
					<c:when test="${userSession.gameDesc.status =='CREATING'}">
						<a class="btn btn-lg btn-default" href="/game/create/description">Back</a>
					</c:when>
					<c:otherwise>
						<input type="submit" name="delete" value="Delete" class="btn btn-lg btn-default"> 
					</c:otherwise>
					</c:choose>
				</form:form>

<!-- Button trigger modal -->

	<c:if test="${(not empty verified) && verified}">
		<!-- Modal -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">You got it right!</h4>
		      </div>
		      <div class="modal-body">
				Of course you did, it's your question!  You may submit it now, or go back and edit it.  
				Once the question is sent to the other players you will not be able to edit it again.				
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Edit</button>
				<a class="btn btn-success" id="submitVerified" href="submit">Submit</a>
		      </div>
		    </div>
		  </div>
		</div>		
	</c:if>
	<c:if test="${(not empty verified) && (not verified)}">
		<!-- Modal -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title" id="myModalLabel">You got it wrong!</h4>
		      </div>
		      <div class="modal-body">
				You need to be able to answer your own question before the others can see it.  
				Maybe you accidently selected the wrong answer?  Edit it and try again.	
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Edit</button>
		      </div>
		    </div>
		  </div>
		</div>		
	</c:if>
			
<ul id="choiceRef" style="display:none">
	<li>
		<div class="list-bullet-icon">						
			<span class="glyphicon glyphicon-unchecked choiceadd"></span>								
		</div>
		<span class="answer-choice-text">									
			<textarea class="form-control" name="choiceList" rows="1"></textarea>
		</span> 
		<span class="glyphicon glyphicon-remove choiceremove"></span>								
	</li>	
</ul>
	
<script>

function formatChoices() {
	var count = $("#choices").children().length;
	if (count==2) {
		// hide removes
		$('#choices .choiceremove').hide();
	}
	if (count>=5) {
		$('#addbutton').hide();
		
	}
	var answer = $('#answer').val();
	if (answer >= 1) {
		$("#choices .choiceadd").eq(answer - 1).click();
	}
}

function setupChoiceClick() {
	$('#choices .choiceadd').click( function() {
			$('#choices .choiceadd').each(function() { 
				if ($(this).is('.glyphicon-check')) {
					$(this).closest('li').removeClass('correct-answer-highlight');
					$(this).removeClass('glyphicon-check').addClass('glyphicon-unchecked');
				} 
			});
			$(this).closest('li').addClass('correct-answer-highlight');
			$(this).removeClass('glyphicon-unchecked').addClass('glyphicon-check');
		}); 
	$('#choices .choiceremove').click( function() {
			$(this).closest('li').remove();	
			var count = $("#choices").children().length;
			if (count==2) {
				// hide removes
				$('#choices .choiceremove').hide();
			}
			if (count==4) {
				$('#addbutton').show();
			}
		});
}

function addChoice() {
	$('#choices').append($('#choiceRef').html());
	setupChoiceClick();
	var count = $("#choices").children().length;
	if (count==3) {
		// show removes
		$('#choices .choiceremove').show();
	}
	if (count>=5) {
		$('#addbutton').hide();
	}
}

function submit() {
	var count = $("#choices").children().length;
	$('#counter').val(count);
	var answerli = $('#choices .glyphicon-check').closest('li');
	if (answerli.length == 0) {
		// need to select an answer!
	} 
	else {
		var answer = answerli.prevAll().length;	
		$('#answer').val(answer);
	}
}

$(document).ready(function(){


	// toggle image upload
	$(document).on('click touchstart', '.add-photo-button', function(e) {
		e.preventDefault();

		// $(this).next('.toggle-content').toggle();
		 $('.dropzone, .upload-region').toggleClass('hidden')

		//	  return false;
		e.stopPropagation();
	});


	setupChoiceClick();

	
	$('#myModal').modal('show');
	$('#submitVerified').click(function(e) {
		var subtext =  "Submitted...";
		if ($(this).text() == subtext) {
			e.preventDefault();
		}
		else {
			$(this).text(subtext);
			$(this).addClass('disabled');
		}
	});
	
	formatChoices();
	
	$('form input[type=submit]').click(function(e) {
		var answerli = $('#choices .glyphicon-check').closest('li');
		if (answerli.length == 0) {
			// need to select an answer!
			e.preventDefault();
			alert("Please select the correct answer by clicking on the checkbox");
		} 
		else {
			var answer = answerli.prevAll().length + 1;	
			$('#answer').val(answer);
		}
	});
	
	$('#createCancel').click( function(e) {
		if (confirm("Would you like to delete this game?")) {
			window.location.replace("/game/create/delete");
		} 
		else {
			window.location.replace("/member/gameList");
		}
	});
	
});
</script>

<script>
$(document).ready(function () {
    Dropzone.autoDiscover = false;
    //Simple Dropzonejs 
    $("#myAwesomeDropzone").dropzone({
    	//forceFallback: true, // for testing
        url: "/game/image",
        createImageThumbnails : false,
        maxFiles: 1,
        acceptedFiles: "image/*",
        success: function (file, response) {
            console.log("loaded pic id=" + response.id);
            $("#imageId").val(response.id);
			$("#pic").attr("src", "/game/image?id="+response.id);
			$("#showImage").removeClass('hidden');
			$("#myAwesomeDropzone").hide();
			$(".add-photo-button").addClass('hidden');
            this.removeAllFiles();
			// could call disable on this also to remove listeners etc...
        },
        error: function (file, response) {
            alert("Could not load picture");
            this.removeAllFiles();
            console.log("FAILED! "); 
        } 
    });
    $("#removePic").click(function() {
    	$.post( "/game/image/delete", {"id": $("#imageId").val()}). done ( function( data ) {
				$("#showImage").addClass('hidden');
				$("#myAwesomeDropzone").show();
				$(".add-photo-button").removeClass('hidden');
	            $("#imageId").val(-1);
    		});
    });
    if ($("#imageId").val() >= 0) {
    	$("#showImage").removeClass('hidden');
		$("#pic").attr("src", "/game/image?id="+$("#imageId").val());
		$(".add-photo-button").addClass('hidden');
    }
});
</script>

<script src="//s3.amazonaws.com/trivolous/js/dropzone.js"></script>	

