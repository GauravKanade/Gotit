package com.felisys.gotit.questions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.felisys.gotit.Adapters.MainMenuListAdapter;
import com.felisys.gotit.R;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.Utility;

import java.util.Arrays;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    ListView lvMainMenu;
    Button bTakeATest;
    final String[] mainMenuItems = {"My Tests", "Tickets", "Logout"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
        Log.i(Utility.LOG_TAG, ">>ProfileActivity.onCreate()");
        init();
    }

    private void init() {
        bTakeATest = (Button) findViewById(R.id.bTakeATest);
        lvMainMenu = (ListView) findViewById(R.id.lvMainMenu);
        MainMenuListAdapter mainMenuListAdapter = new MainMenuListAdapter(this, this, R.id.lvMainMenu, Arrays.asList(mainMenuItems));
        lvMainMenu.setAdapter(mainMenuListAdapter);
        lvMainMenu.setOnItemClickListener(this);
        bTakeATest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(Utility.LOG_TAG, ">>ProfileActivity.onClick()");
        switch (v.getId()) {
            case R.id.bTakeATest:
                takeATest();
                break;

        }
    }

    private void takeATest() {
        startActivity(new Intent(this, TargetActivity.class));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                startActivity(new Intent(this, PreviousTestsActivity.class));
                break;
        }
    }
}
