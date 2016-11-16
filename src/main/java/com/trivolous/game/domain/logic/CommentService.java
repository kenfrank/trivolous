package com.trivolous.game.domain.logic;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.dao.GameCommentDao;
import com.trivolous.game.dao.PlayerDao;
import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;
import com.trivolous.game.notify.NotificationSenderInterface;

@Service
@Transactional
public class CommentService  {

	@Autowired
	private GameCommentDao gameCommentDao; 
	@Autowired
	private NotificationSenderInterface notificationSender;
	@Autowired
	PlayerDao playerDao;

	protected final Log logger = LogFactory.getLog(getClass());
	final int OVER_ROUND_NUM = -1;
		
	public void setGameCommentDao(GameCommentDao gameCommentDao) {
		this.gameCommentDao = gameCommentDao;
	}

	public void setNotificationSender(NotificationSenderInterface notificationSender) {
		this.notificationSender = notificationSender;
	}


	private long submitRoundComment(Integer roundNumber, Player p, String text, boolean afterQuestion)
	{
		if (text.isEmpty()) return -1;
		// TODO -- could check for duplicate comments here?
		logger.debug("Submit Question Comment from " + p.getName() + " for round " + roundNumber);
		GameComment gameComment = new GameComment();
		gameComment.setPlayer(p);
		gameComment.setRoundNumber(roundNumber);
		gameComment.setText(text);
		gameComment.setAfterQuestion(afterQuestion);
		gameCommentDao.create(gameComment);
		if (afterQuestion) {
			Round round = getRound(p.getGame(),roundNumber);
			notificationSender.sendQuestionComment(round, gameComment);
		} else {
			notificationSender.sendGameComment(gameComment);
		}
		return gameComment.getId();
	}

	// copied from game service because did not want to add dependency to comment service.
	private Round getRound(Game game, int roundNum) 
	{
		if (roundNum >= game.getRoundNumber()) {
			return game.getCurrentRound();
		}
		
		if (roundNum < 1) roundNum = 1;
		
		return game.getCompletedRounds().get(roundNum - 1);
	}	
	
	
	public long submitGameOverComment(Player p, String text)
	{
		if (text.isEmpty()) return -1;
		GameComment gameComment = new GameComment();
		gameComment.setPlayer(p);
		gameComment.setRoundNumber(OVER_ROUND_NUM);
		gameComment.setText(text);
		gameComment.setAfterQuestion(false);
		gameCommentDao.create(gameComment);
		notificationSender.sendGameComment(gameComment);
		return gameComment.getId();
	}


	
	public List<GameComment> findAllGameComments(long gameId)
	{
		return gameCommentDao.findByGame(gameId);		
	}
	
	public List<GameComment> findAllRoundComments(Long gameId, Integer roundNumber)
	{
		return gameCommentDao.findAllByRound(
				gameId, 
				roundNumber);		
	}
	
	public List<GameComment> findGameOverComments(Long gameId)
	{
		return gameCommentDao.findAllByRound(
				gameId, 
				OVER_ROUND_NUM);		
	}
	
	@SuppressWarnings("unused")
	private List<GameComment> findBeforeQuestionRoundComments(Long gameId, Integer roundNumber)
	{
		return gameCommentDao.findBeforeQuestionByRound(
				gameId, 
				roundNumber);		
	}

	public void submitCommentToMaster(long playerId, String text) {
		Player player = playerDao.findById(playerId);
		if (text.isEmpty()) return;
		notificationSender.sendMasterMessage(player,text);
	}
	
	public void submitCommentToAll(long playerId, String text) {
		// TODO -- need to handle registration comments?
		Player player = playerDao.findById(playerId);
		Integer roundNum = player.getGame().getRoundNumber();
		if (!player.hasAnswered()) {
			roundNum -= 1;
		}
		submitRoundComment(roundNum, player, text, false);
	}
	
	public long submitCommentToAnswered(long playerId, String text) {
		Player player = playerDao.findById(playerId);
		Turn lastTurn = player.getLastTurn();
		if (lastTurn == null) {
			logger.error("Sent comment to answered, but hasnt answered yet.");
			return -1;
		}
		Integer roundNum = lastTurn.getRound().getRoundNumber();
		return submitRoundComment(roundNum, player, text, true);
	}
	
	
}