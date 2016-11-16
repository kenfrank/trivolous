package com.trivolous.game.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.trivolous.game.dao.hibernate.HibernateGameDao;
import com.trivolous.game.dao.hibernate.HibernateMemberDao;
import com.trivolous.game.dao.hibernate.HibernatePlayerDao;
import com.trivolous.game.dao.hibernate.HibernateQuestionDao;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Question;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;

@SuppressWarnings("deprecation")
public class TestDao {
	private HibernateMemberDao memberDao;
	private HibernatePlayerDao playerDao;
	private HibernateGameDao gameDao;
	private HibernateQuestionDao questionDao;
//	private MemberDao memberDao;
//	private PlayerDao playerDao;
//	private GameDao gameDao;
//	private QuestionDao questionDao;
	private static SessionFactory sessionFactory;
	

	@BeforeClass
	public static void runBeforeClass() throws Exception {
//		ApplicationContext context = 
//			new ClassPathXmlApplicationContext("beans-hibernate.xml");
//		dao = (MemberDao) context.getBean("memberDao");
		Configuration configuration = new AnnotationConfiguration().configure();
		sessionFactory = configuration.buildSessionFactory();
	}
	
	@Before  
	public void runBeforeEveryTest() {  
		sessionFactory.getCurrentSession().beginTransaction();
		memberDao = new HibernateMemberDao();
		memberDao.setSessionFactory(sessionFactory);
		
		playerDao = new HibernatePlayerDao();
		playerDao.setSessionFactory(sessionFactory);

		gameDao = new HibernateGameDao();
		gameDao.setSessionFactory(sessionFactory);
				
		questionDao = new HibernateQuestionDao();
		questionDao.setSessionFactory(sessionFactory);

//		memberDao = new MemoryMemberDao();
//		playerDao = new MemoryPlayerDao();
//		gameDao = new MemoryGameDao();
//		questionDao = new MemoryQuestionDao();
	}  
	  
	@After  
	public void runAfterEveryTest() {  
		sessionFactory.getCurrentSession().getTransaction().commit();
		memberDao = null;
		playerDao = null;
		gameDao = null;
		questionDao = null;
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
	public void testFinal() {
//		Game game = gameDao.findById(1);
//		Question question = game.getCurrentRound().getQuestion();
//		QuestionComment comment = question.getComments().get(0);
//		assertEquals(1, question.getComments().size());
//		assertEquals("Sidney", comment.getPlayer().getName());
//		assertEquals("Test comment", comment.getText());
	}
	
	
	
	

}
