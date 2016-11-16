package com.trivolous.game.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;

@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={"beans-hibernate.xml"})
@TransactionConfiguration(defaultRollback=false)
@Transactional
public class TestDao2  {
	private MemberDao memberDao;
	private PlayerDao playerDao;
	private GameDao gameDao;
	private QuestionDao questionDao;
	private GameCommentDao gameCommentDao;
	
	@Autowired
	public void setGameCommentDao(GameCommentDao gameCommentDao) {
		this.gameCommentDao = gameCommentDao;
	}

	@Autowired
	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}
		
	@Autowired
	public void setPlayerDao(PlayerDao playerDao) {
		this.playerDao = playerDao;
	}
	@Autowired
	public void setGameDao(GameDao gameDao) {
		this.gameDao = gameDao;
	}
	@Autowired
	public void setQuestionDao(QuestionDao questionDao) {
		this.questionDao = questionDao;
	}

	@Test
	public void testCreate() {
		
		Member member = new Member();
		member.setFirstName("Rodney");
		member.setEmail("Rodney@email.com");
		memberDao.create(member);
		
		member = new Member();
		member.setFirstName("Sidney");
		member.setEmail("Sidney@email.com");
		memberDao.create(member);
		
	}
	
	@Test
	public void testGame() {

		List<Member> all = memberDao.findAll();		
		assertEquals(2 , all.size());
		assertEquals( 1, all.get(0).getId());
		assertEquals("Rodney", all.get(0).getName());
		assertEquals("Rodney@email.com", all.get(0).getEmail() );
		assertEquals( 2, all.get(1).getId());
		assertEquals("Sidney", all.get(1).getName());
		assertEquals("Sidney@email.com", all.get(1).getEmail() );

		Game game = new Game();
		game.setName("Unit Test Game");
		gameDao.create(game);

		Game game2 = new Game();
		game2.setName("Dummy Game");
		gameDao.create(game2);
	
	}

	@Test
	public void testAddPlayer() {

		Member member = memberDao.findById(1);
		Game game = gameDao.findById(1);
		
		Player player = new Player();
		player.setMember(member);
		player.setGame(game);
		player.setOrder(0);
		playerDao.create(player);

		
		Member member2 = memberDao.findById(2);
		
		Player player2 = new Player();
		player2.setMember(member2);
		player2.setGame(game);
		player2.setOrder(1);
		playerDao.create(player2);
		
		//game.setIsActive(true);
		game.setStatus(STATUS.ACTIVE);  // or should this be registering?
	}
	
	
	@Test
	public void testActiveGames() {
		List<Game> games = gameDao.findAllActive();
		assertEquals(1, games.size());
		assertEquals(1, games.get(0).getId());
	}
	
	@Test
	public void testPlayers() {

		Member member = memberDao.findById(1);
		
		assertTrue(member != null);
		assertTrue(member.getPlayers() != null);
		assertEquals(1, member.getPlayers().size());
		Player player = member.getPlayers().get(0);
		
		assertEquals("Rodney", player.getName());
		assertEquals("Unit Test Game", player.getGame().getName());
		
		Game game = gameDao.findById(1);
		assertEquals(2, game.getPlayers().size());
		assertEquals(game.getPlayers().get(1).getName(), "Sidney");
		
		long id = memberDao.find("Sidney", "");
		
		assertTrue(id != -1);
		assertEquals(game.getPlayers().get(1).getId(), id);
		
		id = memberDao.find("Mindy", "");
		assertEquals(-1, id);
	}
	
	@Test
	public void testRound() {
		Game game = gameDao.findById(1);	

		Round round = new Round();
		round.setRoundNumber(28);
		// just reference from game.  round will be cascaded in.
		round.setGame(game);
		game.getRounds().add(round);

		assertTrue(game.getRounds().get(0).getRoundNumber() == 28);
	}
	
	@Test
	public void testQuestion() {
		Game game = gameDao.findById(1);
		Round round = game.getRounds().get(0);

		assertTrue(round.getRoundNumber() == 28);
		
		Question question = new Question();
		Player author = game.getPlayers().get(0);
		question.setAuthor(author.getMember());
		List<String> choices = new ArrayList<String>();
		choices.add("choice one");
		choices.add("choice two");
		question.setChoiceList(choices);
		question.setText("question one");
		round.setQuestion(question);
		questionDao.create(question);
	}
	
	@Test
	public void testAnswer() {
		Game game = gameDao.findById(1);
		Round round = game.getRounds().get(0);
		Question question = round.getQuestion();

		assertTrue(round.getRoundNumber() == 28);
		assertEquals(question.getText(), "question one");
		assertEquals(question.getChoiceList().get(0), "choice one");
		assertEquals(question.getChoiceList().get(1), "choice two");
		assertEquals(question.getAuthor().getName(), "Rodney");

		Turn turn = new Turn();
		turn.setPlayer(game.getPlayers().get(1));
		turn.setChoice(28);
		turn.setRound(round);
		round.getTurns().add(turn);
	}

	@Test
	public void testComment() {
//		Game game = gameDao.findById(1);
//		Round round = game.getRounds().get(0);
//		assertEquals(1, round.getTurns().size());
//		assertTrue(28 == round.getTurns().get(0).getChoice());
//		Player player = round.getTurns().get(0).getPlayer();
//		assertEquals("Sidney", player.getName());
//		
//		Question question = game.getCurrentRound().getQuestion();
//		
//		QuestionComment comment = new QuestionComment();
//		comment.setPlayer(player);
//		comment.setQuestion(question);
//		comment.setText("Test comment");
//		
//		question.getComments().add(comment);
	}
	
		
	@Test
	public void testCommentFinal() {
//		Game game = gameDao.findById(1);
//		Question question = game.getCurrentRound().getQuestion();
//		QuestionComment comment = question.getComments().get(0);
//		assertEquals(1, question.getComments().size());
//		assertEquals("Sidney", comment.getPlayer().getName());
//		assertEquals("Test comment", comment.getText());
	}
	
	@Test
	public void testGameComments() {
		Game game = gameDao.findById(1);
		Player p = game.getPlayers().get(0);
		
//		for (int i=0; i<1; ++i)
//		{
			GameComment gameComment = new GameComment();
			gameComment.setPlayer(p);
			gameComment.setText("comment 1");
			//p.getComments().add(gameComment);
			gameCommentDao.create(gameComment);
//		}
			GameComment gameComment2 = new GameComment();
			gameComment2.setPlayer(p);
			gameComment2.setText("comment 2");
			//p.getComments().add(gameComment);
			gameCommentDao.create(gameComment2);

	
	}
	
	@Test
	public void testCheckGameComments() {
		List<GameComment> comments = gameCommentDao.findAll();
		assertEquals(2, comments.size());
	}

}
