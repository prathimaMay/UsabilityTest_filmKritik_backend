package com.Filmkritik.authservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UserSecquesMap")
public class UserSecQuestMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column
	private long uid;
	
	@Column
	private long sid;
	
	@Column
	private String Answer;

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
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(long uid) {
		this.uid = uid;
	}

	/**
	 * @return the sid
	 */
	public long getSid() {
		return sid;
	}

	/**
	 * @param sid the sid to set
	 */
	public void setSid(long sid) {
		this.sid = sid;
	}

	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return Answer;
	}

	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		Answer = answer;
	}

	
	
}
