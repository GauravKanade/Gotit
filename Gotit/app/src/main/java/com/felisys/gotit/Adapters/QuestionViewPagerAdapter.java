package com.felisys.gotit.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.felisys.gotit.R;
import com.felisys.gotit.entity.Question;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.Utility;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gauravkanade on 1/27/17.
 */

public class QuestionViewPagerAdapter extends PagerAdapter {

    Context context;
    TestPaper testPaper;

    public QuestionViewPagerAdapter(Context context, TestPaper testPaper) {
        this.context = context;
        this.testPaper = testPaper;
    }

    @Override
    public int getCount() {
        return testPaper.getQuestions().length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Log.d(Utility.LOG_TAG, ">>initiating viewPager @ position " + position);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.question_layout, null);
        ((TextView) view.findViewById(R.id.tvQuestionBody)).setText(testPaper.getQuestions()[position].getQuestionBody());
        ((TextView) view.findViewById(R.id.tvQuestionNo)).setText("Question No " + String.valueOf(position + 1));
        final Button[] buttonArray = new Button[4];
        buttonArray[0] = ((Button) view.findViewById(R.id.bOption1));

        buttonArray[1] = ((Button) view.findViewById(R.id.bOption2));
        buttonArray[2] = ((Button) view.findViewById(R.id.bOption3));
        buttonArray[3] = ((Button) view.findViewById(R.id.bOption4));

        buttonArray[0].setText(testPaper.getQuestions()[position].getChoices()[0]);
        buttonArray[1].setText(testPaper.getQuestions()[position].getChoices()[1]);
        buttonArray[2].setText(testPaper.getQuestions()[position].getChoices()[2]);
        buttonArray[3].setText(testPaper.getQuestions()[position].getChoices()[3]);
        resetColors(buttonArray, position);
        buttonArray[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColors(buttonArray, position, 0);
                updateAnswer(0, position);
            }
        });
        buttonArray[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColors(buttonArray, position, 1);
                updateAnswer(1, position);
            }
        });
        buttonArray[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColors(buttonArray, position, 2);
                updateAnswer(2, position);
            }
        });
        buttonArray[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetColors(buttonArray, position, 3);
                updateAnswer(3, position);
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    void updateAnswer(int option, int questionIndexNumber) {
        Map<String, Object> updateAnswerRequest = new HashMap<>();
        updateAnswerRequest.put("testId", testPaper.getTestId());
        updateAnswerRequest.put("targetId", testPaper.getTarget());
        updateAnswerRequest.put("questionId", testPaper.getQuestions()[questionIndexNumber].getQuestionId());
        updateAnswerRequest.put("timeRemaining", testPaper.getTimeRemaining());
        List<Integer> answerByUser = new ArrayList<>();
        answerByUser.add(option+1);
        updateAnswerRequest.put("answerByUser", answerByUser);
        Log.i(Utility.LOG_TAG, ">>TestActivity.updateAnswer() - request: " + updateAnswerRequest.toString());

        Map responseMap = InternetHandler.post(Utility.API_SAVE_ANSWER, updateAnswerRequest, Map.class);
       Log.i(Utility.LOG_TAG,">>responseFrom saveAnswer() - " + responseMap);

        testPaper.getQuestions()[questionIndexNumber].setAnswerByUser(Utility.convertToArray(answerByUser));
        Utility.databaseHandler.saveTestPaper(testPaper);
    }

    void resetColors(Button[] buttonArray, int position, int highlightOption){
        for (int i = 0; i < 4; i++) {
            if(i==highlightOption){
                testPaper.getQuestions()[position].clicked[i] = true;
                buttonArray[i].setBackgroundResource(R.color.medium);
            } else {
                testPaper.getQuestions()[position].clicked[i] = false;
                buttonArray[i].setBackgroundResource(R.color.dark);
            }
        }
    }

    void resetColors(Button[] buttonArray, int position){
        for(int i=0;i<4;i++){
            if(testPaper.getQuestions()[position].clicked[i])
                buttonArray[i].setBackgroundResource(R.color.medium);
            else
                buttonArray[i].setBackgroundResource(R.color.dark);

        }
    }
}
