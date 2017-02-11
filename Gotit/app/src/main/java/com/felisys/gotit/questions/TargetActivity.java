package com.felisys.gotit.questions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.felisys.gotit.Adapters.TargetExpandableListViewAdapter;
import com.felisys.gotit.MainActivity;
import com.felisys.gotit.R;
import com.felisys.gotit.database.DatabaseHandler;
import com.felisys.gotit.entity.Subject;
import com.felisys.gotit.entity.Target;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.entity.Ticket;
import com.felisys.gotit.entity.User;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.ObjectMapperUtil;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.SharedPreferencesHandler;
import com.felisys.gotit.util.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TargetActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {

    Context context;
    ExpandableListView elvTargetList;
    Target[] targetArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
        init();

    }

    private void init() {
        context = this;
        if (Utility.databaseHandler == null)
            Utility.databaseHandler = new DatabaseHandler(context);
        elvTargetList = (ExpandableListView) findViewById(R.id.elvTargetList);
        elvTargetList.setOnChildClickListener(this);
        getTargetsFromServer();
        getUserDataFromServer();

    }

    private void getUserDataFromServer() {
        String userId = SharedPreferencesHandler.getStoredUserId(context);
        String url = Utility.API_GET_USER_BY_ID.replace(Utility.USER_ID_PH, userId);
        User user = InternetHandler.get(url, User.class);
        Utility.databaseHandler.saveUser(user);
        SharedPreferencesHandler.saveUserInfo(context, user.getUserId(), user.getUserName(), user.getEmailId());
    }

    private void getTargetsFromServer() {
        final String url = Utility.API_GET_ALL_TARGETS;
        new AsyncHttpClient().get(context, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i(Utility.LOG_TAG, ">>onSuccess - " + new String(bytes));
                try {
                    targetArray = ObjectMapperUtil.getConfiguredObjectMapper().readValue(new String(bytes), Target[].class);
                    populateTargetsTolistView();

                } catch (IOException e) {
                    Log.e(Utility.LOG_TAG, "IO EXCEPTION in handling response from " + url, e);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e(Utility.LOG_TAG, ">>onFailure", throwable);
                Toast.makeText(context, "Oops!! Looks like something went wrong. Please try again later", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateTargetsTolistView() {
        TargetExpandableListViewAdapter adapter = new TargetExpandableListViewAdapter(context, targetArray);
        elvTargetList.setAdapter(adapter);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        String target = targetArray[groupPosition].getTargetId();
        String subject = targetArray[groupPosition].getSubjects().get(childPosition).getSubjectName();
        String userId = SharedPreferencesHandler.getStoredUserId(context);
        User user = Utility.databaseHandler.getUserById(SharedPreferencesHandler.getStoredUserId(context));

        showMesage(target, subject, user);
        Log.i(Utility.LOG_TAG, "->to call generate test paper: target: " + target + ", subject: " + subject + ", user: " + userId);
        return true;
    }

    private void showMesage(final String target, final String subject, final User user) {
        int numberOfTicketsAvailable = getNumberOfAvailableTickets(user);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.ic_action_import_export);
        if (numberOfTicketsAvailable == 0) {
            builder.setTitle("Sorry");
            builder.setMessage("There are no tickets in your account to take this test. You can buy tickets in the tickets sections");
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            builder.setPositiveButton("Buy Tickets", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: move to Tickets Activity
                    alertDialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
        } else {
            builder.setTitle("Confirm");
            builder.setMessage("You wish to take a practce test for the subject " + subject + " in the category " + target + ". This test requires 1 ticket costing \u20B9 10. You have " + numberOfTicketsAvailable + " tickets left Are you sure you want to take this test?");
            builder.setCancelable(false);
            final AlertDialog alertDialog = builder.create();
            builder.setPositiveButton("Take test", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    generateNewTestpaper(target, subject, user);
                    alertDialog.dismiss();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
        }

        builder.show();
    }

    private void generateNewTestpaper(String target, String subject, User user) {
        String url = Utility.API_GENERATE_TEST_PAPER.replace(Utility.USER_ID_PH, user.getUserId()).replace(Utility.TARGET_PH, target).replace(Utility.SUBJECT_PH, subject);
        TestPaper testPaper = InternetHandler.get(url, TestPaper.class);
        saveTestpaperAndUpdateUser(testPaper);
    }

    private void saveTestpaperAndUpdateUser(final TestPaper testPaper) {
        Utility.databaseHandler.saveTestPaper(testPaper);
        String url = Utility.API_GET_USER_BY_ID.replace(Utility.USER_ID_PH, SharedPreferencesHandler.getStoredUserId(context));
        User user = InternetHandler.get(url, User.class);
        Utility.databaseHandler.saveUser(user);
        startTest(testPaper);
    }

    private void startTest(TestPaper testPaper) {
        Log.i(Utility.LOG_TAG, ">>startTest");
        Intent intent = new Intent(this, TestActivity.class);
        intent.putExtra("testId", testPaper.getTestId());
        startActivity(intent);
        finish();
    }

    private int getNumberOfAvailableTickets(User user) {
        int count = 0;
        for (Ticket ticket : user.getTickets()) {
            if (ticket.isTicketAvailable())
                count++;
        }
        return count;
    }


}
