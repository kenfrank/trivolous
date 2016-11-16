package com.trivolous.game.notify;

import java.util.List;

import com.trivolous.game.domain.Member;

public class MySenderPrint implements MySenderInterface {

	@Override
	public void send(List<Member> toList, String subject, String textbody, String htmlbody) {
		
		System.out.print("To: ");
		for (Member to : toList)
		{
			System.out.print(to.getEmail() + ", ");
		}
		
		System.out.print("\nSubject: " + subject);
		
		System.out.print("\nBody:\n" + textbody +"\n<<end of email>>\n");
	}

}
