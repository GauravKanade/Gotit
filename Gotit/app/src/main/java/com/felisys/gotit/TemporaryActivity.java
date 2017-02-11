package com.felisys.gotit;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.felisys.gotit.database.DatabaseHandler;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.util.CircleDisplay;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.Utility;

/**
 * Created by gauravkanade on 1/28/17.
 */

public class TemporaryActivity extends AppCompatActivity {

    ProgressBar progressBar;
    Button bViewResults;
    TestPaper testPaper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_completed_layout);

        findViewById(R.id.displayResults).setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
bViewResults  = (Button) findViewById(R.id.bViewResults);
        bViewResults.setVisibility(View.GONE);
        if(Utility.databaseHandler == null)
            Utility.databaseHandler = new DatabaseHandler(this);
        testPaper = Utility.databaseHandler.getTestPaperById("GUYbWC-5543642472");
       // new ProgressDialogAnimator().execute();
        //initLayouts();
        testCompleted();
    }

    private void testCompleted() {
        Log.i(Utility.LOG_TAG, ">>TestActivity.testCompleted()");
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.test_completed_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
        builder.setView(view);
        builder.show();

        testPaper.setAnswered(true);
        //Utility.databaseHandler.saveTestPaper(testPaper);
        //testPaper = InternetHandler.post(Utility.API_EVALUATE_TESTPAPER, testPaper, TestPaper.class);
        showMarks(view);
    }

    private void showMarks(View view) {

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Button bViewResults = (Button) view.findViewById(R.id.bViewResults);
        CircleDisplay cd = (CircleDisplay) view.findViewById(R.id.displayResults);
        cd.setVisibility(View.VISIBLE);
        cd.setAnimDuration(3000);
        cd.setValueWidthPercent(20f);
        cd.setTextSize(36f);
        cd.setColor(0xff558b2f);
        cd.setDrawText(false);
        cd.setDrawInnerCircle(true);
        cd.setTouchEnabled(false);
        cd.setStepSize(0.5f);
        // cd.setCustomText(...); // sets a custom array of text
        cd.showValue(testPaper.getMarksObatined(), testPaper.getTotalMarks(), true);
        bViewResults.setText(testPaper.getMarksObatined() + "/" + testPaper.getTotalMarks());
        progressBar.setVisibility(View.GONE);
        bViewResults.setVisibility(View.VISIBLE);
        //bViewResults.setOnClickListener(this);
    }

    public class ProgressDialogAnimator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
    }
}
