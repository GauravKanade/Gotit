package com.gotit.entity;

import java.util.List;

public class User {

	String userId;
	String userFirstName;
	String userLastName;
	String userPhotoURL;
	String emailId;
	String phoneNumber;
	long createdOn;
	String notficatonRegistrationId;
	List<Ticket> tickets;

	public User() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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

	public String getUserPhotoURL() {
		return userPhotoURL;
	}

	public void setUserPhotoURL(String userPhotoURL) {
		this.userPhotoURL = userPhotoURL;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", userFirstName=" + userFirstName + ", userLastName=" + userLastName
				+ ", userPhotoURL=" + userPhotoURL + ", emailId=" + emailId + ", phoneNumber=" + phoneNumber
				+ ", createdOn=" + createdOn + ", notficatonRegistrationId=" + notficatonRegistrationId + ", tickets="
				+ tickets + "]";
	}

}
