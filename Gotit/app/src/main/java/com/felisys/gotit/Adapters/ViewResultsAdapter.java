package com.felisys.gotit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felisys.gotit.R;
import com.felisys.gotit.entity.Question;
import com.felisys.gotit.entity.TestPaper;

/**
 * Created by gauravkanade on 1/31/17.
 */

public class ViewResultsAdapter extends ArrayAdapter<Question> {
    Context context;
    TestPaper testPaper;


    public ViewResultsAdapter(Context context, int resource, TestPaper testPaper) {
        super(context, resource, testPaper.getQuestions());
        this.testPaper = testPaper;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Question question = testPaper.getQuestions()[position];
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_result_layout, null);
        ((TextView) view.findViewById(R.id.tvQuestionNo)).setText(String.valueOf(position + 1));
        ((TextView) view.findViewById(R.id.tvQuestionBody)).setText(question.getQuestionBody());
        if (question.getAnswerByUser() != null)
            ((TextView) view.findViewById(R.id.tvYourAnswer)).setText("Your answer: " + question.getChoices()[question.getAnswerByUser()[0] - 1]);
        else {
            ((TextView) view.findViewById(R.id.tvYourAnswer)).setText("You have not answered this question");
            view.findViewById(R.id.llResultColor).setBackgroundColor(Color.RED);
        }
        if (question.getMarksObtained() == 0) {
            String correctAnswer = null;
            for (int answerIndex : question.getAnswer()) {
                if (correctAnswer == null)
                    correctAnswer = question.getChoices()[answerIndex-1];
                else
                    correctAnswer += ", " + question.getChoices()[answerIndex-1];
            }
            correctAnswer = "Correct answer: " + correctAnswer;
            ((TextView) view.findViewById(R.id.tvActualAnswer)).setText(correctAnswer);
            view.findViewById(R.id.llResultColor).setBackgroundColor(Color.RED);
        } else {
            view.findViewById(R.id.tvActualAnswer).setVisibility(View.GONE);
        }
        return view;
    }
}
