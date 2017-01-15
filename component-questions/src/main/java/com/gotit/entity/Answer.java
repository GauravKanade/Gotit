package com.gotit.entity;

public class Answer {
	String questionId;

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	String[] answers;
	int marksObtained;

	public String[] getAnswer() {
		return answers;
	}

	public void setAnswer(String[] answer) {
		this.answers = answer;
	}

	public int getMarksObtained() {
		return marksObtained;
	}

	public void setMarksObtained(int marksObtained) {
		this.marksObtained = marksObtained;
	}

	@Override
	public String toString() {
		return "Answer [questionId=" + questionId + ", answer=" + answers + ", marksObtained=" + marksObtained + "]";
	}

	public Answer() {
	}
}
