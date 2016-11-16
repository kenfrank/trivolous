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
import javax.persistence.Table;

// Generated Jul 16, 2010 9:07:48 AM by Hibernate Tools 3.3.0.GA

/**
 * Question generated by hbm2java
 */
@Entity
@Table
public class Question implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public static final long NO_IMAGE_ID = -1;
	public static final long TEMPORARY_USER_IMAGE_ID = 0;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private int id;
	@Column(length=1000)
	private String text = "";
	
	@Column(length=400)
	private String choice1 = "";
	@Column(length=400)
	private String choice2 = "";
	@Column(length=400)
	private String choice3 = "";
	@Column(length=400)
	private String choice4 = "";
	@Column(length=400)
	private String choice5 = "";
	@Column
	private int numChoices = 2;
	
	// TODO -- make hint 'longtext'  right now it is varchar(255)
	@Column
	public String hint = "";
	@Column
	private Integer answer = -1;
	@Column
	private Integer timeout = 60;
	@Column(length=1000)
	private String explanation = "";
	@Column
	private Date created = new Date();
	
	@Column
	private Long imageId = (long)-1;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="member_fk")
	private Member author;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Member getAuthor() {
		return author;
	}

	public void setAuthor(Member author) {
		this.author = author;
	}

	public Question() {
	}

	public Question(int questionId) {
		this.setId(questionId);
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

	public void setChoiceList(List<String> choices) {
		numChoices = choices.size();
		choice1=choices.get(0);
		choice2=choices.get(1);
		if (choices.size() == 2) return;
		choice3=choices.get(2);
		if (choices.size() == 3) return;
		choice4=choices.get(3);
		if (choices.size() == 4) return;
		choice5=choices.get(4);
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getAnswer() {
		return this.answer;
	}

	public void setAnswer(Integer answer) {
		this.answer = answer;
	}

	public Integer getTimeout() {
		return this.timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public String getExplanation() {
		return this.explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	public Long getImageId() {
		return imageId;
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

}
