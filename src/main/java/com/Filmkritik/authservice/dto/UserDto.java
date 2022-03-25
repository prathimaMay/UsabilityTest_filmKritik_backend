package com.Filmkritik.authservice.dto;

import java.util.List;
import java.util.Map;

public class UserDto {
	private String firstname;
	private String lastname;
	private String phonenumber;
	private String password;
	private String email;
	private Map<Integer,String> SQ_A;
	private List<String> genre;
	
	
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
	 * @return the phonenumber
	 */
	public String getPhonenumber() {
		return phonenumber;
	}
	/**
	 * @param phonenumber the phonenumber to set
	 */
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * @return the genre
	 */
	public List<String> getGenre() {
		return genre;
	}
	/**
	 * @param genre the genre to set
	 */
	public void setGenre(List<String> genre) {
		this.genre = genre;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the sQ_A
	 */
	public Map<Integer, String> getSQ_A() {
		return SQ_A;
	}
	/**
	 * @param sQ_A the sQ_A to set
	 */
	public void setSQ_A(Map<Integer, String> sQ_A) {
		SQ_A = sQ_A;
	}
	

}
