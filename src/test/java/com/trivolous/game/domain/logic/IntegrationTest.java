package com.trivolous.game.domain.logic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.trivolous.game.data.ActiveGameData;
import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.InviteData;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.data.QuestionData;
import com.trivolous.game.data.RoundData;
import com.trivolous.game.data.TurnForm;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Player.Status;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.domain.logic.PlayerService.InviteStatus;
import com.trivolous.game.notify.MySenderTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/springapptest-main.xml")
public class IntegrationTest {
	@Autowired
	private MemberService memberSerivce;
	@Autowired
	private NewGameService gameService;	
	@Autowired
	private PlayerService playerService;	
	protected final Log logger = LogFactory.getLog(getClass());

	public long addMember(String name) {
		if (memberSerivce.findMemberByEmail(name+"@tester.com") == null) {
			MemberData m = new MemberData();
			m.setEmail(name+"@tester.com");
			m.setFirstName(name);
			m.setLastName("Tester");
			m.setPassword("-");
//			m.setFacebookId("FB-"+name);
			memberSerivce.updateMember(m);
		}
		
		assertEquals(name,memberSerivce.findMemberByEmail(name+"@tester.com").getFirstName());
		long id = memberSerivce.findMemberByEmail(name+"@tester.com").getId();
		assertEquals("Tester", memberSerivce.findMemberById(id).getLastName());
//		assertEquals(name+"@tester.com", memberSerivce.findMemberByFacebookId("FB-"+name).getEmail());
		return id;
	}
	
	public long register(String name) {
		String email = name.toLowerCase()+"@tester.com";
		MemberData m = memberSerivce.findMemberByEmail(email);
		if (m == null) {
			m = new MemberData();
			m.setEmail(email);
			m.setFirstName(name);
			m.setLastName("Tester");
			m.setPassword("-");
			m.setFacebookId("FB-"+name);
			memberSerivce.updateMember(m);
		}
		assertEquals(name,memberSerivce.findMemberByEmail(name+"@tester.com").getFirstName());
		long id = memberSerivce.findMemberByEmail(name+"@tester.com").getId();
		assertEquals("Tester", memberSerivce.findMemberById(id).getLastName());
		return id;
	}
	

	public long createGame() {
		MemberData master = memberSerivce.findMemberByEmail("ken@tester.com");
		GameDescriptionData game = new GameDescriptionData();
		game.setDescription("Test Game Description");
		game.setName("Test Game");
		game.setPrivacy(Game.PRIVACY.PUBLIC);
		game.setTotalAuthorCycles(1);
		game.setAllowOpenRegistration(false);
		long gameId = gameService.createNewGame(game, master.getId());
		
		GameDescriptionData g = gameService.getGameDescription(gameId);
		assertEquals("Test Game",g.getName());
		assertEquals(master.getId(),g.getMasterId().longValue());
		assertEquals("Ken",g.getMasterName());
		assertEquals("Ken",playerService.getPlayersInGame(g.getId()).get(0).getName());
		return gameId;
	}

	public void makeQuestion(long playerId, int qnum) {
		QuestionData question = new QuestionData();
		question.setText("Q"+qnum);
		question.setAnswer(1);
		question.setTimeout(20);
		question.setChoice1("Choice1");
		question.setChoice2("Choice2");
		question.setChoice3("Choice3");
		question.setNumChoices(3);
		playerService.submitQuestion(playerId, question);
	}
	
	public void answerQuestion(long playerId, int qnum, int answer, int time) {
		PlayerData player = playerService.findPlayerById(playerId);
		assertEquals(Status.NEEDS_TO_ANSWER, player.getStatus());		

		QuestionData q = playerService.startTimedAnswerSession(playerId,5);
		player = playerService.findPlayerById(playerId);
		assertEquals(Status.ANSWERING, player.getStatus());		
		
		assertEquals("Q"+qnum,q.getText());
		TurnForm tf = new TurnForm();
		tf.setAnswer(answer);
		tf.setSecondsLeft(q.getTimeout() - time);
		playerService.submitAnswer(playerId, tf);

		// dont check answered status here, because if last player it will be different.
	}
	
