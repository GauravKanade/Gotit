package com.gotit.entity;

import java.util.List;

public class Target {
	String targetId;
	String targetName;
	List<Subject> subjects;

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public String toString() {
		return "Target [targetId=" + targetId + ", targetName=" + targetName + ", subjects=" + subjects + "]";
	}

	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}

	public Target() {
	}

}
