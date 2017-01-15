package com.gotit.entity;

import java.util.List;

public class TestPaper {
	String testId;
	String userId;
	long createdOn;
	long attemptedOn;
	long completedOn;
	List<Question> questions;
	List<Answer> answersByUser;
	int totalMarks;
	int marksObatined;
	String target;
	String subject;
	boolean isAnswered;
	boolean isEvaluated;

	public boolean isAnswered() {
		return isAnswered;
	}

	public void setAnswered(boolean isAttempted) {
		this.isAnswered = isAttempted;
	}

	public boolean isEvaluated() {
		return isEvaluated;
	}

	public void setEvaluated(boolean isEvaluated) {
		this.isEvaluated = isEvaluated;
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

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
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

	public int getTotalMarks() {
		return totalMarks;
	}

	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}

	public int getMarksObatined() {
		return marksObatined;
	}

	public void setMarksObatined(int marksObatined) {
		this.marksObatined = marksObatined;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public List<Answer> getAnswersByUser() {
		return answersByUser;
	}

	public void setAnswersByUser(List<Answer> answersByUser) {
		this.answersByUser = answersByUser;
	}

	@Override
	public String toString() {
		return "TestPaper [testId=" + testId + ", userId=" + userId + ", createdOn=" + createdOn + ", attemptedOn="
				+ attemptedOn + ", completedOn=" + completedOn + ", questions=" + questions + ", answersByUser="
				+ answersByUser + ", totalMarks=" + totalMarks + ", marksObatined=" + marksObatined + ", target="
				+ target + ", subject=" + subject + ", isAnswered=" + isAnswered + ", isEvaluated=" + isEvaluated + "]";
	}

}
