package com.example.aphextwitter.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aphextwitter.app.models.Tweet;
import com.example.aphextwitter.app.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ComposeActivity extends Activity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        User current_user = (User) getIntent().getSerializableExtra("current_user");

        imageView = (ImageView) findViewById(R.id.ivProfile);
        ImageLoader.getInstance().displayImage(current_user.getProfileImageUrl(), imageView);

        TextView nameView = (TextView) findViewById(R.id.tvName);
        String formattedName = "<b>" + current_user.getName() + "</b>" +
                " <small><font color='#777777'>@" + current_user.getScreenName() + "</font></small>";
        nameView.setText(Html.fromHtml(formattedName));
    }

    private User getCurrentUser() {
        // I can't figure out how to make a final outside variable that onSuccess can modify, fuck it!
        final User[] user = new User[1];
        AphexTwitterApp.getRestClient().getCurrentCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonUser) {
                user[0] = User.fromJson(jsonUser);
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
                String error = null;
                try {
                    error = jsonObject.getJSONArray("errors").getJSONObject(0).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = "Error fetching current user; also, couldn't extract error code";
                }
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                user[0] = null;
            }
        });
        return user[0];
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
