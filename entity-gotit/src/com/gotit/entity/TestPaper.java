package com.gotit.entity;

import java.util.Arrays;

public class TestPaper {
	String testId;
	String userId;
	String ticketId;
	String target;
	String subject;

	long createdOn;
	long updatedOn;
	long attemptedOn;
	long completedOn;
	long timeRemaining;
	int timeInMinutes;

	TestSection[] testSection;
	double totalMarks;
	double marksObatined;

	boolean isAttempted;
	boolean isAnswered;
	boolean isEvaluated;

	public TestPaper() {
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
	}

	public long getAttemptedOn() {
		return attemptedOn;
	}

	public void setAttemptedOn(long attemptedOn) {
		this.attemptedOn = attemptedOn;
	}

	public long getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(long completedOn) {
		this.completedOn = completedOn;
	}

	public long getTimeRemaining() {
		return timeRemaining;
	}

	public void setTimeRemaining(long timeRemaining) {
		this.timeRemaining = timeRemaining;
	}

	public int getTimeInMinutes() {
		return timeInMinutes;
	}

	public void setTimeInMinutes(int timeInMinutes) {
		this.timeInMinutes = timeInMinutes;
	}

	public TestSection[] getTestSection() {
		return testSection;
	}

	public void setTestSection(TestSection[] testSection) {
		this.testSection = testSection;
	}

	public double getTotalMarks() {
		return totalMarks;
	}

	public void setTotalMarks(double totalMarks) {
		this.totalMarks = totalMarks;
	}

	public double getMarksObatined() {
		return marksObatined;
	}

	public void setMarksObatined(double marksObatined) {
		this.marksObatined = marksObatined;
	}

	public boolean isAttempted() {
		return isAttempted;
	}

	public void setAttempted(boolean isAttempted) {
		this.isAttempted = isAttempted;
	}

	public boolean isAnswered() {
		return isAnswered;
	}

	public void setAnswered(boolean isAnswered) {
		this.isAnswered = isAnswered;
	}

	public boolean isEvaluated() {
		return isEvaluated;
	}

	public void setEvaluated(boolean isEvaluated) {
		this.isEvaluated = isEvaluated;
	}

	@Override
	public String toString() {
		return "TestPaper [testId=" + testId + ", userId=" + userId + ", ticketId=" + ticketId + ", target=" + target
				+ ", subject=" + subject + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn + ", attemptedOn="
				+ attemptedOn + ", completedOn=" + completedOn + ", timeRemaining=" + timeRemaining + ", timeInMinutes="
				+ timeInMinutes + ", testSection=" + Arrays.toString(testSection) + ", totalMarks=" + totalMarks
				+ ", marksObatined=" + marksObatined + ", isAttempted=" + isAttempted + ", isAnswered=" + isAnswered
				+ ", isEvaluated=" + isEvaluated + "]";
	}

}
