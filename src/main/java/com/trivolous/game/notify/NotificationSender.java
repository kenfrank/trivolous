//TODO! --
// _x_ changed sender interface to players.. but that wont work... so...
// _x_ change sender interface to take Members instead of a string of email addresses.
// ___ modify this class so that mutliple senders can be added.  
//    make one function to do sending.  loop over list and call senders.
// ___ make facebook sender.
// ___ check if player has facebook enabled.  if so, send to facebook.
// ___ change formatter to not include trivolous - in subject.  add it to email sender as prefix.

package com.trivolous.game.notify;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.domain.Game;
import com.trivolous.game.domain.GameComment;
import com.trivolous.game.domain.Invite;
import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Player;
import com.trivolous.game.domain.Round;
import com.trivolous.game.domain.Turn;

/**
 * 
 */
public class NotificationSender implements NotificationSenderInterface {
	private MySenderInterface mySender;
	private NotificationFormatter notificationFormatter;

    public void setNotificationFormatter(NotificationFormatter notificationSender) {
		this.notificationFormatter = notificationSender;
	}

	@Override
	public void sendReminderForQuestion(Game game)
	{
		// remind player to make question
		List<Member> toList = makeMemberList(filterPlayersQuestionRequired(game));
		if (!toList.isEmpty()) {
			NotificationFormatter.EmailContent content = notificationFormatter.formatQuestionReminderStatus(game);
			mySender.send(toList,content.subject,content.plainText,content.htmlText);
		}
	}
	
	@Override
	public void sendReminderToAnswer(Game game)
	{
		// remind players that need to answer
		List<Member> toList = makeMemberList(filterPlayersAnswerRequired(game));
		if (!toList.isEmpty()) {
			NotificationFormatter.EmailContent content = notificationFormatter.formatAnswerReminderStatus(game);
			mySender.send(toList,content.subject,content.plainText,content.htmlText);
		}
	}

	@Override
	public void sendReminderToMaster(Game game)
	{
			List<Member> toList = new ArrayList<Member>();
			toList.add(game.getMasterPlayer().getMember());
			NotificationFormatter.EmailContent content = notificationFormatter.formatMasterReminderStatus(game);
			mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}

	@Override
	public void sendGameOver(Game game)
	{
		List<Member> toList = makeMemberList(game.getActivePlayers());
		NotificationFormatter.EmailContent content = notificationFormatter.formatGameOver(game);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}
		
	@Override
	public void sendQuestionIsReadyNotice(Game game)
	{
		List<Member> toList = makeMemberList(filterPlayersAnswerers(game));
		NotificationFormatter.EmailContent content = notificationFormatter.formatQuestionIsReady(game);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}
		
	@Override
	public void sendRoundResultsAndNextQuestionQueued(Round lastRound)
	{
		List<Member> toList = makeMemberList(filterPlayersAllButLastToAnswer(lastRound.getGame())); 
		NotificationFormatter.EmailContent content = notificationFormatter.formatRoundResultsAndNextReady(lastRound);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}
	
	@Override
	public void sendQuestionWasQueuedNotice(Game game)
	{
		List<Member> toList = new ArrayList<Member>();
		toList.add(game.getAsker().getMember());
		NotificationFormatter.EmailContent content = notificationFormatter.formatQuestionQueued(game.getAsker());
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}
		
	@Override
	public void sendRoundResults(Round round)
	{
		List<Member> toList = makeMemberList(filterPlayersAllButLastToAnswer(round.getGame())); 
		NotificationFormatter.EmailContent content = notificationFormatter.formatRoundResults(round);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}