	private InviteData findInviteByMember(long memberId, long gameId) {
		for (InviteData inviteData : gameService.getInvites(gameId)) {
			if (inviteData.getMemberId() != null && inviteData.getMemberId() == memberId) {
				return inviteData;
			}
		}
		return null;
	}

	private InviteData findInviteByEmail(String email, long gameId) {
		for (InviteData inviteData : gameService.getInvites(gameId)) {
			if (inviteData.getEmail() != null && inviteData.getEmail().equals(email)) {
				return inviteData;
			}
		}
		return null;
	}
	
	@Test
	public void test1() {
		long memberIdKen = addMember("Ken");
		long gameId = createGame();
		HashMap<String, Long> idm = new HashMap<String, Long>();  // member id map
		HashMap<String, Long> idp = new HashMap<String, Long>();  // player id map
		
		// make first question
		PlayerData playerKen = playerService.findPlayer(gameId, memberIdKen);
		makeQuestion(playerKen.getId(), 1);
		
		// invite players
		playerService.invitePlayer(gameId, "amy@tester.com");
		List<PlayerData> players = playerService.getPlayersInGame(gameId);
		assertEquals(1,players.size());
		List<InviteData> invites = gameService.getInvites(gameId);
		assertEquals(1,invites.size());
		
		// start game -- how to verify notification?
		/////////////////////////////////////////////////////////////////////////////////////////////////
		gameService.beginGameInvitation(gameId);
		// add players here to test openreg/facebook reg.
		// register - Bob
		long memberIdBob = register("Bob");
		playerService.inviteMember(gameId, memberIdBob);
		
		// verify invited
		InviteData foundInvite = findInviteByMember(memberIdBob, gameId);
		assertNotNull(foundInvite);
		assertNull(playerService.findPlayer(gameId, memberIdBob));
		MemberData bobMember = memberSerivce.findMemberById(memberIdBob);
		assertEquals(foundInvite.getEmail(), bobMember.getEmail());
		assertEquals(foundInvite.getFacebookId(), bobMember.getFacebookId());
		assertEquals(foundInvite.getGameId().longValue(), gameId);
		assertEquals(foundInvite.getName(), bobMember.getFirstName() + " " + bobMember.getLastName());
		
		// bob joins game
		playerService.registrationResponse(memberIdBob, gameId, true);

		// bob no longer in invite list.
		assertNull(findInviteByMember(memberIdBob, gameId));
		// bob is player now.
		PlayerData playerBob = playerService.findPlayer(gameId, memberIdBob);
		assertNotNull(playerBob);
		
		playerBob = playerService.findPlayerById(playerBob.getId());
		assertEquals(Status.NEEDS_TO_ANSWER, playerBob.getStatus());
		
		// get/answer question - Bob
		answerQuestion(playerBob.getId(), 1, 1, 10);
		playerBob = playerService.findPlayerById(playerBob.getId());
		assertEquals(Status.ANSWERED, playerBob.getStatus());				
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		gameService.beginGameRegistration(gameId);		
		
		long memberIdAmy = register("Amy");
		
		// verify invited
		foundInvite = findInviteByMember(memberIdAmy, gameId);
		assertNotNull(foundInvite);
		assertNull(playerService.findPlayer(gameId, memberIdAmy));
		MemberData amyMember = memberSerivce.findMemberById(memberIdAmy);
		assertEquals(foundInvite.getEmail(), amyMember.getEmail());
		assertEquals(foundInvite.getFacebookId(), amyMember.getFacebookId());
		assertEquals(foundInvite.getGameId().longValue(), gameId);
		assertEquals(foundInvite.getName(), amyMember.getFirstName() + " " + amyMember.getLastName());		
		
		playerService.registrationResponse(memberIdAmy, gameId, true);
		assertNull(findInviteByMember(memberIdAmy, gameId));
		PlayerData playerAmy = playerService.findPlayer(gameId, memberIdAmy);
		assertNotNull(playerAmy);
		assertEquals(Status.NEEDS_TO_ANSWER, playerAmy.getStatus());
		
		// get/answer question - Amy
		answerQuestion(playerAmy.getId(), 1, 1, 20);
		playerAmy = playerService.findPlayerById(playerAmy.getId());
	
		idm.put("Ken", memberIdKen);
		idm.put("Amy", memberIdAmy);
		idm.put("Bob", memberIdBob);
		idp.put("Ken", playerKen.getId());
		idp.put("Amy", playerAmy.getId());
		idp.put("Bob", playerBob.getId());
		
		// get last turn information!
		assertEquals(playerService.findPlayer(gameId, idm.get("Ken")).getStatus(), Player.Status.WAITING_FOR_QUESTION);
		assertEquals(playerService.findPlayer(gameId, idm.get("Bob")).getStatus(), Player.Status.NEEDS_TO_MAKE_QUESTION);
		assertEquals(playerService.findPlayer(gameId, idm.get("Amy")).getStatus(), Player.Status.WAITING_FOR_QUESTION);

		ActiveGameData agame = gameService.getActiveGameData(gameId);
		assertEquals(1, agame.getAskerCycle().intValue());
		assertEquals(2, agame.getRoundNumber().intValue());
	
		RoundData round = gameService.getRound(gameId, 1);
		assertEquals(1, round.getRoundNumber().intValue());
		assertEquals(playerKen.getName(), round.getAskerPlayerName());
		assertEquals(2, round.getAnswerTurns().size());
		
		assertEquals(true, round.getAskerTurn().isAsker());
		assertEquals(Turn.Reason.ANSWER_CORRECT, round.getAnswerTurns().get("Amy").getReason());
		assertEquals(Turn.Reason.ANSWER_CORRECT_QUICKEST, round.getAnswerTurns().get("Bob").getReason());

		// 
		//  R O U N D 2
		//
		makeQuestion(idp.get("Bob"), 2);

		round = gameService.getRound(gameId, 2);
		assertEquals(true, round.getAskerTurn().isAsker());

		// get/answer question 
		answerQuestion(idp.get("Ken"), 2, 1, 20);
		PlayerData player = playerService.findPlayerById(idp.get("Ken"));
		assertEquals(Status.ANSWERED, player.getStatus());		

		// get/answer question 
		answerQuestion(idp.get("Amy"), 2, 1, 10);

		// TODO!!! -- make playerService.getPlayersByGame(gameid)  returns map name->playerData
		// get last turn information!
		assertEquals(playerService.findPlayerById(idp.get("Ken")).getStatus(), Player.Status.WAITING_FOR_QUESTION);
		assertEquals(playerService.findPlayerById(idp.get("Bob")).getStatus(), Player.Status.WAITING_FOR_QUESTION);
		assertEquals(playerService.findPlayerById(idp.get("Amy")).getStatus(), Player.Status.NEEDS_TO_MAKE_QUESTION);

		
		round = gameService.getRound(gameId, 2);
		assertEquals(2, round.getRoundNumber().intValue());
		assertEquals("Bob", round.getAskerPlayerName());
		assertEquals(2, round.getAnswerTurns().size());
		
		assertEquals(true, round.getAskerTurn().isAsker());
		assertEquals(Turn.Reason.ANSWER_CORRECT, round.getAnswerTurns().get("Ken").getReason());
		assertEquals(Turn.Reason.ANSWER_CORRECT_QUICKEST, round.getAnswerTurns().get("Amy").getReason());

		// 
		//  R O U N D  3
		//
	
		makeQuestion(idp.get("Amy"), 3);

		round = gameService.getRound(gameId, 3);
		assertEquals(true, round.getAskerTurn().isAsker());

		// get/answer question 
		answerQuestion(idp.get("Bob"), 3, 1, 20);
		player = playerService.findPlayerById(idp.get("Bob"));
		assertEquals(Status.ANSWERED, player.getStatus());		
		playerService.submitComment(idp.get("Bob"), "Bob comment");
		
		// get/answer question 
		answerQuestion(idp.get("Ken"), 3, 2, 10);

		// get last turn information!
		assertEquals(playerService.findPlayerById(idp.get("Ken")).getStatus(), Player.Status.FINISHED);
		assertEquals(playerService.findPlayerById(idp.get("Bob")).getStatus(), Player.Status.FINISHED);
		assertEquals(playerService.findPlayerById(idp.get("Amy")).getStatus(), Player.Status.FINISHED);

		
		round = gameService.getRound(gameId, 3);
		assertEquals(3, round.getRoundNumber().intValue());
		assertEquals("Amy", round.getAskerPlayerName());
		assertEquals(2, round.getAnswerTurns().size());
		
		assertEquals(true, round.getAskerTurn().isAsker());
		assertEquals(Turn.Reason.ANSWER_WRONG_LOST, round.getAnswerTurns().get("Ken").getReason());
		assertEquals(Turn.Reason.ANSWER_CORRECT, round.getAnswerTurns().get("Bob").getReason());
		assertEquals("Bob comment", round.getComments().get(0).getText());
		assertEquals(true, round.getComments().get(0).isAfterQuestion());

		GameDescriptionData g = gameService.getGameDescription(gameId);
		assertEquals(false, g.isActive());
		Assert.assertTrue(g.getEndDate() != null);
		Assert.assertTrue(g.getStatus() == STATUS.COMPLETE);
		
		// test find past players
		// create game and look for past players
		gameId = createGame();
		playerKen = playerService.findPlayer(gameId, memberIdKen);
		List<MemberData> pastms = playerService.findPastPlayers(playerKen.getId());
		assertEquals(2, pastms.size());	
		Assert.assertTrue((pastms.get(0).getEmail().equals("amy@tester.com") && pastms.get(1).getEmail().equals("bob@tester.com")) ||	
				(pastms.get(0).getEmail().equals("bob@tester.com") && pastms.get(1).getEmail().equals("amy@tester.com")));	

		// TODO -- delete games.
	}

