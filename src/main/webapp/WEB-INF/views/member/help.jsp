<%@taglib prefix="k" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://trivolous.com/functions" prefix="t"%>

<k:member_page>
	<jsp:body>
	

<div class="container" id="main">
	<section class="row theme-game-content">
		<div class="container-fluid">
			<div class="col-md-12 page-help">

				<h1>How to Play Trivolous</h1>
				<p class="text-center">Get the help you need and answers to common questions.</p>
				
				<div class="row">
					<div class="col-md-3">
				
						<div class="list-group help-toc">
							<a class="list-group-item scroll" href="#chapter-game-overview">Game Overview</a></li>
							<a class="list-group-item scroll" href="#chapter-getting-started">Getting Started</a></li>
							<a class="list-group-item scroll" href="#chapter-scoring">Scoring Points</a></li>
							<a class="list-group-item scroll" href="#chapter-game-tips">Game Tips</a></li>
							<a class="list-group-item scroll" href="#chapter-faq">FAQ - Frequently Asked Questions</a></li>
						</div> <!-- .help-toc -->
						
					</div> <!-- .col-md-* -->

					
					<div class="col-md-9">
						<hr id="chapter-game-overview" style="margin-top: 0;">
						
						<h2>Game Overview</h2>
						<div class="row game-overview">
							<div class="col-sm-4 col-md-4">
								<div class="col-content">
									<h3>Host</h3>
									<p>
									Starts the game by inviting Players and making the first question. 
									Also decides how many questions each player will make.
									</p>
								</div> <!-- .col-content -->
							</div> <!-- .col-md-* -->
							
							<div class="col-sm-4 col-md-4">
								<div class="col-content">
									<h3>Asker</h3>
									<p>
									The person making the question in each round. 
									The Host is	always the first Asker
									</p>
								</div> <!-- .col-content -->	
							</div> <!-- .col-md-* -->
							
							<div class="col-sm-4 col-md-4">	
								<div class="col-content">								
									<h3>Players</h3>
									<p>
									Everyone answering the question.
									</p>
								</div> <!-- .col-content -->
							</div> <!-- .col-md-* -->
							
							<div class="col-sm-4 col-md-4">		
								<div class="col-content">						
									<h3>Notices</h3>
									<p>
									An email or message sent via Facebook that tells you what to do next.
									</p>
								</div> <!-- .col-content -->
							</div> <!-- .col-md-* -->
							
							<div class="col-sm-4 col-md-4">	
								<div class="col-content">								
									<h3>Round</h3>
									<p>
									A Round is complete when every Player has answered the current question.
									</p>
								</div> <!-- .col-content -->
							</div> <!-- .col-md-* -->
							
							<div class="col-sm-4 col-md-4">	
								<div class="col-content">								
									<h3>Cycle</h3>
									<p>
									A Cycle is complete when all Players have taken a turn as the Asker and created a question.
									</p>
								</div> <!-- .col-content -->
							</div> <!-- .col-md-* -->								
						</div> <!-- .row -->
		

				
						<hr id="chapter-getting-started">
				
						<h2>Getting Started</h2>
						<p>YOU'RE THE HOST!</p>
						<p>Create a game by inviting other Players on Trivolous.com or
							via Facebook&rsquo;s Trivolous app, and make the first question. All
							Players will get a Notice.</p>
						<p>YOU&rsquo;RE INVITED!</p>
						<p>As soon as you accept, you will get to place your bet (see
							scoring below) and answer the question. All invited Playershave to
							accept, decline, or be removed by the Host for the game to keep
							going. Notices are sent to players who haven't responded.</p>
						<p>HOST! You might want to send a personal message if people
							aren&rsquo;t responding!</p>
						<p>YOU'VE ANSWERED!</p>
						<p>Wait until everyone has answered. After all Players have
							answered, a Notice is sent with everyone's scores.</p>
						<p>NEXT ROUND!</p>
						<p>The next Player in the list becomes the new Asker and makes
							the next question.</p>
						<p>WE'VE ALL MADE A QUESTION!</p>
						<p>A new Cycle starts and questions keep being asked and answered
							until all the Cycles are done.</p>
						<p>WE'RE DONE!</p>
						<p>A Notice is sent with the final scores! Posturing and shaming
							are optional :)</p>
						<p>FIRST TIME PLAYERS!</p>
						<p>Now that you&rsquo;ve played a game you can be the HOST for a
							new game! Think of a fun topic, invite some Players(new and/or
							existing players) and continue the fun!</p>



						<hr id="chapter-scoring">					
					
						<h2>Scoring Points</h2>
						<p>The scoring is designed to reward making good questions that
						other players can answer and answering correctly. The Player with
						the highest score at the end wins.</p>
						<p>Before you answer a question you will be asked to bet 5, 10 or
							25 points</p>
						<p>YOU GOT IT RIGHT!</p>
						<p>You win the points you bet.  If you answered the quickest, and you were not the only one to get it right, you get a 5 point bonus.</p>
						<p>YOU GOT IT WRONG BUT OTHER PLAYERS GOT IT RIGHT!</p>
						<p>You lose your bet to the Asker</p>
						<p>NO ONE GOT IT RIGHT!</p>
						<p>Players win their bets even though they got it wrong. ASKER -
							you do not want this to happen since other players will gain points
							and you will not!</p>
						<p>EVERYONE GAVE UP!</p>
						<p>All Players win their bets. ASKER - your fellow players
							rejected your question. You do not want this to happen since
							everyone else will gain points except you!</p>



						<hr id="chapter-game-tips">	
				
						<h2>Game Tips</h2>
						<h3>How Do I Win? Here Are Some Tips!</h3>
				
						<p>YOU'RE THE ASKER!</p>
						<p>You will get the highest score by making questions that are
							hard enough that some people get them wrong, but not SO hard that NO
							ONE gets it right. The optimal score is achieved when one player
							gets it right and all others get it wrong.</p>
						<p>YOU'RE A PLAYER!</p>
						<p>You want to make an educated guess since you want to add to
							your points. You don't lose points if you get it wrong, and you will
							win points if no one gets it right. If you are neck and neck with
							the Asker you may not wish to randomly guess on their question as if
							you are wrong you could give them points and put them ahead of you.
							But if the Asker is far ahead or behind you it might be worth a
							guess.</p>


					
						<hr id="chapter-faq" class="hidden-divider">
				
						<div class="well well-lrg faq-list">
							<h2><span class="glyphicon glyphicon-question-sign"></span> FAQ <small>- Frequently Asked Questions</small></h2>
							<p>
							Select any FAQ below to reveal help and answers for common questions.
							</p>
				
							<h3 class="slide-trigger">How do I start my own game with my friends?</h3>
							<div class="slide-content">
								<p>
								After you've been invited to and play another game you will be
									able to start your own as the host. 
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">I don't see any emails from Trivolous!</h3>
							<div class="slide-content">
								<p>
								First check your spam/junk email folder. Whether it's
								there or not, most email providers will allow you to mark certain
								email addresses as safe. Go in to your account settings and add
								notifier@trivolous.com to your safe emails. However, don't email
								notifier@trivolous.com as it does not accept emails.
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">How do I keep the game moving?</h3>
							<div class="slide-content">
								<p>
								Daily email reminders are sent to players who need to take
								their turn, but sometimes a game can stall. Comments can also be
								used to nudge players along. If the player is unavailable for some
								period, the game can be paused and then reactivated. If the player
								can no longer participate in the game, he may quit, or the host can
								remove them.
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">I'm stuck and can't think of a good question!</h3>
							<div class="slide-content">
								<ol>
									<li>Troll the internet and wikipedia for ideas.</li>
									<li>Your facebook stream, Pinterest, and Instagram can also be
										good sources of inspiration.</li>
									<li>Try asking a question about yourself</li>
									<li>Try making a question using an image</li>
									<li>Try creating a true/false question</li>
									<li>Don't worry that your question isn't good, you will find they are always much better than you think.</li>
								</ol>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">What if a question was created with an incorrect or disputed answer?</h3>
							<div class="slide-content">
								<p>
								This is possible, even by mistake. It's a social game so send
								a message and discuss it. There is no advantage to having incorrect
								answers since no points are given for a question that no one answers
								correctly.
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">I'm trying to sign in on a computer that I share with another player and I am having trouble!</h3>
							<div class="slide-content">
								<p>
								In the top right corner is a tab with the player's name on it.
								Click on that and then click on sign out. If you sign in via
								Facebook, even on the Trivolous.com website, you need to make sure
								that the other player is ALSO signed out of Facebook, even if they
								don't have Facebook open. Then you can sign in as yourself.
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">How do I quit?</h3>
							<div class="slide-content">
								<p>
								There is a quit button in the game menu. If you are stuck
								making a question please take a look at the advice above. If there
								are other issues you may want to check with the Host for help.
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">What's with the name?</h3>
							<div class="slide-content">
								<p>
								It's a combination of 'trivia' and 'frivolous', like the game.
								</p>
							</div> <!-- .toggle-content -->

							<h3 class="slide-trigger">Is Internet Explorer 8 (IE8) supported?</h3>
							<div class="slide-content">
								<p>
								Sorry it is not.  Trivolous was built using modern web technologies which are not supported
								by IE8.  IE8 is going end of life in 2015 so we decided not to go too far out of our way to 
								support it.  If possible upgrade your browser as you are missing out on other great
								websites too.
								</p>
							</div> <!-- .toggle-content -->

			
							<h3 class="slide-trigger">How do I contact you guys?</h3>
							<div class="slide-content">
								<p>
								Shoot us an email at <a href="mailto:admin@trivolous.com" target="_blank">admin@trivolous.com</a>.  
								We would love to hear your feedback!
								</p>
								
								<div class="text-center">
									<p>
									Also, be sure to follow us and reach out to us on social media:
									</p>
									
									<p>
									<a href="https://twitter.com/Trivolous" target="_blank"><img src="//s3.amazonaws.com/trivolous/img/icons/twitter.gif" width="50" height="50" alt="Twitter"></a>

									<a href="https://www.facebook.com/pages/Trivolous/1666305153600742" target="_blank"><img src="//s3.amazonaws.com/trivolous/img/icons/facebook.gif" width="50" height="50" alt="Facebook"></a>
									</p>
								</div>
							</div> <!-- .toggle-content -->
					
						</div> <!-- .faq-list -->

					</div> <!-- .col-md-* -->
				</div> <!-- row -->
				
			</div> <!-- .col-md-* -->
		</div> <!-- .container-fluid -->
	</section> <!-- .row -->	