	@Override
	public void sendInvite(Game game, boolean isReminder, boolean isLastReminder)
	{
		// TODO - FB - dont send to FB invitees. (with fb#* emails.)
		List<Member> toList = new ArrayList<Member>();
		toList = filterInvitesNotRegisteredMembers(game);
		NotificationFormatter.EmailContent content;
		if (!toList.isEmpty())
		{
			content = notificationFormatter.formatInvite(game, isReminder, isLastReminder);
			mySender.send(toList,content.subject,content.plainText,content.htmlText);
		}
		
		// if there are any new player, send more detailed invite.
		toList = filterInvitesNotRegisteredNew(game);
		if (!toList.isEmpty())
		{
			List<Member> toOneList = new ArrayList<Member>();
			for (Member m : toList) {
				content = notificationFormatter.formatNewInvite(m, game, isReminder, isLastReminder);
				toOneList.clear();
				toOneList.add(m);
				mySender.send(toOneList,content.subject,content.plainText,content.htmlText);
			}
		}
		// if last reminder, notify master too
		if (isLastReminder) {
			toList.clear();
			toList.add(game.getMaster());
			content = notificationFormatter.formatRemindAutostart(game);
			mySender.send(toList,content.subject,content.plainText,content.htmlText);
		}
		
	}



	   private List<Member> makeMemberList(List<Player> players)
	   {
			List<Member> members = new ArrayList<Member>();
			for (Player p : players)
			{
				members.add(p.getMember()); 
			}
			return members;
	   }


   private List<Player> filterPlayersNew(Game game)
   {
		List<Player> players = new ArrayList<Player>();
		for (Player player : game.getPlayers())
		{
			// if is new, but not invited via Facebook (ie. no email).
			// TODO FB -- abstract fb/email in here?  This might be a big change. especially since, fb messages may need to be send on client side. (I know this is true for the invite request.  I am not sure about this rest.)
			if (player.getMember().getIsNew()  && 
					!(player.getMember().getEmail() == null || player.getMember().getEmail().isEmpty()))
			{
				players.add(player);
			}
		}
		return players;
   }

   private List<Member> filterInvitesNotRegisteredMembers(Game game)
   {
		List<Member> members = new ArrayList<Member>();
		for (Invite invite : game.getInvites())
		{
			if ((invite.getHasJoined() == null) && (invite.getMember() != null)) 
			{
				// convert to member because, invites used to be members also, and I want to limit the changes in refactoring.
				members.add(invite.toMember());
			}
		}
		return members;
   }
   
   private List<Member> filterInvitesNotRegisteredNew(Game game)
   {
		List<Member> members = new ArrayList<Member>();
		for (Invite invite : game.getInvites())
		{
			if ((invite.getHasJoined() == null) && (invite.getMember() == null)) 
			{
				// convert to member because, invites used to be members also, and I want to limit the changes in refactoring.
				members.add(invite.toMember());
			}
		}
		return members;
   }
   
  
   // just send reminder to those that need to act and always master.
   private List<Player> filterPlayersQuestionRequired(Game game)
   {
		List<Player> players = new ArrayList<Player>();
		
		for (Player p : game.getActivePlayers())
		{				
			if (p.getStatus() == Player.Status.NEEDS_TO_MAKE_QUESTION)
			{
				players.add(p);
			}
		}
		return players;
   }

   // just send reminder to those that need to act and always master.
   private List<Player> filterPlayersAnswerRequired(Game game)
   {
		List<Player> players = new ArrayList<Player>();
		
		for (Player p : game.getActivePlayers())
		{
			if (p.getStatus() == Player.Status.NEEDS_TO_ANSWER)
			{
				players.add(p);
			}
		}
		return players;
   }

   private List<Player> filterPlayersAllButLastToAnswer(Game game)
   {
		List<Player> players = new ArrayList<Player>();
		List<Turn> turns = game.getLastCompletedRound().getTurnsInDateOrder();
		turns.remove(turns.size() -1);

		for (Turn t : turns)
		{
			players.add(t.getPlayer());
		}
		return players;
   }
   
   private List<Player> filterPlayersAnswerers(Game game)
   {
		List<Player> players = game.getActivePlayers();
		players.remove(game.getAsker());
		return players;
   }

