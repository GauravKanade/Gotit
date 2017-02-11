package com.felisys.gotit.entity;

import java.util.Arrays;

public class Question {
    String questionId;
    String questionBody;
    String targetId;
    int marks;
    int marksObtained;
    String subject;
    String choices[];
    int answer[];
    int[] answerByUser;
    public boolean[] clicked;

    public int getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(int marksObtained) {
        this.marksObtained = marksObtained;
    }

    public int[] getAnswerByUser() {
        return answerByUser;
    }

    public void setAnswerByUser(int[] answerByUser) {
        this.answerByUser = answerByUser;
    }

    public Question() {
        clicked = new boolean[4];
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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    @Override
    public String toString() {
        return "Question [questionId=" + questionId + ", questionBody=" + questionBody + ", targetId=" + targetId
                + ", marks=" + marks + ", marksObtained=" + marksObtained + ", subject=" + subject + ", choices="
                + Arrays.toString(choices) + ", answer=" + Arrays.toString(answer) + ", answerByUser="
                + Arrays.toString(answerByUser) + "]";
    }

}
