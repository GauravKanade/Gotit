package com.gotit.questions.util;

import java.util.Comparator;

import com.gotit.entity.TestPaper;

public class TestPaperComparator implements Comparator<TestPaper> {

	@Override
	public int compare(TestPaper o1, TestPaper o2) {
		if (o1.getCompletedOn() == o2.getCompletedOn())
			return 0;
		else if (o1.getCompletedOn() < o2.getCompletedOn())
			return -1;
		else
			return 1;
	}

}