	private List<Player> filterPlayersAnswered(Round round, Player author) {
		List<Player> players = new ArrayList<Player>();
		for (Turn turn : round.getTurns())
		{
			// send to any players that have taken their turn
			Player p = turn.getPlayer();
			if (p.getId() != author.getId())
			{
				players.add(p);
			}
		}
		return players;
	}
	
	private List<Player> filterComments(Game game, Player author) {
		List<Player> players = new ArrayList<Player>();
		for (Player p : game.getActivePlayers())
		{
			if (p.getId() != author.getId())
			{
				players.add(p);
			}
		}
		return players;
	}

	
	private  List<Player> filterPlayersInviteNotDeclined(Game game, Player author) {
		List<Player> players = new ArrayList<Player>();
		for (Player p : game.getPlayers())
		{
			if (p.getIsActive())
			{
				if (p.getId() != author.getId())
				{
					players.add(p);
				}
			}
		}
		return players;
	}

	public void setMySender(MySenderInterface mySender) {
		this.mySender = mySender;
	}




	@Override
	public void sendGameComment(GameComment gameComment) {
		List<Member> toList = makeMemberList(filterComments(gameComment.getPlayer().getGame(),gameComment.getPlayer()));
		NotificationFormatter.EmailContent content = notificationFormatter.formatComment(gameComment);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);		
		
	}

	@Override
	public void sendRegistrationComment(GameComment gameComment) {
		List<Member> toList = makeMemberList(filterPlayersInviteNotDeclined(gameComment.getPlayer().getGame(),
				gameComment.getPlayer()));
		NotificationFormatter.EmailContent content = notificationFormatter.formatComment(gameComment);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);		
	}

	@Override
	public void sendQuestionComment(Round round, GameComment gameComment) {
		List<Member> toList = makeMemberList(filterPlayersAnswered(round, gameComment.getPlayer()));
		
		if (!toList.isEmpty())
		{
			NotificationFormatter.EmailContent content = notificationFormatter.formatQuestionComment(gameComment);
			mySender.send(toList,content.subject,content.plainText,content.htmlText);
		}
		
	}

	@Override
	public void sendPasswordReminder(Member member) {
		List<Member> toList = new ArrayList<Member>();
		toList.add(member);
		
		NotificationFormatter.EmailContent content = notificationFormatter.formatForgotPassword(member);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}

	@Override
	public void sendMasterMessage(Player player, String message) {
		List<Member> toList = new ArrayList<Member>();
		toList.add(player.getGame().getMaster());
		
		NotificationFormatter.EmailContent content = notificationFormatter.formatMasterMessage(player,message);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}

	@Override
	public void sendGameCancelled(Game game) {
		List<Member> toList = makeMemberList(game.getActivePlayers());
		NotificationFormatter.EmailContent content = notificationFormatter.formatGameCancelled(game);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}

	@Override
	public void sendRegistrationAborted(Game game) {
		List<Member> toList = makeMemberList(game.getActivePlayers());
		NotificationFormatter.EmailContent content = notificationFormatter.formatRegistrationAborted(game);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
		
	}

	@Override
	public void sendRegistrationLink(String email, String link) {
		List<Member> toList = new ArrayList<Member>();
		// TODO - this is silly that I have to create a temp Member.  Make mySender interface for just email.
		Member tempMember = new Member();
		tempMember.setEmail(email);
		toList.add(tempMember);
		NotificationFormatter.EmailContent content = notificationFormatter.formatRegisterLinkMessage(link);
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}
	@Override
	public void sendWelcome(String email) {
		List<Member> toList = new ArrayList<Member>();
		// TODO - this is silly that I have to create a temp Member.  Make mySender interface for just email.
		Member tempMember = new Member();
		tempMember.setEmail(email);
		toList.add(tempMember);
		NotificationFormatter.EmailContent content = notificationFormatter.formatWelcomeMessage();
		mySender.send(toList,content.subject,content.plainText,content.htmlText);
	}

}
