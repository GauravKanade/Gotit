package com.felisys.gotit.questions;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.felisys.gotit.Adapters.PreviousTestsAdapter;
import com.felisys.gotit.R;
import com.felisys.gotit.database.DatabaseHandler;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.Utility;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

public class PreviousTestsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    Context context;
    ListView lvPreviousTests;
    TextView tvNoTests;
    List<TestPaper> testPaperList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_tests);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
        init();
    }

    private void init() {
        context = this;
        if (Utility.databaseHandler == null)
            Utility.databaseHandler = new DatabaseHandler(context);
        lvPreviousTests = (ListView) findViewById(R.id.lvPreviousTestList);
        tvNoTests = (TextView) findViewById(R.id.tvNoTests);

        testPaperList = Utility.databaseHandler.getAllTestPapers();
        if (testPaperList.size() == 0) {
            Log.i(Utility.LOG_TAG, "-> No tests to display");
            lvPreviousTests.setVisibility(View.GONE);
        } else {
            Log.i(Utility.LOG_TAG, "-> Tests available");
            tvNoTests.setVisibility(View.GONE);
            PreviousTestsAdapter previousTestsAdapter = new PreviousTestsAdapter(context, R.id.lvPreviousTestList, testPaperList);
            lvPreviousTests.setAdapter(previousTestsAdapter);
            lvPreviousTests.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TestPaper testPaper = testPaperList.get(position);
        Log.i(Utility.LOG_TAG, ">>onCLick: testPaperId: " + testPaper.getTestId() + ", isEvaluated: " + testPaper.isEvaluated());
        if(testPaper.isEvaluated()){
            Intent intent = new Intent(this, ResultActivity.class);
            intent.putExtra("testId", testPaper.getTestId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("testId", testPaper.getTestId());
            startActivity(intent);
            finish();
        }
    }
}
