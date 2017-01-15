package com.gotit.entity;

public class Ticket {
	String ticketId;
	long createdOn;
	String testPaperId;
	String target;
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	boolean isTicketAvailable;
	String paymentInformation;

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public String getTestPaperId() {
		return testPaperId;
	}

	public void setTestPaperId(String testPaperId) {
		this.testPaperId = testPaperId;
	}

	public boolean isTicketAvailable() {
		return isTicketAvailable;
	}

	public void setTicketAvailable(boolean isTicketAvailable) {
		this.isTicketAvailable = isTicketAvailable;
	}

	public String getPaymentInformation() {
		return paymentInformation;
	}

	public void setPaymentInformation(String paymentInformation) {
		this.paymentInformation = paymentInformation;
	}

	public Ticket() {
	}

	@Override
	public String toString() {
		return "Ticket [ticketId=" + ticketId + ", createdOn=" + createdOn + ", testPaperId=" + testPaperId
				+ ", target=" + target + ", isTicketAvailable=" + isTicketAvailable + ", paymentInformation="
				+ paymentInformation + "]";
	}

}
