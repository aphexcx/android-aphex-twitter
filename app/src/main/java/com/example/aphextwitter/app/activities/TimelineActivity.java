package com.example.aphextwitter.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aphextwitter.app.AphexTwitterApp;
import com.example.aphextwitter.app.R;
import com.example.aphextwitter.app.TweetsAdapter;
import com.example.aphextwitter.app.models.Tweet;
import com.example.aphextwitter.app.models.User;

public class TimelineActivity extends FragmentActivity {
    private static final int TWEET_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
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

    public void onCompose(MenuItem item) {
        User current_user = AphexTwitterApp.getCurrentUser();
        Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
        i.putExtra("current_user", current_user);
        startActivityForResult(i, TWEET_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == TWEET_REQUEST_CODE) {
            // Extract name value from result extras
            Tweet posted_tweet = (Tweet) data.getExtras().getSerializable("tweet");
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_tweets_list);
            ListView lvTweets = (ListView) frag.getView().findViewById(R.id.lvTweets);
            TweetsAdapter twAdapter = (TweetsAdapter) lvTweets.getAdapter();
            twAdapter.insert(posted_tweet, 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, "Tweet posted", Toast.LENGTH_SHORT).show();
        }
    }
}
