package com.trivolous.game.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table
public class Round {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;
	
	@ManyToOne
	@JoinColumn(name="game_fk")
	private Game game;
	
	@Column
	private Integer askerCycle;  // question round #

	@Column
	private Integer roundNumber;  // question #
	

	@Column
	private Date startDate;
	
	// questionDate.  Remember questions can be played in multiple games
	// so any game specific info cannot be kept in them.  This became an issue with 
	// queued questions.  when is the create date?  For now, it will be when queued to 
	// keep the duration calculations correct.  
	@Column
	private Date questionMadeDate;
	
	@Column
	private Date endDate;

	@ManyToOne
	@JoinColumn(name="question_fk")
	private Question question;
	
	@ManyToOne
	@JoinColumn(name="player_fk")
	private Player asker;
	

	@OneToMany(mappedBy="round", cascade=CascadeType.ALL)
	private List<Turn> turns = new ArrayList<Turn>();
	
	public void setRoundNumber(Integer number) {
		this.roundNumber = number;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	public List<Turn> getTurns() {
		return turns;
	}
	public List<Turn> getTurnsInPlayerOrder() {
		List<Turn> orderedTurns = new ArrayList<Turn>(turns);
		Collections.sort(orderedTurns, new Comparator<Turn>() {
		    public int compare(Turn o1, Turn o2) {
		        return (int) (o1.getPlayer().getOrder() - o2.getPlayer().getOrder());
		    }});
		return orderedTurns;
	}
	public List<Turn> getTurnsInDateOrder() {
		List<Turn> orderedTurns = new ArrayList<Turn>(turns);
		Collections.sort(orderedTurns, new Comparator<Turn>() {
		    public int compare(Turn o1, Turn o2) {
		        return (int) (o1.getCreated().getTime() - o2.getCreated().getTime());
		    }});
		return orderedTurns;
	}
	public List<Turn> getAnswerTurns() {
		List<Turn> answerTurns = new ArrayList<Turn>();
		for (Turn t : turns) {
			if (!t.getIsAsker()) answerTurns.add(t);
		}
		return answerTurns;
	}
	public int getNumAnswersWrong() {
		int c = 0;
		for (Turn t : turns) {
			switch (t.getReason()) {
			case ANSWER_WRONG_LOST:
			case ANSWER_WRONG_WON:
				c += 1;
				break;
			default:
				break;
			}
		}
		return c;
	}

	public Turn getAskerTurn() {
		for (Turn t : turns) {
			if (t.getIsAsker()) return t;
		}
		// this should never happen
		return null;
	}
	public void setTurns(List<Turn> turns) {
		this.turns = turns;
	}
	
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
		game.getRounds().add(this);
	}
	public void setAskerCycle(Integer roundNumber) {
		this.askerCycle = roundNumber;
	}
	public Integer getAskerCycle() {
		return askerCycle;
	}
	// roundNumber is one based.  So first one is 1.
	public Integer getRoundNumber() {
		return roundNumber;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	
	public Player getAsker()
	{
		return asker;
	}
	
	public boolean getAnyAnswersYet()
	{
		// turns always includes one turn for asker.
		return turns.size() > 1;
	}
	public Date getQuestionMadeDate() {
		return questionMadeDate;
	}
	public void setQuestionMadeDate(Date questionMadeDate) {
		this.questionMadeDate = questionMadeDate;
	}
	public void setAsker(Player asker) {
		this.asker = asker;
	}
	
	public boolean isComplete() {
		return endDate != null;
	}
}
