package com.trivolous.game.notify;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.domain.logic.NewGameService;

public class RemindAll {
	protected final Log logger = LogFactory.getLog(getClass());
	private long maxDaysToJoin = 3;
	private NewGameService gameService;
	
	public long getMaxDaysToJoin() {
		return maxDaysToJoin;
	}

	public void setMaxDaysToJoin(long maxDaysToJoin) {
		this.maxDaysToJoin = maxDaysToJoin;
	}


	public void setGameService(NewGameService gameService) {
		this.gameService = gameService;
	}

	@Transactional
	public void run()
	{
		logger.debug("Remind All");
		
		gameService.dailyTick(new Date());
	}	
}
