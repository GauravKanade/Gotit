package com.gotit.questions;

import org.junit.Test;

import com.gotit.questions.controller.TestPaperController;

public class PracticePaperTest {

	TestPaperController practiceTestController = new TestPaperController();

	/**
	 * Test to check the behavior of flag provideFullMarksOnPartialAnswer
	 */
	@Test
	public void testProvideFullMarksOnPartialAnswer() {
		//TestPaper testPaper = TestDataProvider.getTestPaper();
		//MarksRule marksRule = new MarksRule();

		/*marksRule.setProvideFullMarksOnPartialAnswer(true);
		practiceTestController.evaluateTestPaperWithRule(testPaper, marksRule);
		String[] expectedFullAnswer = { "1", "3", "5", "6", "7" };
		String[] expectedZeroAnswer = { "2", "4" };
		for (Question question : testPaper.getQuestions()) {
			if (Arrays.asList(expectedFullAnswer).contains(question.getQuestionId()))
				Assert.assertTrue(question.getMarksObtained() == 1.0);
			else if (Arrays.asList(expectedZeroAnswer).contains(question.getQuestionId()))
				Assert.assertTrue(question.getMarksObtained() == 0.0);
		}

		marksRule.setProvideFullMarksOnPartialAnswer(false);
		practiceTestController.evaluateTestPaperWithRule(testPaper, marksRule);
		Log.i(">>" + testPaper);
		expectedFullAnswer = new String[] { "1", "3", "5" };
		expectedZeroAnswer = new String[] { "2", "4", "6", "7" };
		for (Question question : testPaper.getQuestions()) {
			if (Arrays.asList(expectedFullAnswer).contains(question.getQuestionId()))
				Assert.assertTrue(1.0 == question.getMarksObtained());
			else if (Arrays.asList(expectedZeroAnswer).contains(question.getQuestionId()))
				Assert.assertTrue(0.0 == question.getMarksObtained());
		}*/
	}

}