	// OTHER TESTS :
	// TODO -- add reorder players?  wait on this.  it might be better just to send all the players when the registering the game.
	// EDIT PROFILE
	// EDIT GAME INFO
	// ODD STARTUP PROCEDURES -- 

	@Test
	public void testQueuedQuestions() {
		long memberIdKen = addMember("Ken");
		long gameId = createGame();
		
		// make first question
		PlayerData playerKen = playerService.findPlayer(gameId, memberIdKen);
		Long pid = playerKen.getId();
		makeQuestion(pid, 1);
		makeQuestion(pid, 2);
		makeQuestion(pid, 3);
		
		List<QuestionData> qd = playerService.getQueuedQuestions(pid);
		assertEquals(3, qd.size());
		assertEquals("Q1", qd.get(0).getText());		
		assertEquals("Q2", qd.get(1).getText());		
		assertEquals("Q3", qd.get(2).getText());	
		
		// shift list
		ArrayList<Integer> newOrder = new ArrayList<Integer>();
		newOrder.add(qd.get(1).getId());
		newOrder.add(qd.get(2).getId());
		newOrder.add(qd.get(0).getId());
		playerService.reorderQueuedQuestions(pid, newOrder);

		qd = playerService.getQueuedQuestions(pid);
		assertEquals(3, qd.size());
		assertEquals("Q2", qd.get(0).getText());		
		assertEquals("Q3", qd.get(1).getText());		
		assertEquals("Q1", qd.get(2).getText());	

		// delete first, last
		newOrder = new ArrayList<Integer>();
		newOrder.add(qd.get(1).getId());
		playerService.reorderQueuedQuestions(pid, newOrder);

		qd = playerService.getQueuedQuestions(pid);
		assertEquals(1, qd.size());
		assertEquals("Q3", qd.get(0).getText());		
		
	}
	
