package com.Filmkritik.authservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_movie_map")
public class UserMovieMap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;


	@Column
	private long uid;
	
	@Column
	private long mid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public long getMid() {
		return mid;
	}

	public void setMid(long mid) {
		this.mid = mid;
	}
	
}
