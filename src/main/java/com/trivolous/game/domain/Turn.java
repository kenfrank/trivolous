package com.trivolous.game.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class Turn {
	public static final int GAVEUP = 99;
	public static final int TIMEOUT = 100;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;
	
	@Column
	private Integer choice = -1;

	@ManyToOne
	@JoinColumn(name="player_fk")
	private Player player;

	// TODO (low) -- make different turn for asker (rename author?) that points to question. Does not need to include things like time to answer etc...
	@Column
	private Integer timeToAnswer;
	
	@Column
	private Integer score;
	
	@Column
	private Date created;

	@ManyToOne
	@JoinColumn(name="round_fk")
	private Round round;

	@Column
	private Reason reason;

	@Column
	private int points;
	
	@Column
	boolean isAsker;

	@Column
	private Integer bet = 1;
	

	public Integer getChoice() {
		return choice;
	}
	public void setChoice(Integer choice) {
		this.choice = choice;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
		player.getTurns().add(this);
	}
	public Integer getTimeToAnswer() {
		return timeToAnswer;
	}
	public void setTimeToAnswer(Integer timeToAnswer) {
		this.timeToAnswer = timeToAnswer;
	}

	public enum ReasonOld {
		CORRECT_ANSWER_QUICKEST,
		CORRECT_ANSWER,
		GAVE_UP,
		TIMED_OUT,
		WRONG_BUT_NONE_RIGHT,
		WRONG_AND_OTHERS_RIGHT, 
		NOBODY_GOT_IT, 
		CORRECT_ANSWERS, 
		CORRECT_AND_INCORRECT_ANSWERS,
		NOBODY_ANSWERED
	}	

	public enum Reason {
		ANSWER_CORRECT_QUICKEST,  
		ANSWER_CORRECT,
		ANSWER_WRONG_LOST,  // Answer wrong, and other were right, lost bet
		ANSWER_WRONG_WON,   // Answer wrong, but no others right, won bet
		ANSWER_NONE,
		ANSWER_NONE_WON,	// No one else answered either, won bet.
		QUESTION_RIGHT, 	// Some right answers to question
		QUESTION_WRONG,     // No right answers to question (but some wrong)
		QUESTION_NONE		// No answers to question (all gave up/timed out).
	}	

	
	public Reason getReason() {
		return reason;
	}
	// TODO (med) -- see other enums that are made into a class with a toString method.
//	public String getReasonString() {
//		switch(reason)
//		{
//			case CORRECT_ANSWER_QUICKEST: return "Correct answer in the least time";
//			case CORRECT_ANSWER: return "Correct answer";
//			case GAVE_UP: return "Gave up";
//			case TIMED_OUT: return "Timed out";
//			case WRONG_BUT_NONE_RIGHT: return "Wrong answer, but no one got it right.";
//			case WRONG_AND_OTHERS_RIGHT: return "Wrong answer, but others got it right"; 
//			case NOBODY_GOT_IT: return "No one got it correct";
//			case CORRECT_ANSWERS: return "Some got it right"; 
//			case CORRECT_AND_INCORRECT_ANSWERS: return "Some got it right and some got it wrong";
//			case NOBODY_ANSWERED: return "No one tried to answer";
//		}
//		return "*** ERROR: Unknown reason ***";
//	}
	public String getReasonString() {
		switch(reason)
		{
		case ANSWER_CORRECT_QUICKEST: return "Right answer in least time.  Won 2 times bet"; 
		case ANSWER_CORRECT: return "Right answer. Won bet";
		case ANSWER_WRONG_LOST: return "Wrong answer, but others were right. Lost bet.";  // Answer wrong, and other were right, lost bet
		case ANSWER_WRONG_WON: return "Wrong answer, but no others right. Won bet.";   // Answer wrong, but no others right, won bet
		case ANSWER_NONE: return "No answer. Kept bet.";
		case ANSWER_NONE_WON: return "No one answered.  Won bet.";	// No one else answered either, won bet.
		case QUESTION_RIGHT: return "Some players got right answer to question. Match bets of right answers.  If more than one, quickest to answer gets double their bet.  Asker takes bets of wrong answers."; 	// Some right answers to question
		case QUESTION_WRONG: return "Players tried, but none got right answer.  Asker matches bets of those who answered.";     // No right answers to question (but some wrong)
		case QUESTION_NONE: return "All players gave up on question.  Asker matches all bets.";		// No answers to question (all gave up/timed out).
		}
		return "*** ERROR: Unknown reason ***";
	}
//	public String getReasonCode() {
//		
//		if (reason == null)
//		{
//			if (getIsAsker()) return "Q?";
//			else return "A?";
//		}
//		
//		switch(reason)
//		{
//			case CORRECT_ANSWER_QUICKEST: return "A+";
//			case CORRECT_ANSWER: return "A";
//			case GAVE_UP: return "NGU";			
//
//			case TIMED_OUT: return "NTO";
//			case WRONG_BUT_NONE_RIGHT: return "F0";
//			case WRONG_AND_OTHERS_RIGHT: return "F-"; 
//			case NOBODY_GOT_IT: return "Q0";
//			case CORRECT_ANSWERS: return "Q+"; 
//			case CORRECT_AND_INCORRECT_ANSWERS: return "Q++";
//			case NOBODY_ANSWERED: return "Q-";
//		}
//		return "*** ERROR: Unknown reason ***";
//	}
	public void setReason(Reason reason) {
		this.reason = reason;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}

	public boolean getIsAsker() {
		return isAsker;
	}
	public void setIsAsker(boolean isAsker) {
		this.isAsker = isAsker;
	}

	public Long getTimeToPlayMs() {
		Long time;
		
		if (isAsker)
		{
			time = created.getTime() - round.getStartDate().getTime();
		}
		else
		{
			time = created.getTime() - round.getQuestionMadeDate().getTime();
		}
		
		return time;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	public Integer getScore() {
		return score;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getCreated() {
		return created;
	}
	public void setRound(Round round) {
		this.round = round;
		round.getTurns().add(this);
	}
	public Round getRound() {
		return round;
	}
	public int getBet() {
		return bet;
	}
	public void setBet(Integer bet) {
		this.bet = bet;
	}
	public long getId() {
		return id;
	}

}