	@Test
	public void test_forceStart() {
		long memberIdKen = addMember("Ken");
//		long gameId = createGame();
		MemberData master = memberSerivce.findMemberByEmail("ken@tester.com");
		GameDescriptionData game = new GameDescriptionData();
		game.setDescription("Test Game Description");
		game.setName("Test Game");
		game.setPrivacy(Game.PRIVACY.PUBLIC);
		game.setTotalAuthorCycles(1);
		game.setAllowOpenRegistration(true);
		game.setMaxPlayers(3);
		long gameId = gameService.createNewGame(game, master.getId());		
		
		// make first question
		PlayerData playerKen = playerService.findPlayer(gameId, memberIdKen);
		makeQuestion(playerKen.getId(), 1);
		gameService.beginGameInvitation(gameId);
		
		// invite players
		playerService.invitePlayer(gameId, "amy@tester.com");
		playerService.invitePlayer(gameId, "bob@tester.com");
		//playerService.invitePlayer(gameId, "carl@tester.com");
		
		gameService.beginGameRegistration(gameId);		
		
		long memberIdAmy = register("Amy");
		assertNotNull(findInviteByEmail("amy@tester.com", gameId));
		playerService.registrationResponse(memberIdAmy, gameId, true);
		assertNull(findInviteByEmail("amy@tester.com", gameId));		
		assertNull(findInviteByMember(memberIdAmy, gameId));
		PlayerData playerAmy = playerService.findPlayer(gameId, memberIdAmy);
		
		assertEquals(Status.NEEDS_TO_ANSWER, playerAmy.getStatus());
		// get/answer question - Amy
		answerQuestion(playerAmy.getId(), 1, 1, 20);
		playerAmy = playerService.findPlayerById(playerAmy.getId());
		assertEquals(Status.ANSWERED, playerAmy.getStatus());		

		long memberId = register("Bob");
		assertNotNull(findInviteByEmail("bob@tester.com", gameId));
		playerService.registrationResponse(memberId, gameId, true);		
		assertNull(findInviteByMember(memberId, gameId));
		PlayerData p = playerService.findPlayer(gameId, memberId);
		assertEquals(Status.NEEDS_TO_ANSWER, p.getStatus());
		// get/answer question - Bob
		answerQuestion(p.getId(), 1, 1, 20);
		p = playerService.findPlayerById(p.getId());
		assertEquals(Status.ANSWERED, p.getStatus());		

		// start game before last player joins.
		gameService.startGame(gameId);
		
	}	
	
