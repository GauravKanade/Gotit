package com.felisys.gotit.questions;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.felisys.gotit.Adapters.ViewResultsAdapter;
import com.felisys.gotit.R;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.Utility;

public class ResultActivity extends AppCompatActivity {

    Context context;
    ListView lvResult;
    TestPaper testPaper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
        init();
    }

    private void init() {
        context = this;
        Bundle data = getIntent().getExtras();
        String testId = data.getString("testId");
        testPaper = Utility.databaseHandler.getTestPaperById(testId);
        lvResult = (ListView) findViewById(R.id.lvResultList);
        ViewResultsAdapter viewResultsAdapter = new ViewResultsAdapter(context, R.id.lvResultList, testPaper);
        lvResult.setAdapter(viewResultsAdapter);
    }
}