</div> <!-- #main -->


	<script>
	$(document).ready(function() {
		
		$('.page-help h2').append('<a class="back-to-top scroll" href="#main"><span class="glyphicon glyphicon-chevron-up"></span> Back to top</a>');
		$('.faq-list .slide-trigger').prepend('<span class="glyphicon glyphicon-plus"></span> ');
		
		$('.faq-list .slide-content').hide();

		$(document).on('click touchstart', '.slide-trigger', function(e) {
		  e.preventDefault();
		  $(this).next('.slide-content').slideToggle('fast');
		  $(this).children('.glyphicon').toggleClass('glyphicon-minus', 'glyphicon-plus');
	//	  return false;
			e.stopPropagation();
		});
		
		$(document).on('click touchstart', '.slide-close', function(e) {
		  e.preventDefault();
		  $(this).parent('.slide-content').slideUp('fast');
	//	  return false;
			e.stopPropagation();
		});	

		// for smooth scrolling of internal anchors
		$(".scroll").click(function (event) {
			event.preventDefault();
			//calculate destination place
			var dest = 0;
			if ($(this.hash).offset().top > $(document).height() - $(window).height()) {
				dest = $(document).height() - $(window).height();
			} else {
				dest = $(this.hash).offset().top;
			}
			//go to destination
			$('html,body').animate({
				scrollTop: dest
			}, 600, 'swing');
		});

		
	});
	</script>


    </jsp:body>
</k:member_page>