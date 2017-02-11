package com.gotit.entity;

import java.util.Arrays;

public class Subject {
	String subjectName;
	Section[] sections;
	String[] chapters;
	int timeInMinutes;

	public Subject() {
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Section[] getSections() {
		return sections;
	}

	public void setSections(Section[] sections) {
		this.sections = sections;
	}

	public int getTimeInMinutes() {
		return timeInMinutes;
	}

	public void setTimeInMinutes(int timeInMinutes) {
		this.timeInMinutes = timeInMinutes;
	}

	public String[] getChapters() {
		return chapters;
	}

	public void setChapters(String[] chapters) {
		this.chapters = chapters;
	}

	@Override
	public String toString() {
		return "Subject [subjectName=" + subjectName + ", sections=" + Arrays.toString(sections) + ", chapters="
				+ Arrays.toString(chapters) + ", timeInMinutes=" + timeInMinutes + "]";
	}

	
}
