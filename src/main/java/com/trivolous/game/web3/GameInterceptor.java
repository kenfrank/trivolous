package com.trivolous.game.web3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import com.trivolous.game.data.GameDescriptionData;
import com.trivolous.game.data.MemberData;
import com.trivolous.game.data.PlayerData;
import com.trivolous.game.domain.Game.STATUS;
import com.trivolous.game.domain.logic.MemberService;
import com.trivolous.game.domain.logic.NewGameService;
import com.trivolous.game.domain.logic.PlayerService;


/**
 */
public class GameInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private NewGameService gameService;	
	@Autowired
	private MemberService memberService;
	@Autowired
	private PlayerService playerService;

	protected final Log logger = LogFactory.getLog(getClass());

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		UserSession userSession = (UserSession) WebUtils.getRequiredSessionAttribute(request, "userSession");
		
		// if gameid is set, then set session gameid to that
		// if gameid is not valid for this player, then redirect.
		
		int id = ServletRequestUtils.getIntParameter(request, "gameid", -1);
		long gameId;
		long memberId = userSession.getMemberId();
		// if id given as parameter use that (if valid), otherwise, use session id, if none, pick one.
		if (id != -1)
		{
			gameId = id;
			String code = ServletRequestUtils.getStringParameter(request, "gamecode", "");		

			boolean allowed = gameService.authorizeGameAccess(code, memberId, gameId);
			if (allowed) 
			{
				logger.debug("Set game id from param = " + gameId);
				userSession.setGameId(gameId);
			} 
			else
			{
				logger.warn("Invalid game id for member. id= " + gameId);
				gameId = 0;
			}
		}
		else
		{
			gameId = userSession.getGameId();
			logger.debug("Session game id = " + gameId);
			if (gameId <= 0) {
					MemberData member = memberService.findMemberById(memberId);
					gameId = member.getDefaultGameId();
					userSession.setGameId(gameId);
			}
							 
		}

		logger.info("Game interceptor, gameid="+gameId+" memberid="+userSession.getMemberId()+
				" url="+request.getServletPath()+
				"?"+request.getQueryString() +
				" h=" + handler.getClass().getName()
				);

		
		if (gameId > 0)
		{
			// should this be in the session scope or just 'view' scope (I am not sure the proper term for this.)
			// TODO (high) -- I think this can be optimized so that game is used instead of gameId
			// and then all the game based controllers can just use these, instead of looking up their own each time.
			// also consider putting this in userSession?
			// Or just set this in usersession object and then get rid of all the code used in each controller
			// to look these up. !
			// UPDATE -- Tomcat seems to want to save session objects which is a problem if gameService is in it!
			// So remove gameservice from sesssion object to get rid of warnings, and then use these guys instead:
			//userSession.reset(gameService);
			// populate data for template header -- TODO - is it possible to add game object here?
			GameDescriptionData gameDesc = gameService.getGameDescription(userSession.getGameId());
			PlayerData playerData = playerService.findPlayer(gameId, memberId);
			userSession.setGameDesc(gameDesc);
			userSession.setPlayerData(playerData);

			// TODO -- can use from userSession above instead.
			// TODO -- if state=CREATING only allow step 1, step 2, INVITING only allow step 3.  May want to make another interceptor for this?
			// TODO -- was going to do some checks per path in here, but I think the only check that needs
			// to be done is for the master, and that can be another handler for /game/master/**
			// TODO -- also need to check if game creation is not done.  1) 1st question needs to be made, 2) inviting players.
			// TODO -- another approach to some is this is just to check on the service side (which should be done anyway) and
			// throw errors.  going to these pages out of state should never happen (except maybe bad bookmarks or something).
			Pattern p = Pattern.compile("/game/(.*)");
			Matcher m = p.matcher(request.getServletPath());
			if (m.find()) {
				String gamePath = m.group(1); 
				logger.info("-----Game path= '"+gamePath+"' playerid="+playerData.getId() + "------");
				// step 1 : CREATING
				// step 2 : CREATING
				// step 3 : INVITING
				// other  : REGISTERING, ACTIVE, INACTIVE, ...
				// if bad, redirect :
				// CREATING -> step 1
				// INVITING -> step 3
				// others -> game/game
				if (gamePath.startsWith("create")) {
					if (gamePath.contains("invite")) {
						if (gameDesc.getStatus() != STATUS.INVITING) {
							logger.warn("Invalid state for invite.  redirect");
							response.sendRedirect("/game/game");
							return false;
						}
					} else {
						if (gameDesc.getStatus() != STATUS.CREATING) {
							logger.warn("Invalid state for invite.  redirect");
							response.sendRedirect("/game/game");
							return false;
						}
					}
				}
				if (!gamePath.contains("over")) {
					if (gameDesc.getStatus() == STATUS.COMPLETE) {
						logger.warn("Invalid path for completed game.  redirect");
						response.sendRedirect("/game/over");
						return false;
					}
				}
			}
			request.setAttribute("gameName", gameDesc.getName());
			return true;
		}
		else
		{
			logger.warn("Invalid gameid=" + gameId + " memid="+userSession.getMemberId()+" url="+request.getServletPath()+"?"+request.getQueryString());
			// invalid or missing gamid.  Redirect to choose game.
			response.sendRedirect("/member/gameList");
			return false;
		}
	}
}
