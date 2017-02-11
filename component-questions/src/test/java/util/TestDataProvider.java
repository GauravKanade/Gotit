package util;

import org.apache.commons.lang3.RandomStringUtils;

import com.gotit.entity.Question;
import com.gotit.entity.TestPaper;

public class TestDataProvider {

	public static TestPaper getTestPaper() {
		TestPaper testPaper = new TestPaper();
		testPaper.setTestId(RandomStringUtils.randomAlphabetic(10));
		testPaper.setAnswered(true);

		Question question1 = new Question();
		question1.setQuestionId("1");
		question1.setAnswer(new int[] { 1 });
		question1.setAnswerByUser(new int[] { 1 });
		question1.setMarks(1.0);

		Question question2 = new Question();
		question2.setQuestionId("2");
		question2.setAnswer(new int[] { 1 });
		question2.setAnswerByUser(new int[] { 2 });
		question2.setMarks(1.0);

		Question question3 = new Question();
		question3.setQuestionId("3");
		question3.setAnswer(new int[] {});
		question3.setAnswerByUser(new int[] {});
		question3.setMarks(1.0);

		Question question4 = new Question();
		question4.setQuestionId("4");
		question4.setAnswer(new int[] { 2 });
		question4.setAnswerByUser(new int[] {});
		question4.setMarks(1.0);

		Question question5 = new Question();
		question5.setQuestionId("5");
		question5.setAnswer(new int[] { 2, 3 });
		question5.setAnswerByUser(new int[] { 2, 3 });
		question5.setMarks(1.0);

		Question question6 = new Question();
		question6.setQuestionId("6");
		question6.setAnswer(new int[] { 2, 3 });
		question6.setAnswerByUser(new int[] { 2 });
		question6.setMarks(1.0);

		Question question7 = new Question();
		question7.setQuestionId("7");
		question7.setAnswer(new int[] { 2, 3 });
		question7.setAnswerByUser(new int[] { 2, 1 });
		question7.setMarks(1.0);

		Question question8 = new Question();
		question8.setQuestionId("8");
		question8.setAnswer(new int[] { 2, 3 });
		question8.setAnswerByUser(new int[] { 2, 3, 1 });
		question8.setMarks(1.0);
		
		Question[] questionArray = new Question[8];
		questionArray[0] = question1;
		questionArray[1] = question2;
		questionArray[2] = question3;
		questionArray[3] = question4;
		questionArray[4] = question5;
		questionArray[5] = question6;
		questionArray[6] = question7;
		questionArray[7] = question8;
		
		//testPaper.setQuestions(questionArray);
		return testPaper;

	}

}
