package com.trivolous.game.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;

public class RoundData {
	
	private long id;
	private Integer askerCycle;  // question round #
	private Integer roundNumber;  // question #
	private Date startDate;
	private Date questionMadeDate;
	private Date endDate;
	private QuestionData question;
	private String askerPlayerName;
	private TurnData askerTurn;
	private HashMap<String, TurnData> answerTurns;   // PlayerName->Turn
	private List<CommentData> comments;
	private HashMap< Long, ArrayList<String> > choiceToPlayer; // Choice Num -> Players -- WARNING! Must be Long to work with JSTL/EL. 
	
	// comments can be null
	public RoundData(Round r, List<GameComment> comments ) {
		this.id = r.getId();
		this.askerCycle = r.getAskerCycle();
		this.roundNumber = r.getRoundNumber();
		this.startDate = r.getStartDate();
		this.questionMadeDate = r.getQuestionMadeDate();
		this.endDate = r.getEndDate();
		// Todo include question here?
		this.question = new QuestionData(r.getQuestion());
		this.askerPlayerName = r.getAsker().getName();
		answerTurns = new HashMap<String, TurnData>();
		for (Turn t : r.getTurnsInPlayerOrder()) {
			if (t.getIsAsker()) {
				askerTurn = new TurnData(t);
			}
			else {
				answerTurns.put(t.getPlayer().getName(), new TurnData(t));
			}
		}
		this.comments = new ArrayList<CommentData>();
		for (GameComment gameComment : comments) {
			this.comments.add(new CommentData(gameComment));
		}
		// Choice Num -> Players -- WARNING! Must be Long to work with JSTL/EL. 
		choiceToPlayer = new HashMap< Long, ArrayList<String> >();
		for (Turn turn : r.getTurns())
		{
			// Choice Num -> Players -- WARNING! Must be Long to work with JSTL/EL. 
			Long choice = new Long(turn.getChoice());
			ArrayList<String> players = choiceToPlayer.get(choice);
			if (players == null) {
				players = new ArrayList<String>();
				choiceToPlayer.put(choice, players);
			}
			players.add(turn.getPlayer().getName());
		}
		
	}

	public long getId() {
		return id;
	}

	public Integer getAskerCycle() {
		return askerCycle;
	}

	public Integer getRoundNumber() {
		return roundNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getQuestionMadeDate() {
		return questionMadeDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public QuestionData getQuestion() {
		return question;
	}

	public String getAskerPlayerName() {
		return askerPlayerName;
	}

	public HashMap<String, TurnData> getAnswerTurns() {
		return answerTurns;
	}

	public List<CommentData> getComments() {
		return comments;
	}

	public HashMap<Long, ArrayList<String>> getChoiceToPlayer() {
		return choiceToPlayer;
	}

	public TurnData getAskerTurn() {
		return askerTurn;
	}

}
