				
					<div class="well well-lg">
						<c:if test="${lastRound.question.imageId >= 0}">
						<div class="question-image">			
							<img src="/game/image?id=${lastRound.question.imageId}">
						</div> <!-- .question-image -->
						</c:if>

						<div class="question-text">
							<span class="big-text-in-circle">Q</span>
							<p><c:out value= "${lastRound.question.text}"/></p>
						</div> <!-- .question-text -->

						<div class="answer-text">
							<span class="big-text-in-circle">A</span>

							<ul class="list-unstyled answer-list">
							<c:forEach items="${lastRound.question.choiceList}" var="choice" varStatus="status" >
								<c:choose>
									<c:when test="${(status.index+1) == lastRound.question.answer}">
									<li class="correct-answer-highlight">
									</c:when>
									<c:otherwise>
									<li>
									</c:otherwise>
								</c:choose>
										<span class="list-bullet-icon">&nbsp;</span> 
										<span class="answer-choice-text"><c:out value="${choice}"/></span> 
										<span class="question-player-answer-group">
											<c:if test="${not empty lastRound.choiceToPlayer[status.index+1]}">
												<c:forEach items="${lastRound.choiceToPlayer[status.index+1]}" var="pname">
													<span class="question-player-answer">${pname}</span>
												</c:forEach>
											</c:if> 
										</span> <!-- .question-player-answer-group -->
									</li>
							</c:forEach>
								<li class="no-bullet">
									<span class="list-bullet-icon">&nbsp;</span> 
									<span>?:</span> 
									
									<span class="answer-choice-text">									
									I give up 
									</span> <!-- .answer-choice-text -->
									
									<span class="question-player-answer-group">	
										<c:if test="${not empty lastRound.choiceToPlayer[99]}">
											<c:forEach items="${lastRound.choiceToPlayer[99]}" var="pname">
												<span class="question-player-answer">${pname}</span>
											</c:forEach>
										</c:if> 
										<c:if test="${not empty lastRound.choiceToPlayer[100]}">
											<c:forEach items="${lastRound.choiceToPlayer[100]}" var="pname">
												<span class="question-player-answer">${pname}</span>
											</c:forEach>
										</c:if> 
									</span> <!-- .question-player-answer-group -->
								</li>
							</ul>
						</div> <!-- .question-text -->

						<hr>

						<p>
							<b>Time Limit: </b> <span>${lastRound.question.timeout } seconds</span>
						</p>
						<p>
							<b>Hint: </b> 
							<c:choose>
							<c:when test="${empty lastRound.question.hint}">None</c:when>
							<c:otherwise>'<c:out value='${lastRound.question.hint}'/>'</c:otherwise>
							</c:choose>
						</p>
						<p>
							<b>Explanation: </b> 
							<c:choose>
							<c:when test="${empty lastRound.question.explanation}">None</c:when>
							<c:otherwise>'<c:out value='${lastRound.question.explanation}'/>'</c:otherwise>
							</c:choose>
						</p>
<c:if test="${not empty lastRound.endDate}">						
						<a href="sharedQuestion.html?qid=145&amp;c=-1304733477" class="btn btn-default btn-sm hidden"><span class="glyphicon glyphicon-share-alt"></span> Share Question</a>
</c:if>						
					</div> <!-- .well -->

<c:if test="${not empty lastRound.endDate}">
					<h3 id="current-scoreboard">Player Scores</h3>


