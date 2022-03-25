package com.Filmkritik.authservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MST_Secques")
public class SecurityQuestionsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="question")
	private String Question;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the questions
	 */
	public String getQuestion() {
		return Question;
	}

	/**
	 * @param questions the questions to set
	 */
	public void setQuestion(String question) {
		Question = question;
	}

	
}
