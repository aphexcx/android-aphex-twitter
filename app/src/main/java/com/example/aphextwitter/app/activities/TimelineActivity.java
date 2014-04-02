package com.example.aphextwitter.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aphextwitter.app.AphexTwitterApp;
import com.example.aphextwitter.app.EndlessScrollListener;
import com.example.aphextwitter.app.R;
import com.example.aphextwitter.app.TweetsAdapter;
import com.example.aphextwitter.app.models.Tweet;
import com.example.aphextwitter.app.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class TimelineActivity extends Activity implements OnRefreshListener {
    private static final int TWEET_REQUEST_CODE = 100;
    private ListView lvTweets;
    private TweetsAdapter twAdapter;
    private long lowest_tweet_id = 0;
    private PullToRefreshLayout mPullToRefreshLayout;
    private long highest_tweet_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);

        // Now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(this)
                // Mark All Children as pullable
                .allChildrenArePullable()
                        // Set a OnRefreshListener
                .listener(this)
                        // Finally commit the setup to our PullToRefreshLayout
                .setup(mPullToRefreshLayout);

        lvTweets = (ListView) findViewById(R.id.lvTweets);

        twAdapter = new TweetsAdapter(getBaseContext(), new ArrayList<Tweet>());
        lvTweets.setAdapter(twAdapter);

        loadTweets(0, -1);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadTweets(0, lowest_tweet_id - 1);
            }
        });
    }

    private void loadTweets(final long since_id, long tweet_id) {
        AphexTwitterApp.getRestClient().getHomeTimeline(since_id, tweet_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
                if (!tweets.isEmpty()) {
                    Tweet last_tweet = tweets.get(tweets.size() - 1);
                    lowest_tweet_id = last_tweet.getId();
                    if (since_id > 0) {
                        //these are new tweets, prepend them
                        for (int i = 0; i < tweets.size(); i++) {
                            twAdapter.insert(tweets.get(i), i);
                        }
                    } else {
                        twAdapter.addAll(tweets);
                    }
                }
                // Notify PullToRefreshLayout that the refresh has finished
                mPullToRefreshLayout.setRefreshComplete();
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
                String error;
                try {
                    error = jsonObject.getJSONArray("errors").getJSONObject(0).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = "Error fetching HomeTimeline; also, couldn't extract error code";
                }
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        });
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
            twAdapter.insert(posted_tweet, 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, "Tweet posted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        highest_tweet_id = twAdapter.getItem(0).getId();
        loadTweets(highest_tweet_id, -1);
    }
}