<c:set var="at" value="${lastRound.askerTurn}" />
		<c:choose>
			<c:when test='${at.reason=="QUESTION_RIGHT"}'>
				<h5>The question was correctly answered.  Players who answered correctly won their bets:</h5>
				<ul>  
				<c:forEach items="${lastRound.answerTurns}" var="kv" varStatus="status">
					<c:set var="t" value="${kv.value}" />
					<c:choose>
						<c:when test='${t.reason=="ANSWER_CORRECT"}'>
							<li><b>${t.playerName}</b> won ${t.bet} points</li>
						</c:when>
						<c:when test='${t.reason=="ANSWER_CORRECT_QUICKEST"}'>
							<li><b>${t.playerName}</b> won ${t.bet} points + 5 points bonus for the quickest answer</li>
						</c:when>
					</c:choose>
				</c:forEach>
				</ul>
				<c:if test='${at.points==0}'>
					<h5>No one answered incorrectly, so the author does not get any points.</h5> 
				</c:if>
				<c:if test='${at.points>0}'>
					<h5>The asker got the bets of those who answered incorrectly:</h5>
					<ul>  
					<c:forEach items="${lastRound.answerTurns}" var="kv" varStatus="status">
						<c:set var="t" value="${kv.value}" />
						<c:choose>
							<c:when test='${t.reason=="ANSWER_WRONG_LOST"}'>
								<li><b>${t.playerName}</b> bet ${t.bet} points that go to ${at.playerName}</li>
							</c:when>
						</c:choose>
					</c:forEach>
		<%-- 			<c:if test='${lastRound.numAnswersWrong>1}'> --%>
						<li><b>${at.playerName}</b> got a total of ${at.points} points</li>
					</ul>
				</c:if>
			</c:when>
			<c:when test='${at.reason=="QUESTION_WRONG"}'>
			<h5>The question was not correctly answered so the players who answered win their bets:</h5>
				<ul>  
				<c:forEach items="${lastRound.answerTurns}" var="kv" varStatus="status">
					<c:set var="t" value="${kv.value}" />
					<c:choose>
						<c:when test='${t.reason=="ANSWER_WRONG_WON"}'>
							<li><b>${t.playerName}</b> won ${t.bet} points</li>
						</c:when>
					</c:choose>
				</c:forEach>
				</ul>
			</c:when>
			<c:when test='${at.reason=="QUESTION_NONE"}'>
			<h5>Everyone gave up so all players won their bets:</h5>
				<ul>  
				<c:forEach items="${lastRound.answerTurns}" var="kv" varStatus="status">
					<c:set var="t" value="${kv.value}" />
					<li><b>${t.playerName}</b> won ${t.bet} points</li>
				</c:forEach>
				</ul>
			</c:when>
		</c:choose>

				<div class="row players-list">
					<c:forEach items="${players}" var="p"> 
						<div class="col-sm-6 col-md-3"> 
							<div class="thumbnail player-info">
								<c:set var="avatarSrc" value="${p.memberImage}" />
								<c:if test="${empty avatarSrc}">
									<c:set var="avatarSrc" value="//s3.amazonaws.com/trivolous/img/avatars/default-avatar.jpg" />
								</c:if>							
								<img class="avatar-sm inline-avatar" src="${avatarSrc}" alt="" width="50" height="50">
								<span class="player-name">${p.name}</span>
								<span class="label label-primary player-total"><small>Total</small> ${p.score}</span>
								<c:set var="turn" value="${lastRound.answerTurns[p.name]}" />
								<c:choose>
								<c:when test="${empty turn}">
									<span class="player-round-points">							
										<div class="author-points" title="Points awarded from other players">
											<span class="glyphicon glyphicon-certificate"></span>
											<div class="author-points-label">${lastRound.askerTurn.points}</div>
										</div> <!-- .player-bonus -->
									</span> <!-- .player-round-points -->
								</c:when>
								<c:when test="${turn.points > 0}">
									<span class="player-round-points">
										<c:if test="${turn.reason=='ANSWER_CORRECT_QUICKEST'}">						
											<div class="player-bonus" title="Player bonus">
												<span class="glyphicon glyphicon-certificate"></span>
												<div class="player-bonus-label">5</div>
											</div> <!-- .player-bonus -->
										</c:if>
										<c:choose>
										<c:when test="${turn.bet == 25}">
											<div class="game-token token-sm gold player-bid" title="Player bid">
												<div class="game-token-label">25</div>
											</div> <!-- .game-token -->
										</c:when>
										<c:when test="${turn.bet == 10}">
											<div class="game-token token-sm silver player-bid" title="Player bid">
												<div class="game-token-label">10</div>
											</div> <!-- .game-token -->
										</c:when>
										<c:when test="${turn.bet == 5}">
											<div class="game-token token-sm copper player-bid" title="Player bid">
												<div class="game-token-label">5</div>
											</div> <!-- .game-token -->
										</c:when>
										</c:choose>
									</span>
								</c:when>

								</c:choose>
							</div> <!-- .player-info -->
						</div> <!-- .col-md-* -->
					</c:forEach>
				</div>
				
	
</c:if>
			

				


				
