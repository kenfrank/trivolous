package com.trivolous.game.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.trivolous.game.domain.Member;
import com.trivolous.game.domain.Question;

public class QuestionData {

		public static final long NO_IMAGE_ID = -1;
		public static final long TEMPORARY_USER_IMAGE_ID = 0;
		
		private int id;
		private String text = "";
		private String choice1 = "";
		private String choice2 = "";
		private String choice3 = "";
		private String choice4 = "";
		private String choice5 = "";
		private int numChoices = 2;
		private String hint = "";
		private Integer answer = -1;
		private Integer timeout = 60;
		private String explanation = "";
		private Date created = new Date();
		private Long imageId = NO_IMAGE_ID;
		private Long authorMemberId = (long)-1;
		// this is only valid in answer session
		private Integer secondsLeft = 60;
		
		public QuestionData() {}

		public QuestionData(Question q) {
			this.id = q.getId();
			this.text = q.getText();
			this.choice1 = q.getChoice1();
			this.choice2 = q.getChoice2();
			this.choice3 = q.getChoice3();
			this.choice4 = q.getChoice4();
			this.choice5 = q.getChoice5();
			this.numChoices = q.getNumChoices();
			this.hint = q.getHint();
			this.answer = q.getAnswer();
			this.timeout = q.getTimeout();
			this.secondsLeft = q.getTimeout();
			this.explanation = q.getExplanation();
			this.created = q.getCreated();
			this.imageId = q.getImageId();
			this.authorMemberId = q.getAuthor().getId();
		}

		public void fillQuestion(Question q, Member author) {
			q.setId(this.id);
			q.setText(this.text);
			q.setChoice1(this.choice1);
			q.setChoice2(this.choice2);
			q.setChoice3(this.choice3);
			q.setChoice4(this.choice4);
			q.setChoice5(this.choice5);
			q.setNumChoices(this.numChoices);
			q.setHint(this.hint);
			q.setAnswer(this.answer);
			q.setTimeout(this.timeout);
			q.setExplanation(this.explanation);
			q.setCreated(this.created);
			q.setImageId(this.imageId);
			q.setAuthor(author);
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getChoice1() {
			return choice1;
		}
		public void setChoice1(String choice1) {
			this.choice1 = choice1;
		}
		public String getChoice2() {
			return choice2;
		}
		public void setChoice2(String choice2) {
			this.choice2 = choice2;
		}
		public String getChoice3() {
			return choice3;
		}
		public void setChoice3(String choice3) {
			this.choice3 = choice3;
		}
		public String getChoice4() {
			return choice4;
		}
		public void setChoice4(String choice4) {
			this.choice4 = choice4;
		}
		public String getChoice5() {
			return choice5;
		}
		public void setChoice5(String choice5) {
			this.choice5 = choice5;
		}
		public int getNumChoices() {
			return numChoices;
		}
		public void setNumChoices(int numChoices) {
			this.numChoices = numChoices;
		}
		public String getHint() {
			return hint;
		}
		public void setHint(String hint) {
			this.hint = hint;
		}
		public Integer getAnswer() {
			return answer;
		}
		public void setAnswer(Integer answer) {
			this.answer = answer;
		}
		public Integer getTimeout() {
			return timeout;
		}
		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
			this.secondsLeft = timeout;
			
		}
		public String getExplanation() {
			return explanation;
		}
		public void setExplanation(String explanation) {
			this.explanation = explanation;
		}
		public Date getCreated() {
			return created;
		}
		public void setCreated(Date created) {
			this.created = created;
		}
		public Long getImageId() {
			return imageId;
		}
		public void setImageId(Long imageId) {
			this.imageId = imageId;
		}
		public Long getAuthorMemberId() {
			return authorMemberId;
		}
		public void setAuthorMemberId(Long authorMemberId) {
			this.authorMemberId = authorMemberId;
		}

		public Integer getSecondsLeft() {
			return secondsLeft;
		}

		public void setSecondsLeft(Integer secondsLeft) {
			this.secondsLeft = secondsLeft;
		}
		
		public List<String> getChoiceList() {
			ArrayList<String> cl = new ArrayList<String>();
			cl.add(choice1);
			cl.add(choice2);
			if (numChoices >= 3) cl.add(choice3);
			if (numChoices >= 4) cl.add(choice4);
			if (numChoices >= 5) cl.add(choice5);
			return cl;
		}
			
		public void setChoiceList(List<String> l) {
			numChoices = l.size();
			choice1 = l.get(0);
			choice2 = l.get(1);
			if (numChoices >= 3) choice3 = l.get(2);
			if (numChoices >= 4) choice4 = l.get(3);
			if (numChoices >= 5) choice5 = l.get(4);
		}
			
}
