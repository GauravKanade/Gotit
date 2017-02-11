package com.felisys.gotit.questions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.felisys.gotit.Adapters.QuestionViewPagerAdapter;
import com.felisys.gotit.R;
import com.felisys.gotit.database.DatabaseHandler;
import com.felisys.gotit.entity.Question;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.util.CircleDisplay;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.Utility;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity implements View.OnClickListener, FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    Context context;
    TestPaper testPaper;
    CountDownTimer countDownTimer;
    TextView tvHours, tvMinutes, tvSeconds;
    ViewPager vpQuestons;
    FloatingActionsMenu fam;
    FloatingActionButton fabSubmit;
    AlertDialog alertDialog;
    long milliseconds;
    boolean isMenuExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
    }

    @Override
    protected void onPause() {
        super.onStop();
        pauseTest();
        isMenuExpanded = false;
    }

    @Override
    protected void onResume() {
        super.onStart();
        startOrResumeTest();
    }

    private void startOrResumeTest() {
        context = this;
        if (Utility.databaseHandler == null)
            Utility.databaseHandler = new DatabaseHandler(context);

        if (testPaper == null) {
            Bundle data = getIntent().getExtras();
            String testId = data.getString("testId");
            testPaper = Utility.databaseHandler.getTestPaperById(testId);
        }
        tvHours = (TextView) findViewById(R.id.tvHours);
        tvMinutes = (TextView) findViewById(R.id.tvMinutes);
        tvSeconds = (TextView) findViewById(R.id.tvSeconds);
        vpQuestons = (ViewPager) findViewById(R.id.vpQuestions);
        fabSubmit = (FloatingActionButton) findViewById(R.id.fabSubmit);
        fabSubmit.setOnClickListener(this);
        //initalizeTestPaper counter
        if (testPaper.isAttempted()) {
            resetCounter(testPaper.getTimeRemaining());
            if (isMenuExpanded)
                startTest();
            else
                inflateMenu(false);
        } else {
            resetCounter(testPaper.getTimeInMinutes() * 60000);
            if (isMenuExpanded)
                startTest();
            else
                inflateMenu(true);
        }
    }

    public void startTest() {
        testPaper.setAttempted(true);
        Utility.databaseHandler.saveTestPaper(testPaper);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("testId", testPaper.getTestId());
        requestMap.put("target", testPaper.getTarget());
        requestMap.put("attempted", true);

        InternetHandler.post(Utility.API_UPDATE_TESTPAPER, requestMap, Map.class);
        countDownTimer.start();
        vpQuestons.setAdapter(new QuestionViewPagerAdapter(context, testPaper));
        int startIndexNumber = 0;
        for(int i=0;i<testPaper.getQuestions().length; i++){
            Question question = testPaper.getQuestions()[i];
            if(question.getAnswerByUser()==null || question.getAnswerByUser().length==0) {
                startIndexNumber = i;
                break;
            }
        }
        vpQuestons.setCurrentItem(startIndexNumber);
        fam = (FloatingActionsMenu) findViewById(R.id.famMainMenu);

        fam.setOnFloatingActionsMenuUpdateListener(this);
    }

    private void inflateMenu(boolean isStart) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.question_menu_inflator_layout, null);
        builder.setView(view);
        alertDialog = builder.create();
        view.findViewById(R.id.bStartTest).setOnClickListener(this);
        if (isStart)
            ((Button) view.findViewById(R.id.bStartTest)).setText("Start");
        else
            ((Button) view.findViewById(R.id.bStartTest)).setText("Resume");
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bStartTest:
                alertDialog.dismiss();
                startTest();
                break;
            case R.id.bViewResults:
                Log.i(Utility.LOG_TAG, "TO CALL VIEW RESULTS");
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("testId", testPaper.getTestId());
                startActivity(intent);
                finish();
                break;
            case R.id.fabSubmit:
                fam.collapse();
                testCompleted();
                break;
        }
    }

    @Override
    public void onMenuExpanded() {
        findViewById(R.id.flQuestionOverlay).setBackgroundColor(0xd9ffffff);
        pauseTest();
        isMenuExpanded = true;
    }

    @Override
    public void onMenuCollapsed() {
        findViewById(R.id.flQuestionOverlay).setBackgroundColor(Color.TRANSPARENT);
        startOrResumeTest();
    }

    private void resetCounter(long startTime) {
        countDownTimer = new CountDownTimer(startTime, 1000) {

            public void onTick(long millisUntilFinished) {
                int h = (int) (millisUntilFinished / 3600000);
                int m = (int) (millisUntilFinished - h * 3600000) / 60000;
                int s = (int) (millisUntilFinished - h * 3600000 - m * 60000) / 1000;
                tvHours.setText((h < 10) ? "0" + h : h + "");
                tvMinutes.setText(":" + ((m < 10) ? "0" + m : m + ""));
                tvSeconds.setText(":" + ((s < 10) ? "0" + s : s + ""));
                milliseconds = millisUntilFinished;
            }

            public void onFinish() {
                tvSeconds.setText(":00");
                testCompleted();
            }

        };
    }

    private void pauseTest() {
        countDownTimer.cancel();
        testPaper.setTimeRemaining(milliseconds);
        Utility.databaseHandler.saveTestPaper(testPaper);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("testId", testPaper.getTestId());
        requestMap.put("target", testPaper.getTarget());
        requestMap.put("timeRemaining", milliseconds);

        InternetHandler.post(Utility.API_UPDATE_TESTPAPER, requestMap, Map.class);
    }

    private void testCompleted() {
        Log.i(Utility.LOG_TAG, ">>TestActivity.testCompleted()");
        countDownTimer.cancel();
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.test_completed_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        builder.setView(view);
        builder.show();

        testPaper.setAnswered(true);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("testId", testPaper.getTestId());
        requestMap.put("target", testPaper.getTarget());
        requestMap.put("isAnswered", true);

        testPaper = InternetHandler.post(Utility.API_EVALUATE_TESTPAPER, requestMap, TestPaper.class);
        Utility.databaseHandler.saveTestPaper(testPaper);
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
        bViewResults.setOnClickListener(this);
    }


}
