package com.trivolous.game.notify;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.domain.Member;


public class MySenderList implements MySenderInterface {

	ArrayList<MySenderInterface> senders;
	
	public ArrayList<MySenderInterface> getSenders() {
		return senders;
	}

	public void setSenders(ArrayList<MySenderInterface> senders) {
		this.senders = senders;
	}

	@Override
	public void send(List<Member> toList, String subject, String textbody, String htmlbody) {
		for (MySenderInterface sender : senders) {
			sender.send(toList, subject, textbody, htmlbody);
		}
	}

}
