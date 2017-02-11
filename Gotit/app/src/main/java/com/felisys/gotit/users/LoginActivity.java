
package com.felisys.gotit.users;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.felisys.gotit.R;
import com.felisys.gotit.database.DatabaseHandler;
import com.felisys.gotit.entity.User;
import com.felisys.gotit.questions.ProfileActivity;
import com.felisys.gotit.util.Constants;
import com.felisys.gotit.util.InternetHandler;
import com.felisys.gotit.util.ObjectMapperUtil;
import com.felisys.gotit.util.ReplaceFont;
import com.felisys.gotit.util.SharedPreferencesHandler;
import com.felisys.gotit.util.Utility;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, Constants {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private final int MY_PERMISSIONS_REQUEST_GET_ACCOUNT = 50;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ReplaceFont.replaceDefaultFont(this, "DEFAULT", "Segoe_UI.ttf");
        Log.i(Utility.LOG_TAG, ">>LoginActivity.onCreate()");
        init();

    }

    private void init() {
        context = this;
        if (Utility.databaseHandler == null)
            Utility.databaseHandler = new DatabaseHandler(context);
        findViewById(R.id.bGoogleSignIn).setOnClickListener(this);
        initGoogleSignIn();

    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_REQUEST_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(Utility.LOG_TAG, ">>Login failed: " + connectionResult.getErrorMessage());
        Toast.makeText(context, "Login Failed. Please check the account and try again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Log.i(Utility.LOG_TAG, ">>LoginActivity.onClick()");
        switch (v.getId()) {
            case R.id.bGoogleSignIn:
                //signIn();
                checkIfUserExistsInServer("gkanade4@gmail.com", "Gautam Kanade");
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(Utility.LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String photoURL = acct.getPhotoUrl().getEncodedPath();
            checkIfUserExists(personEmail, personName, photoURL);
        } else {
            Log.e(Utility.LOG_TAG, ">>LoginActivity.handleSignInResult() - SignIn Failed" + GoogleSignInStatusCodes.getStatusCodeString(result.getStatus().getStatusCode()));
        }
    }

    private void checkIfUserExists(String personEmail, String personName, String photoURL) {
        //TODO: check if the user exists in the server. If not, create the user and assign the userId
        checkIfUserExistsInServer(personEmail, personName);
        SharedPreferencesHandler.saveUserInfo(context, "TestUser", personName, personEmail);
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkIfUserExistsInServer(final String email, final String name) {
        Log.i(Utility.LOG_TAG, ">>LoginActivity.checkIfUserExistsInServer()");
        final String url = Utility.API_CHECK_LOGIN.replace(Utility.EMAIL_PH, email);
        Map<String, Object> response = InternetHandler.get(url, Map.class);
        try {
            if (Boolean.parseBoolean(response.get("success").toString())) { // user already exists in server
                Log.i(Utility.LOG_TAG, "-> user with email " + email + " exists in the server. userMap: " + response.get("user").toString());
                User user = ObjectMapperUtil.getConfiguredObjectMapper().readValue(response.get("user").toString(), User.class);
                saveUserInfoAndProceedToHomePage(user);
            } else { //user is not present in the server -> call create user API
                Log.i(Utility.LOG_TAG, "-> user with the emailId " + email + " does no exist in the server. So calling createNewUserInServer");
                createNewUserInServer(name, email);
            }
        } catch (IOException e) {
            Log.e(Utility.LOG_TAG, "IO EXCEPTION in handling response from " + url, e);
        }
    }


    private void createNewUserInServer(String name, String email) {
        Log.i(Utility.LOG_TAG, ">>LoginActivity.createNewUserInServer()");
        User user = new User();
        user.setEmailId(email);
        user.setUserName(name);
        Map<String, Object> response = InternetHandler.post(Utility.API_SAVE_USER, user, Map.class);
        String userId = response.get("userId").toString();
        getUserDetailsAndSaveInPhoneDB(userId);
    }

    private void getUserDetailsAndSaveInPhoneDB(String userId) {
        Log.i(Utility.LOG_TAG, ">>LoginActivity.getUserDetailsAndSaveInPhoneDB()");
        String url = Utility.API_GET_USER_BY_ID.replace(Utility.USER_ID_PH, userId);
        User user = InternetHandler.get(url, User.class);
        saveUserInfoAndProceedToHomePage(user);
    }

    private void saveUserInfoAndProceedToHomePage(User user) {
        Utility.databaseHandler.saveUser(user);
        SharedPreferencesHandler.saveUserInfo(context, user.getUserId(), user.getUserName(), user.getEmailId());
        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
        finish();
    }
}
