package com.gotit.entity;

import java.util.Arrays;

public class TestSection {
	
	String section;
	String description;
	Question[] questions;

	public TestSection() {
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public Question[] getQuestions() {
		return questions;
	}

	public void setQuestions(Question[] questions) {
		this.questions = questions;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "TestSection [section=" + section + ", description=" + description + ", questions="
				+ Arrays.toString(questions) + "]";
	}
	
}
