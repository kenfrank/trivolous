package com.trivolous.game.notify;

import java.util.List;

import com.trivolous.game.domain.Member;


public interface MySenderInterface {

	/**
	 * "send" method to send the message.
	 */
	public abstract void send(final List<Member> toList, final String subject,
			final String textbody, final String htmlbody);

}