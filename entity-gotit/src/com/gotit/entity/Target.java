package com.gotit.entity;

import java.util.Arrays;

public class Target {
	String targetId;
	String description;
	Subject[] subjects;
	String imageURL;

	public Target() {
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Subject[] getSubjects() {
		return subjects;
	}

	public void setSubjects(Subject[] subjects) {
		this.subjects = subjects;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	@Override
	public String toString() {
		return "Target [targetId=" + targetId + ", description=" + description + ", subjects="
				+ Arrays.toString(subjects) + ", imageURL=" + imageURL + "]";
	}

}
