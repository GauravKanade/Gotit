package com.gotit.entity;

public class Ticket {
	String ticketId;
	String testPaperId;
	String target;
	String orderId;
	boolean completed;
	boolean isTicketAvailable;

	public Ticket() {
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTestPaperId() {
		return testPaperId;
	}

	public void setTestPaperId(String testPaperId) {
		this.testPaperId = testPaperId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isTicketAvailable() {
		return isTicketAvailable;
	}

	public void setTicketAvailable(boolean isTicketAvailable) {
		this.isTicketAvailable = isTicketAvailable;
	}

	@Override
	public String toString() {
		return "Ticket [ticketId=" + ticketId + ", testPaperId=" + testPaperId + ", target=" + target + ", orderId="
				+ orderId + ", completed=" + completed + ", isTicketAvailable=" + isTicketAvailable + "]";
	}

}
