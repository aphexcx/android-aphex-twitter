package com.example.aphextwitter.app.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.aphextwitter.app.AphexTwitterApp;
import com.example.aphextwitter.app.R;
import com.example.aphextwitter.app.fragments.HomeTimelineFragment;
import com.example.aphextwitter.app.fragments.MentionsFragment;
import com.example.aphextwitter.app.models.Tweet;
import com.example.aphextwitter.app.models.User;

//import android.support.v4.app.FragmentActivity;

public class TimelineActivity extends Activity implements ActionBar.TabListener {
    private static final int TWEET_REQUEST_CODE = 100;
    private HomeTimelineFragment homeTimelineFragment;
    private MentionsFragment mentionsFragment;

//    private TweetsListFragment fragmentTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
//        fragmentTweets = (TweetsListFragment) getFragmentManager().findFragmentById(R.id.fragment_tweets_list);
        homeTimelineFragment = new HomeTimelineFragment();
        mentionsFragment = new MentionsFragment();
        setupNavigationTabs();
    }

    private void setupNavigationTabs() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        Tab tabHome = actionBar.newTab().setText("Home")
                .setTag("HomeTimelineFragment").setIcon(R.drawable.ic_home)
                .setTabListener(this);
        Tab tabMentions = actionBar.newTab().setText("Mentions")
                .setTag("MentionsFragment").setIcon(R.drawable.ic_mentions)
                .setTabListener(this);


        actionBar.addTab(tabHome);
        actionBar.addTab(tabMentions);
        actionBar.selectTab(tabHome);
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
            HomeTimelineFragment fragmentHome = (HomeTimelineFragment) getFragmentManager().findFragmentByTag("HomeTimelineFragment");
            fragmentHome.getAdapter().insert(posted_tweet, 0);
            // Toast the name to display temporarily on screen
            Toast.makeText(this, "Tweet posted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction fts = manager.beginTransaction();
        if (tab.getTag() == "HomeTimelineFragment") {
            fts.replace(R.id.frame_container, homeTimelineFragment, homeTimelineFragment.getMyName());
        } else {
            fts.replace(R.id.frame_container, mentionsFragment, mentionsFragment.getMyName());
        }
        fts.commit();
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }

    public void onProfileView(MenuItem item) {
        Intent i = new Intent(this, ProfileActivity.class);
        User user = AphexTwitterApp.getCurrentUser();
        i.putExtra("user", user);
        startActivity(i);
    }
}
