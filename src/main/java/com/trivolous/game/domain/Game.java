package com.trivolous.game.domain;

import java.util.ArrayList;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity
@Table
public class Game implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long id;
	
	@Column
	private String name;
	
	@OneToMany(mappedBy="game", cascade=CascadeType.ALL, orphanRemoval=true)
	@OrderBy("playOrder")
	private List<Player> players  = new ArrayList<Player>();
	
	@OneToMany(mappedBy="game", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<Invite> invites  = new ArrayList<Invite>();
		
	@OneToMany(mappedBy="game", cascade=CascadeType.ALL)
	@OrderBy("id")
	private List<Round> rounds = new ArrayList<Round>();
	
	@Column
	private Date created = new Date();
	
	@Column
	private Date endDate = null;
	
	// TODO -- added logging, but maybe the logic where this is used should be moved to service.
	@Transient
	protected final Log logger = LogFactory.getLog(getClass());
	
	public enum STATUS
	{
		CREATING,   // description and first question being made
		INACTIVE,
		ACTIVE,		// in play
		COMPLETE,	
		REGISTERING, // sending notices and timing down...
		INVITING;	// players can join via links, but registration count down has not started.
		
		public String getString() {
			   //only capitalize the first letter
			   String s = super.toString();
			   s = s.replace('_',' ');
			   return s.substring(0, 1) + s.substring(1).toLowerCase();
			 }		
	}

	public enum PRIVACY
	{
		PLAYERS_ONLY,
		FREINDS_ONLY,
		PUBLIC;

		public String getString() {
			   //only capitalize the first letter
			   String s = super.toString();
			   s = s.replace('_',' ');
			   return s.substring(0, 1) + s.substring(1).toLowerCase();
			   
			 }		
		
	}

	@Column
	private STATUS status = STATUS.CREATING;

	@Column
	private Boolean allowOpenRegistration; // true  just for debug --  false;

	@Column
	private Integer maxPlayers; // no default to allow retrofitting this.  otherwise hibernate has issues.  default using getter.

	public Integer getCountDown() {
		return countDown;
	}

	public void setCountDown(Integer countDown) {
		this.countDown = countDown;
	}

	@Column
	private PRIVACY privacy = PRIVACY.PLAYERS_ONLY;

	@Column(length=1000)
	private String description = new String();

	@ManyToOne
	@JoinColumn(name="master_fk")
	private Member master = null;

	@Column
	private Integer totalAuthorCycles = 2;
	
	// Right now this is the count down in days to autostart.  Made the name generic in case it can be reused in active state too.
	@Column
	private Integer countDown = 2;
	
	// remove these.  have service layer work with 
	// round object instead of putting this logic here.  Same goes to setter.
	//@Deprecated
	public Question getQuestion() {
		Round r = getCurrentRound();
		return (r == null ? null : r.getQuestion());
	}

	public void setQuestion(Question question) {
		getCurrentRound().setQuestion(question);
	}
	
	// includes players in the process of answering (very important if checking to end round)
	public int getPlayersLeftToAnswer() {
		// TODO (low) -- this should probably be moved to logic/service layer.
		int count = 0;
		for (Player p : players)
		{
			Player.Status status = p.getStatus();
			logger.debug("player id="+p.getId()+" status="+ status);
			if (p.getIsActive() && 
				((Player.Status.NEEDS_TO_ANSWER == status) || 
				(Player.Status.ANSWERING == status) ))
			{
				++count;
			}
		}
		return count;
	}

		public Player getAsker()
		{
			Round round = getCurrentRound();
			
			return (round == null ? null : round.getAsker());
		}
		
		public void setPlayers(List<Player> players) {
			this.players = players;
		}

 		public Date getCreated() {
			return created;
		}

		public void setCreated(Date created) {
			this.created = created;
		}

		public Game() {
			this.id = -1;
		
		}

		public Game(long id) {
			this.id = id;
		}

		public List<Player> getPlayers()
		{
			return players;
		}
		
		public List<Player> getActivePlayers()
		{
			ArrayList<Player> pl = new ArrayList<Player>();
			for (Player p : getPlayers()) // NOTE: Must call function so hibernate proxy works!
			{
				if (p.getIsActive() == true) pl.add(p);
			}
			return pl;
		}
		
		
		
		public long getId() {
			return this.id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getRoundNumber() {
			Round currentRound = getCurrentRound(); 
			return currentRound == null ? 0 : currentRound.getRoundNumber();
		}

		public void setRounds(List<Round> rounds) {
			this.rounds = rounds;
		}

		public List<Round> getRounds() {
			return rounds;
		}
		
		public List<Round> getCompletedRounds() {
			List<Round> completedRounds = new ArrayList<Round>();
			for (Round r : rounds) {
				if (r.isComplete()) completedRounds.add(r);
			}
			return completedRounds;
		}
		

////////// moved from logic... These are calculated, not persistant objects. //////////////////////
		
		public long getSecondsSinceTurn()
		{
			Round currentRound = getCurrentRound();
			if (currentRound == null) return 0;
			return new Date().getTime() - currentRound.getStartDate().getTime();
		}
		
		public long getSecondsSinceQuestion()
		{
			Round currentRound = getCurrentRound();
			if (currentRound == null) return 0;	
			Date qdate = getCurrentRound().getQuestionMadeDate();
			if (qdate == null) return 0;
			return new Date().getTime() - qdate.getTime();
		}
		
		public long getSecondsWaiting()
		{
			if (getQuestion() == null) return getSecondsSinceTurn();
			return getSecondsSinceQuestion();
		}

		public Date getWaitDate()
		{
			switch (status)
			{
				case CREATING: 
					return created; 
				default:
					Round currentRound = getCurrentRound();
					if (currentRound == null) return null;			
					if (currentRound.getQuestion() == null)
					{
						return currentRound.getStartDate();
					}
					else
					{
						return currentRound.getQuestionMadeDate();
					}
			}
		}
		
	
		public String getStatusString()
		{
			switch(status) {
				case REGISTERING:
					return "Players joining";
				case ACTIVE:
					if (getQuestion() == null) 	{
						return "Creating question";
					}
					else {
						return "Players answering";
					}
				case COMPLETE:
					return "Finished";
				case CREATING:
					return "Creating";
				case INACTIVE:
					return "Paused";
				case INVITING:
					return "Inviting";
				default:
					return "?????";
			}
		}

		public String getStatus2()
		{
			// what is difference here?
			
			return getStatusString();
		}

		// returns last completed round or null, if none.  Note: this could
		// be the current round or not.
		public Round getLastCompletedRound()
		{
			for (int i=rounds.size()-1; i >= 0; --i)
			{
				Round r = rounds.get(i);
				if (r.isComplete())	return r;
			}
			return null;
		}
		
		public Round getCurrentRound()
		{
			return rounds.size() == 0 ? null : rounds.get(rounds.size()-1);
		}

		public void setTotalAuthorCycles(int authorCycles) {
			this.totalAuthorCycles = authorCycles;
		}

		public int getTotalAuthorCycles() {
			return totalAuthorCycles; 
		}

		public Integer getAskerCycle() {
			Round currentRound = getCurrentRound(); 
			return currentRound == null ? 0 : currentRound.getAskerCycle();
		}

		public boolean getIsActive() {
			return status == STATUS.ACTIVE || status == STATUS.REGISTERING || status == STATUS.INVITING;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public void setMaster(Member master) {
			this.master = master;
		}

		public Member getMaster() {
			return master;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setStatus(STATUS status) {
			this.status = status;
		}

		public STATUS getStatus() {
			return status;
		}
		
		public boolean getIsComplete()
		{
			return status == STATUS.COMPLETE;
		}

		public void setPrivacy(PRIVACY privacy) {
			this.privacy = privacy;
		}

		public PRIVACY getPrivacy() {
			return privacy;
		}

		// NOTE -- probably should just reference master as player?... but will not work when creating game.
		public Player getMasterPlayer() {
			for (Player p : players)
			{
				if (p.getMember().getId() == master.getId())
				{
					return p;
				}
			}
			return null;
		}

		public int getTotalRounds()
		{
			// rounds  = turns played + rounds left(including this one) * players - num. players that made a question this round
			int roundsLeft = getTotalAuthorCycles() - getAskerCycle() + 1;
			int turnsLeft = getRoundNumber() - 1 +  roundsLeft * getActivePlayers().size();  // -1 because rounds are 1 based
			for (Player p : getActivePlayers())
			{
				if (p.getIsAsker())
					break;
				turnsLeft -= 1;
			}
			return turnsLeft;
			
		}		
		
		public boolean getAreAllPlayersRegistered() {
			
			switch(status) {
			case CREATING:
				logger.warn("all registered? No, creating.  Shouldnt be getting called!");
				return false;
			case INVITING:
				logger.debug("all registered? No, still inviting");
				return false;
			case REGISTERING:
				for (Invite i : invites) {
					if (i.getHasJoined() == null) {
						logger.debug("all registered? invite not registered yet, id="+i.getId());
						return false;
					}
				}
				// give entire time window for players to join.
				if (getOpenRegistrationAvailable()) {
					logger.debug("all registered? No, open registration still available.");
					return false;
				}
				logger.debug("all registered? Yes!");
				return true;
			case ACTIVE:
			case COMPLETE:
			case INACTIVE:
			default:
				break;
				
			}
			logger.debug("all registered yes returned for state="+status);
			return true;
		}

		// return true if players can register themselves at this time
		public boolean getOpenRegistrationAvailable() {
			// allow players to register as long as first round not completed
			// TODO -- watch out for race conditions here?
			return (status == STATUS.REGISTERING  && 
					getAllowOpenRegistration() == true &&
					players.size() < getMaxPlayers());
		}

		public boolean getAllowOpenRegistration() {
			if (allowOpenRegistration==null) allowOpenRegistration = true;
			return allowOpenRegistration;
		}

		public void setAllowOpenRegistration(boolean allowOpenRegistration) {
			this.allowOpenRegistration = allowOpenRegistration;
		}
		
		// returns a crypto string 
		public String getCode() {
			// NOTE: I was using created time here, but it turns out that changes when game is started.
			// TODO -- use something more secure.  Could make started time so created time doesnt change?
			String code = "Trivy" + getId() + "Divy" + getMaster().getId();
			String hash = new Integer(code.hashCode()).toString();
			return hash;
		}

		public Integer getMaxPlayers() {
			if (maxPlayers == null) maxPlayers = 10;
			return maxPlayers;
		}

		public void setMaxPlayers(Integer maxPlayers) {
			this.maxPlayers = maxPlayers;
		}

		public List<Invite> getInvites() {
			return invites;
		}


}
