package com.trivolous.game.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Player.Status;

public class PlayerData  {
	protected final Log logger = LogFactory.getLog(getClass());

	private long id;
	private Integer score = 0 ;
	private Long memberId = null;
	private Long gameId = null;
	private Integer playOrder = 0;
	private Boolean isActive = null;
	private String name = "";
	private String memberName = "";
	private String memberImage = null;
	private String email = "";
	private boolean answered;
	private Status status;
	private boolean isActionRequired;
	private boolean isAsker;
	private boolean isMaster;
	private Integer queuedQuestionCount;
	
	public PlayerData(Player p) {
		this.id = p.getId();
		this.score = p.getScore();
		this.memberId = p.getMember().getId();
		this.gameId = p.getGame().getId();
		this.playOrder = p.getOrder();
		this.isActive = p.getIsActive();
		this.name = p.getName();
		this.memberName = p.getMember().getName();
		this.email = p.getMember().getEmail();
		this.answered = p.hasAnswered();
		this.status = p.getStatus();
		this.isActionRequired = p.getIsActionRequired();
		this.isAsker = p.getIsAsker();
		this.isMaster = p.getIsMaster();
		this.setQueuedQuestionCount(p.getQueuedQuestions().size());
		this.memberImage  = p.getMember().getImageUrl();
	}
	
	
	public String getActionRequiredString()
	{
		return "Action required";
	}
	
	// TODO (med) --use enum class like done with other enums.
	// not being used in java source but didnt check jsp.
	@Deprecated
	public String getStatusString()
	{
		return status.getString();
	}


	public long getId() {
		return id;
	}


	public Integer getScore() {
		return score;
	}


	public Long getMemberId() {
		return memberId;
	}


	public Long getGameId() {
		return gameId;
	}


	public Integer getPlayOrder() {
		return playOrder;
	}


	public Boolean getIsActive() {
		return isActive;
	}


	public String getName() {
		return name;
	}


	public boolean getIsAnswered() {
		return answered;
	}


	public Status getStatus() {
		return status;
	}


	public boolean getIsActionRequired() {
		return isActionRequired;
	}


	public boolean setIsAsker() {
		return isAsker;
	}

	public boolean getIsMaster() {
		return isMaster;
	}


	public boolean getIsAsker() {
		return isAsker;
	}


	public void setAsker(boolean isAsker) {
		this.isAsker = isAsker;
	}


	public Integer getQueuedQuestionCount() {
		return queuedQuestionCount;
	}


	public void setQueuedQuestionCount(Integer queuedQuestionCount) {
		this.queuedQuestionCount = queuedQuestionCount;
	}


	public String getMemberName() {
		return memberName;
	}


	public String getEmail() {
		return email;
	}


	public String getMemberImage() {
		return memberImage;
	}

}
