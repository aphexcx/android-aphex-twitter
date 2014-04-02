package com.example.aphextwitter.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.example.aphextwitter.app.AphexTwitterApp;
import com.example.aphextwitter.app.R;
import com.example.aphextwitter.app.TwitterClient;
import com.example.aphextwitter.app.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {
        AphexTwitterApp.getRestClient().getCurrentCredentials(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int arg0, JSONObject jsonObject) {
                AphexTwitterApp.setCurrentUser(User.fromJson(jsonObject));
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
                String error;
                try {
                    error = jsonObject.getJSONArray("errors").getJSONObject(0).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = "Error fetching current user; also, couldn't extract error code";
                }
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }

        });

        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
    }

    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToRest(View view) {
        getClient().connect();
    }

}
