package com.felisys.gotit.entity;

import java.util.List;

public class User {

	String userId;
	String userName;
	String emailId;
	String password;
	long createdOn;
	String notficatonRegistrationId;
	List<Ticket> tickets;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getNotficatonRegistrationId() {
		return notficatonRegistrationId;
	}

	public void setNotficatonRegistrationId(String notficatonRegistrationId) {
		this.notficatonRegistrationId = notficatonRegistrationId;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(List<Ticket> tickets) {
		this.tickets = tickets;
	}

	public User() {
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userName=" + userName + ", emailId=" + emailId + ", password=" + password
				+ ", createdOn=" + createdOn + ", notficatonRegistrationId=" + notficatonRegistrationId + ", tickets="
				+ tickets + "]";
	}

}