	@Test
	public void test_comments() {
		long memberIdKen = addMember("Ken");
//		long gameId = createGame();
		MemberData master = memberSerivce.findMemberByEmail("ken@tester.com");
		GameDescriptionData game = new GameDescriptionData();
		game.setDescription("Test Game Description");
		game.setName("Test Game");
		game.setPrivacy(Game.PRIVACY.PUBLIC);
		game.setTotalAuthorCycles(1);
		game.setAllowOpenRegistration(true);
		game.setMaxPlayers(3);
		long gameId = gameService.createNewGame(game, master.getId());		
		
		// make first question
		PlayerData playerKen = playerService.findPlayer(gameId, memberIdKen);
		makeQuestion(playerKen.getId(), 1);
		gameService.beginGameInvitation(gameId);
		
		// invite players
		playerService.invitePlayer(gameId, "amy@tester.com");
		playerService.invitePlayer(gameId, "bob@tester.com");
		//playerService.invitePlayer(gameId, "carl@tester.com");
		
		gameService.beginGameRegistration(gameId);		
		
		//
		// send questions with no answers
		//
		
		MySenderTest.notices.clear();
		playerService.submitComment(playerKen.getId(), "Comment from Ken before others answer");
		// should not be sent to anyone, since other players didnt answer yet.
		assertEquals(MySenderTest.notices.size(), 0);  
		//assertEquals(MySenderTest.notices.get(0).toList.size(),0);  

		// TODO -- test what happens when other make comments when they should be able too, like here.
		
		long memberIdAmy = register("Amy");
		playerService.registrationResponse(memberIdAmy, gameId, true);
		PlayerData playerAmy = playerService.findPlayer(gameId, memberIdAmy);
		playerAmy = playerService.findPlayerById(playerAmy.getId());
		assertEquals(Status.NEEDS_TO_ANSWER, playerAmy.getStatus());
		// get/answer question - Amy
		answerQuestion(playerAmy.getId(), 1, 1, 20);
		playerAmy = playerService.findPlayerById(playerAmy.getId());
		assertEquals(Status.ANSWERED, playerAmy.getStatus());		

		
		//
		// send questions with one answer
		//
		MySenderTest.notices.clear();
		playerService.submitComment(playerKen.getId(), "Comment from Ken after Amy answered");
		assertEquals(MySenderTest.notices.size(), 1);  
		assertEquals(MySenderTest.notices.get(0).toList.size(),1);  
		assertEquals(MySenderTest.notices.get(0).toList.get(0).getId(),
				playerAmy.getMemberId().longValue());  
		
		
		
		
		long memberId = register("Bob");
		playerService.registrationResponse(memberId, gameId, true);
		PlayerData playerBob = playerService.findPlayer(gameId, memberId);
		playerBob = playerService.findPlayerById(playerBob.getId());
		assertEquals(Status.NEEDS_TO_ANSWER, playerBob.getStatus());
		// get/answer question - Bob
		answerQuestion(playerBob.getId(), 1, 1, 20);
		playerBob = playerService.findPlayerById(playerBob.getId());
		assertEquals(Status.ANSWERED, playerBob.getStatus());		

		// start game before last player joins.
		gameService.startGame(gameId);
		

		//
		// comment after question all answered -- all should get.
		//
		MySenderTest.notices.clear();
		playerService.submitComment(playerKen.getId(), "Comment from Ken after question answered");
		assertEquals(1, MySenderTest.notices.size());  
		assertEquals(2, MySenderTest.notices.get(0).toList.size());  

		
		makeQuestion(playerAmy.getId(), 2);
		//
		// comment after making question, none answered
		//
		MySenderTest.notices.clear();
		playerService.submitComment(playerAmy.getId(), "Comment from Amy after authoring new question");
		assertEquals(0, MySenderTest.notices.size());  

		//
		// comment on previous question, before answering latest, should go to all
		//
		MySenderTest.notices.clear();
		playerService.submitComment(playerKen.getId(), "Comment from Ken after on old question, before answering new");
		assertEquals(1, MySenderTest.notices.size());  
		assertEquals(2, MySenderTest.notices.get(0).toList.size());  

		//
		// comment on previous question, before answering latest, should go to all
		//
		MySenderTest.notices.clear();
		playerService.submitComment(playerBob.getId(), "Comment from Ken after on old question, before answering new");
		assertEquals(1, MySenderTest.notices.size());  
		assertEquals(2, MySenderTest.notices.get(0).toList.size());  
		
		
		
		gameService.endGame(gameId);
		
		//
		// comment on game, should go to all, but not include author.
		//
		MySenderTest.notices.clear();
		playerService.submitComment(playerBob.getId(), "Comment from Bob after game over");
		assertEquals(1, MySenderTest.notices.size());  
		assertEquals(2, MySenderTest.notices.get(0).toList.size());  
	}	

