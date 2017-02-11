package com.gotit.entity;

public class MarksRule {
	String ruleId;
	boolean provideFullMarksOnPartialAnswer;
	boolean provideFractionalMarksOnPartialAnswer;
	boolean ignoreExtraAnswers;
	boolean negateFullMarksOnExtraAnswer;
	boolean assignZeroMarksOnExtraAnswer;
	int numberOfWrongAnswersForNegativeMarking;
	double negativeMarks;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public boolean provideFullMarksOnPartialAnswer() {
		return provideFullMarksOnPartialAnswer;
	}

	public void setProvideFullMarksOnPartialAnswer(boolean provideFullMarksOnPartialAnswer) {
		this.provideFullMarksOnPartialAnswer = provideFullMarksOnPartialAnswer;
	}

	public boolean provideFractionalMarksOnPartialAnswer() {
		return provideFractionalMarksOnPartialAnswer;
	}

	public void setProvideFractionalMarksOnPartialAnswer(boolean provideFractionalMarksOnPartialAnswer) {
		this.provideFractionalMarksOnPartialAnswer = provideFractionalMarksOnPartialAnswer;
	}

	public boolean ignoreExtraAnswers() {
		return ignoreExtraAnswers;
	}

	public void setIgnoreExtraAnswers(boolean ignoreExtraAnswers) {
		this.ignoreExtraAnswers = ignoreExtraAnswers;
	}

	public boolean negateFullMarksOnExtraAnswer() {
		return negateFullMarksOnExtraAnswer;
	}

	public void setNegateFullMarksOnExtraAnswer(boolean negateFullMarksOnExtraAnswer) {
		this.negateFullMarksOnExtraAnswer = negateFullMarksOnExtraAnswer;
	}

	public boolean assignZeroMarksOnExtraAnswer() {
		return assignZeroMarksOnExtraAnswer;
	}

	public void setAssignZeroMarksOnExtraAnswer(boolean assignZeroMarksOnExtraAnswer) {
		this.assignZeroMarksOnExtraAnswer = assignZeroMarksOnExtraAnswer;
	}

	public int getNumberOfWrongAnswersForNegativeMarking() {
		return numberOfWrongAnswersForNegativeMarking;
	}

	public void setNumberOfWrongAnswersForNegativeMarking(int numberOfWrongAnswersForNegativeMarking) {
		this.numberOfWrongAnswersForNegativeMarking = numberOfWrongAnswersForNegativeMarking;
	}

	public double getNegativeMarks() {
		return negativeMarks;
	}

	public void setNegativeMarks(double negativeMarks) {
		this.negativeMarks = negativeMarks;
	}

	@Override
	public String toString() {
		return "MarksRule [ruleId=" + ruleId + ", provideFullMarksOnPartialAnswer=" + provideFullMarksOnPartialAnswer
				+ ", provideFractionalMarksOnPartialAnswer=" + provideFractionalMarksOnPartialAnswer
				+ ", ignoreExtraAnswers=" + ignoreExtraAnswers + ", negateFullMarksOnExtraAnswer="
				+ negateFullMarksOnExtraAnswer + ", assignZeroMarksOnExtraAnswer=" + assignZeroMarksOnExtraAnswer
				+ ", numberOfWrongAnswersForNegativeMarking=" + numberOfWrongAnswersForNegativeMarking
				+ ", negativeMarks=" + negativeMarks + "]";
	}

	public MarksRule() {
	}
}
