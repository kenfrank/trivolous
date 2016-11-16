package com.trivolous.game.data;

import java.util.List;

public class GameList {

	public class ActiveGame {

	
		private GameDescriptionData desc;
		private ActiveGameData activeGame;
		private PlayerData player;

		public GameDescriptionData getDesc() {
			return desc;
		}
		public void setDesc(GameDescriptionData desc) {
			this.desc = desc;
		}
		public ActiveGameData getStatus() {
			return activeGame;
		}
		public void setStatus(ActiveGameData status) {
			this.activeGame = status;
		}
		public PlayerData getPlayer() {
			return player;
		}
		public void setPlayer(PlayerData player) {
			this.player = player;
		}	
	
	}
	
	public class CompletedGame {
		private GameDescriptionData desc;
		private String winnerName;

		public GameDescriptionData getDesc() {
			return desc;
		}
		public void setDesc(GameDescriptionData desc) {
			this.desc = desc;
		}
		public String getWinnerName() {
			return winnerName;
		}
		public void setWinnerName(String name) {
			this.winnerName = name;
		}
	}
	
	private List<ActiveGame> activeGames;
	private List<CompletedGame> completedGames;
	private List<GameDescriptionData> publicGames;
	private List<GameDescriptionData> invitedGames;

	public List<ActiveGame> getActiveGames() {
		return activeGames;
	}
	public void setActiveGames(List<ActiveGame> activeGames) {
		this.activeGames = activeGames;
	}
	public List<CompletedGame> getCompletedGames() {
		return completedGames;
	}
	public void setCompletedGames(List<CompletedGame> completedGame) {
		this.completedGames = completedGame;
	}
	public List<GameDescriptionData> getPublicGames() {
		return publicGames;
	}
	public void setPublicGames(List<GameDescriptionData> publicGames) {
		this.publicGames = publicGames;
	}
	public List<GameDescriptionData> getInvitedGames() {
		return invitedGames;
	}
	public void setInvitedGames(List<GameDescriptionData> invitedGames) {
		this.invitedGames = invitedGames;
	}	
}
