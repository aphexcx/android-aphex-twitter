package com.example.aphextwitter.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aphextwitter.app.models.Tweet;
import com.example.aphextwitter.app.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends Activity {
    private ListView lvTweets;
    private TweetsAdapter twAdapter;
    private long lowest_tweet_id;
    private User current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        lvTweets = (ListView) findViewById(R.id.lvTweets);

        AphexTwitterApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
                twAdapter = new TweetsAdapter(getBaseContext(), tweets);
                lvTweets.setAdapter(twAdapter);
                Tweet last_tweet = tweets.get(tweets.size() - 1);
                lowest_tweet_id = last_tweet.getId();
            }

            @Override
            public void onFailure(Throwable throwable, JSONArray array) {
                super.onFailure(throwable, array);
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
                String error = null;
                try {
                    error = jsonObject.getJSONArray("errors").getJSONObject(0).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                    error = "Error fetching HomeTimeline; also, couldn't extract error code";
                }
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
//                Tweet last_tweet = (Tweet) twAdapter.getItem(totalItemsCount - 1);
                customLoadMoreDataFromApi(lowest_tweet_id - 1);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });

        // get current user
        current_user = null;
        AphexTwitterApp.getRestClient().getCurrentCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject jsonUser) {
                current_user = User.fromJson(jsonUser);
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
                current_user = null;
            }
        });
    }

    private void customLoadMoreDataFromApi(final long tweet_id) {
        AphexTwitterApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray jsonTweets) {
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
//                twAdapter.remove();
//                twAdapter.notifyDataSetChanged();
//                tweets.remove(0);
                twAdapter.addAll(tweets);
            }

            @Override
            public void onFailure(Throwable throwable, JSONArray array) {
                super.onFailure(throwable, array);
            }

            @Override
            public void onFailure(Throwable throwable, JSONObject jsonObject) {
                super.onFailure(throwable, jsonObject);
            }
        }, tweet_id);

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
        Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
        i.putExtra("current_user", current_user);
        startActivity(i);
    }
}
