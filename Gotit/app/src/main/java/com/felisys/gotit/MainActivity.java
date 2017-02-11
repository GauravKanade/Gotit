package com.felisys.gotit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.felisys.gotit.database.DatabaseHandler;
import com.felisys.gotit.entity.TestPaper;
import com.felisys.gotit.entity.User;
import com.felisys.gotit.questions.ProfileActivity;
import com.felisys.gotit.questions.TestActivity;
import com.felisys.gotit.users.LoginActivity;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.ObjectMapperUtil;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.SharedPreferencesHandler;
import com.felisys.gotit.util.Utility;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        init();
        //startActivity(new Intent(this, TemporaryActivity.class));
    }

    private void init() {
        context = this;
        if (Utility.databaseHandler == null)
            Utility.databaseHandler = new DatabaseHandler(context);
        if (SharedPreferencesHandler.isUserLoggedIn(this)) {
            Log.i(Utility.LOG_TAG, ">>MainActivity.init() - User already logged in. Proceeding to ProfileActivity");
            getUserDetailsAndSaveInPhoneDB(SharedPreferencesHandler.getStoredUserId(context));
            return;
        }
        Log.i(Utility.LOG_TAG, ">>MainActivity.init() - User not logged in. Proceeding to LoginActivity");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void getUserDetailsAndSaveInPhoneDB(String userId) {
        Log.i(Utility.LOG_TAG, ">>MainAcvity.getUserDetailsAndSaveInPhoneDB()");
        String url = Utility.API_GET_USER_BY_ID.replace(Utility.USER_ID_PH, userId);
        User user = InternetHandler.get(url, User.class);
        Utility.databaseHandler.saveUser(user);
        SharedPreferencesHandler.saveUserInfo(context, user.getUserId(), user.getUserName(), user.getEmailId());
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        finish();
    }
}
