package com.gotit.entity;

import java.util.Arrays;

public class Order {
	String orderId;
	String userId;
	long purchasedOn;
	String[] ticketIds;
	Payment payment;
	// Other order related fields

	public Order() {
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public long getPurchasedOn() {
		return purchasedOn;
	}

	public void setPurchasedOn(long purchasedOn) {
		this.purchasedOn = purchasedOn;
	}

	public String[] getTicketIds() {
		return ticketIds;
	}

	public void setTicketIds(String[] ticketIds) {
		this.ticketIds = ticketIds;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", userId=" + userId + ", purchasedOn=" + purchasedOn + ", ticketIds="
				+ Arrays.toString(ticketIds) + ", payment=" + payment + "]";
	}

}
