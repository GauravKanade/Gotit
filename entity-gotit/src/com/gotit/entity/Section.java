package com.gotit.entity;

import java.util.Arrays;

public class Section {

	String section;
	String description;
	String ruleId;
	String[] chapters;
	int numberOfQuestions;
	int marksPerQuestion;

	public Section() {
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String[] getChapters() {
		return chapters;
	}

	public void setChapters(String[] chapters) {
		this.chapters = chapters;
	}

	public int getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(int numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	public int getMarksPerQuestion() {
		return marksPerQuestion;
	}

	public void setMarksPerQuestion(int marksPerQuestion) {
		this.marksPerQuestion = marksPerQuestion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Section [section=" + section + ", description=" + description + ", ruleId=" + ruleId + ", chapters="
				+ Arrays.toString(chapters) + ", numberOfQuestions=" + numberOfQuestions + ", marksPerQuestion="
				+ marksPerQuestion + "]";
	}

}