	// TODO -- test invite by email but register as member -- should detect this.  Same goes with facebook.

	@Test
	public void test_invites() {
		long memberIdKen = addMember("Ken");
		long gameId = createGame();
		// make first question
		PlayerData playerKen = playerService.findPlayer(gameId, memberIdKen);
		makeQuestion(playerKen.getId(), 1);
		gameService.beginGameInvitation(gameId);
		int i = 0;
		assertEquals(i, gameService.getInvites(gameId).size());
		// invite new player by email -- may need to clear datatbase first.
		InviteStatus status = playerService.invitePlayer(gameId, "invite1@tester.com");
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(InviteStatus.SUCCESS, status);
		assertEquals("invite1@tester.com", gameService.getInvites(gameId).get(i).getEmail());
		// invite new player by facebook -- may need to clear datatbase first.
		playerService.inviteFbPlayer(gameId, "fbid001", "One", "Invite");
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals("fbid001", gameService.getInvites(gameId).get(i).getFacebookId());
		assertEquals("One Invite", gameService.getInvites(gameId).get(i).getName());
		// invite existing player 
		long memberIdAmy = register("Dylan");
		playerService.inviteMember(gameId, memberIdAmy);
		++i;
		MemberData dylanMember = memberSerivce.findMemberById(memberIdAmy);
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(dylanMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(dylanMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(dylanMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());

		// invite existing player by email -- should find member		
		long memberIdErnie = register("Ernie");
		MemberData ernieMember = memberSerivce.findMemberById(memberIdErnie);
		playerService.invitePlayer(gameId, ernieMember.getEmail());
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(ernieMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(ernieMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(ernieMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());
		
		// invite existing player by facebook -- should find member
		long memberIdFrank = register("Frank");
		MemberData frankMember = memberSerivce.findMemberById(memberIdFrank);
		playerService.inviteFbPlayer(gameId, frankMember.getFacebookId(), "Frank", "Invite"); // i think name will get replaced with member name?
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(frankMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(frankMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(frankMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());
		
		// try invite same player by facebook and then email, should silently reject.
		status = playerService.invitePlayer(gameId, frankMember.getEmail());
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(InviteStatus.ALREADY_MEMBER, status);
		status = playerService.inviteMember(gameId, frankMember.getId());
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(InviteStatus.ALREADY_MEMBER, status);
		// test inviting self!
		status = playerService.inviteMember(gameId, playerKen.getMemberId());
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(InviteStatus.ALREADY_MEMBER, status);
		
		//
		// End pre-invites
		//
		
		// verify notices send to invites
		MySenderTest.notices.clear();
		List<MySenderTest.Notice> notices = MySenderTest.notices;
		gameService.beginGameRegistration(gameId);		
		assertEquals(3, MySenderTest.notices.size());  
		assertEquals(3, MySenderTest.notices.get(0).toList.size());  
		// note - probably should not assume order here, but being lazy...
		assertEquals(dylanMember.getEmail(), notices.get(0).toList.get(0).getEmail());  
		assertEquals(ernieMember.getEmail(), notices.get(0).toList.get(1).getEmail());  
		assertEquals(frankMember.getEmail(), notices.get(0).toList.get(2).getEmail());
		assertEquals("invite1@tester.com", notices.get(1).toList.get(0).getEmail());  
		assertEquals("fbid001", notices.get(2).toList.get(0).getFacebookId());
		
		//
		// Start open-register
		//

		// test invites after Registration because that is how open registration works.
		long memberIdGary = register("Gary");
		MemberData garyMember = memberSerivce.findMemberById(memberIdGary);
		playerService.inviteFbPlayer(gameId, garyMember.getFacebookId(), "Frank", "Invite"); // i think name will get replaced with member name?
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(garyMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(garyMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(garyMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());
		

		// returns all past games. last one should be ours... BUT really should check entire list, or delete games after test is over.
		List<GameDescriptionData>  gddl = gameService.getGameList(memberIdErnie).getInvitedGames();
		assertEquals(gameId, gddl.get(gddl.size()-1).getId());
		gddl = gameService.getGameList(memberIdFrank).getInvitedGames();
		assertEquals(gameId, gddl.get(gddl.size()-1).getId());
		gddl = gameService.getGameList(memberIdGary).getInvitedGames();
		assertEquals(gameId, gddl.get(gddl.size()-1).getId());
		
		
		
	}		
	@Test
	public void test_invites_forceStart() {
		long memberIdKen = addMember("Ken");
		long gameId = createGame();
		// make first question
		PlayerData playerKen = playerService.findPlayer(gameId, memberIdKen);
		makeQuestion(playerKen.getId(), 1);
		gameService.beginGameInvitation(gameId);
		int i = 0;
		assertEquals(i, gameService.getInvites(gameId).size());
		// invite new player by email -- may need to clear datatbase first.
		InviteStatus status = playerService.invitePlayer(gameId, "invite1@tester.com");
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(InviteStatus.SUCCESS, status);
		assertEquals("invite1@tester.com", gameService.getInvites(gameId).get(i).getEmail());
		// invite new player by facebook -- may need to clear datatbase first.
		playerService.inviteFbPlayer(gameId, "fbid001", "One", "Invite");
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals("fbid001", gameService.getInvites(gameId).get(i).getFacebookId());
		assertEquals("One Invite", gameService.getInvites(gameId).get(i).getName());
		// invite existing player 
		long memberIdAmy = register("Dylan");
		playerService.inviteMember(gameId, memberIdAmy);
		++i;
		MemberData dylanMember = memberSerivce.findMemberById(memberIdAmy);
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(dylanMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(dylanMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(dylanMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());

		// invite existing player by email -- should find member		
		long memberIdErnie = register("Ernie");
		MemberData ernieMember = memberSerivce.findMemberById(memberIdErnie);
		playerService.invitePlayer(gameId, ernieMember.getEmail());
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(ernieMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(ernieMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(ernieMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());
		
		// invite existing player by facebook -- should find member
		long memberIdFrank = register("Frank");
		MemberData frankMember = memberSerivce.findMemberById(memberIdFrank);
		playerService.inviteFbPlayer(gameId, frankMember.getFacebookId(), "Frank", "Invite"); // i think name will get replaced with member name?
		++i;
		assertEquals(i+1, gameService.getInvites(gameId).size());
		assertEquals(frankMember.getEmail(), gameService.getInvites(gameId).get(i).getEmail());
		assertEquals(frankMember.getId(), gameService.getInvites(gameId).get(i).getMemberId().longValue());
		assertEquals(frankMember.getFacebookId(), gameService.getInvites(gameId).get(i).getFacebookId());
		
		
		//
		// End pre-invites
		//
		
		gameService.beginGameRegistration(gameId);		

		//
		// Start open-register
		// Register all but one.

		playerService.registrationResponse(memberIdFrank, gameId, true);
		PlayerData playerFrank = playerService.findPlayer(gameId, memberIdFrank);
		answerQuestion(playerFrank.getId(), 1, 1, 20);

		playerService.registrationResponse(memberIdErnie, gameId, true);
		PlayerData playerErnie = playerService.findPlayer(gameId, memberIdErnie);
		answerQuestion(playerErnie.getId(), 1, 1, 20);
		
		GameDescriptionData gdd = gameService.getGameDescription(gameId);
		assertEquals(Game.STATUS.REGISTERING , gdd.getStatus());
		ActiveGameData agd = gameService.getActiveGameData(gameId);
		assertEquals(new Integer(1) , agd.getRoundNumber());
		gameService.startGame(gameId);
		
		// Turn should have completed here!!! Test for that
		agd = gameService.getActiveGameData(gameId);
		assertEquals(Game.STATUS.ACTIVE , agd.getStatus());
		assertEquals(new Integer(2) , agd.getRoundNumber());
	}		
	// TODO -- invite tests:
		// invite new player by email -- may need to clear datatbase first.
		// invite new player by facebook -- may need to clear datatbase first.
		// invite existing player by email -- should find member
		// invite existing player by facebook -- should find member
		// try to invite same player 2x, should reject.  
		// try invite by email and facebook should reject 2nd.
		// verify game shows up in invited games for each invited player -- thru fb and email and as member

	
	// TODO -- invite tests:
		// invite new player by email -- may need to clear datatbase first.
		// invite new player by facebook -- may need to clear datatbase first.
		// invite existing player by email -- should find member
		// invite existing player by facebook -- should find member
		// try to invite same player 2x, should reject.  
		// try invite by email and facebook should reject 2nd.
		// verify game shows up in invited games for each invited player -- thru fb and email and as member
}
		
	