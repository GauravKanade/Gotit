package com.gotit.entity;

public class Subject {
	String subjectName;
	int numberOfQuestions;

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Subject() {
	}

	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(int numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	@Override
	public String toString() {
		return "Subject [subjectName=" + subjectName + ", numberOfQuestions=" + numberOfQuestions + "]";
	}

}
