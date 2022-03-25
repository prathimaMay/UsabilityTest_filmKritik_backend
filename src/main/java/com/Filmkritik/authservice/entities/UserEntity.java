package com.Filmkritik.authservice.entities;

import java.util.Set;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "user")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "email")
	private String username;
	
	@Column
	private String password;

	@Column(name="Firstname")
	private String firstname;
	
	@Column(name="Lastname")
	private String lastname;
	
	@Column
	private String phoneno;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	/**
	 * @return the phoneno
	 */
	public String getPhoneno() {
		return phoneno;
	}

	/**
	 * @param phoneno the phoneno to set
	 */
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}

}
