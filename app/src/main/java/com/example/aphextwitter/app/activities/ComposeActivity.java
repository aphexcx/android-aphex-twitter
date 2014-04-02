package com.example.aphextwitter.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aphextwitter.app.AphexTwitterApp;
import com.example.aphextwitter.app.R;
import com.example.aphextwitter.app.models.Tweet;
import com.example.aphextwitter.app.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;


public class ComposeActivity extends Activity {
    private ImageView imageView;
    private EditText etTweetText;
    private TextView tvCharCount;

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

        etTweetText = (EditText) findViewById(R.id.etTweetText);

        etTweetText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCount.setText(String.valueOf(140 - s.length()));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.compose, menu);
        tvCharCount = (TextView) menu.findItem(R.id.layout_char_count).getActionView().findViewById(R.id.tvCharCount);
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

    public void onTweetSubmit(MenuItem item) {
        String status = etTweetText.getText().toString();
        if (status.length() > 140) {
            Toast.makeText(this, "Your tweet is too long.", Toast.LENGTH_SHORT).show();
        } else {
            AphexTwitterApp.getRestClient().postUpdate(status, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonTweet) {
                    Tweet posted_tweet = Tweet.fromJson(jsonTweet);
                    Intent data = new Intent();
                    // Pass relevant data back as a result
                    data.putExtra("tweet", posted_tweet);
                    // Activity finished ok, return the data
                    setResult(RESULT_OK, data); // set result code and bundle data for response
                    finish();
                }

                @Override
                public void onFailure(Throwable throwable, JSONObject jsonObject) {
                    super.onFailure(throwable, jsonObject);
                    String error;
                    try {
                        error = jsonObject.getJSONArray("errors").getJSONObject(0).getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error = "Error posting tweet; also, couldn't extract error code";
                    }
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
