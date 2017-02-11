package com.felisys.gotit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.felisys.gotit.R;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.util.Utility;

import java.util.List;

/**
 * Created by gauravkanade on 2/1/17.
 */

public class PreviousTestsAdapter extends ArrayAdapter<TestPaper> {
    Context context;
    List<TestPaper> testPaperList;

    public PreviousTestsAdapter(Context context, int resource, List<TestPaper> objects) {
        super(context, resource, objects);
        this.context = context;
        this.testPaperList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.previous_test_layout, null);
        ((TextView) view.findViewById(R.id.tvTarget)).setText(testPaperList.get(position).getTarget());
        ((TextView) view.findViewById(R.id.tvSubject)).setText(testPaperList.get(position).getSubject());

        if (testPaperList.get(position).isEvaluated()) {
            ((TextView) view.findViewById(R.id.tvTimeOfTest)).setText("Test completed on " + Utility.getDateFormat(testPaperList.get(position).getCompletedOn()));
        } else {
            ((TextView) view.findViewById(R.id.tvTimeOfTest)).setText("Test not completed");
            ((TextView) view.findViewById(R.id.tvTimeOfTest)).setTextColor(Color.RED);
        }
        return view;
    }
}
