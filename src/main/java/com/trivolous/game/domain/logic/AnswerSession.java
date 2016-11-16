package com.trivolous.game.domain.logic;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Turn;


// TODO (med) -- maybe expand this to handle other session concepts like timeleft.
// TODO (med) -- remove circular reference to Session.  Could do this with callback,
// but that still forces the service to publicly expose the callback function
// for this object, that should not be called by the user.  I dont like doing this.
// Also note that the callback must be transactional.  (I dont think the transactional
// annotation works unless it is on a reference provided by the spring framework).
// Another option is to place the session on top of the service.  This means the
// user needs to call directly to the session.
@Service
public class AnswerSession {
	protected final Log logger = LogFactory.getLog(getClass());
	
	@Autowired
	private PlayerService service;

	private class Task extends TimerTask
	{
		private long playerId;
		
		public Task(long playerId)
		{
			this.playerId = playerId;
		}
		
		@Transactional
		@Override
		public void run() {
			end(playerId, 0, Turn.TIMEOUT);
		}
		
	}
	private Map<Long, Task> tasks = new ConcurrentHashMap<Long,Task>();
	private Timer timer = new Timer();
	
	public void start(Player player, long timeout)
	{
		logger.debug("Start " + player.getName() + " to=" + timeout);
		Task task = new Task(player.getId());
		tasks.put(player.getId(), task);
		// add a little padding to the timeout to lessen chance of concurrency issues.
		timer.schedule(task, (timeout + 5)*1000);
	}
	
	public Turn end(long playerId, long secondsLeft, int answer)
	{
		Turn turn = null;
		logger.info("Answer session end pid=" + playerId + " secsLeft=" + secondsLeft + " ans=" + answer);
		// !!! This function runs concurrently.  The remove is atomic.  This ensures
		// submit is only called once.
		Task task = tasks.remove(playerId);
		if (task == null)
		{
			logger.warn("No session for answer?");
			// just in case submit timedout answer???
			// or let this be solved somewhere else.  Get status maybe?
		}
		else
		{
			logger.debug("Autoanswer pid=" + playerId);
			task.cancel();
			tasks.remove(playerId);   // TODO -- ?? is this needed a second time?
			turn = service.submitAnswerInternal(playerId, secondsLeft, answer);
		}
		return turn;
	}
	
	
}
