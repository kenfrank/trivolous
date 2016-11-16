package com.trivolous.game.notify;

import java.util.ArrayList;
import java.util.List;

import com.trivolous.game.domain.Member;

public class MySenderTest implements MySenderInterface {
	
	public class Notice {
		public Notice(List<Member> toList, String subject, String textbody, String htmlbody) {
			this.toList.addAll(toList);
			this.subject = subject;
			this.textbody = textbody;
			this.htmlbody = htmlbody;
		}
		public List<Member> toList = new ArrayList<Member>(); 
		public String subject = "";
		public String textbody = ""; 
		public String htmlbody = "";
	}

	static public ArrayList<Notice> notices = new ArrayList<Notice>();

	@Override
	public void send(List<Member> toList, String subject, String textbody, String htmlbody) {
		
		System.out.print("To: ");
		for (Member to : toList)
		{
			System.out.print(to.getEmail() + ", ");
		}
		
		System.out.print("\nSubject: " + subject);
		
		System.out.print("\nBody:\n" + textbody +"\n<<end of email>>\n");
		
		notices.add(new Notice(toList,  subject,  textbody,  htmlbody));
		
	}

}
