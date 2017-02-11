package com.gotit.entity;

public class Payment {
	String paymentMethod;
	String cardNumber;
	String nameOnCard;
	String cardValidForm;
	String cardValidTo;
	double amount;
	String status;

	public Payment() {
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getNameOnCard() {
		return nameOnCard;
	}

	public void setNameOnCard(String nameOnCard) {
		this.nameOnCard = nameOnCard;
	}

	public String getCardValidForm() {
		return cardValidForm;
	}

	public void setCardValidForm(String cardValidForm) {
		this.cardValidForm = cardValidForm;
	}

	public String getCardValidTo() {
		return cardValidTo;
	}

	public void setCardValidTo(String cardValidTo) {
		this.cardValidTo = cardValidTo;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Payment [paymentMethod=" + paymentMethod + ", cardNumber=" + cardNumber + ", nameOnCard=" + nameOnCard
				+ ", cardValidForm=" + cardValidForm + ", cardValidTo=" + cardValidTo + ", amount=" + amount
				+ ", status=" + status + "]";
	}

}
