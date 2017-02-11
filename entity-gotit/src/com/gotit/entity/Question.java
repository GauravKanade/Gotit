package com.gotit.entity;

import java.util.Arrays;

public class Question {
	String questionId;
	String questionBody;
	String target;
	String subject;
	String chapter;
	double marks;
	double marksObtained;
	String choices[];
	int answer[];
	int[] answerByUser;

	public Question() {
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getQuestionBody() {
		return questionBody;
	}

	public void setQuestionBody(String questionBody) {
		this.questionBody = questionBody;
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

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public double getMarks() {
		return marks;
	}

	public void setMarks(double marks) {
		this.marks = marks;
	}

	public double getMarksObtained() {
		return marksObtained;
	}

	public void setMarksObtained(double marksObtained) {
		this.marksObtained = marksObtained;
	}

	public String[] getChoices() {
		return choices;
	}

	public void setChoices(String[] choices) {
		this.choices = choices;
	}

	public int[] getAnswer() {
		return answer;
	}

	public void setAnswer(int[] answer) {
		this.answer = answer;
	}

	public int[] getAnswerByUser() {
		return answerByUser;
	}

	public void setAnswerByUser(int[] answerByUser) {
		this.answerByUser = answerByUser;
	}

	@Override
	public String toString() {
		return "Question [questionId=" + questionId + ", questionBody=" + questionBody + ", target=" + target
				+ ", subject=" + subject + ", chapter=" + chapter + ", marks=" + marks + ", marksObtained="
				+ marksObtained + ", choices=" + Arrays.toString(choices) + ", answer=" + Arrays.toString(answer)
				+ ", answerByUser=" + Arrays.toString(answerByUser) + "]";
	}

}
