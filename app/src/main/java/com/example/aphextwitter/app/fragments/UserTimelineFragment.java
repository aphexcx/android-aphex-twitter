package com.example.aphextwitter.app.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.aphextwitter.app.AphexTwitterApp;
import com.example.aphextwitter.app.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserTimelineFragment extends TweetsListFragment {

    private long user_id;

    public UserTimelineFragment() {
        // Required empty public constructor
        user_id = -1;
    }

    public UserTimelineFragment(long _user_id) {
        // Required empty public constructor
        user_id = _user_id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home_timeline, container, false);
//    }

    @Override
    protected void loadTweets(final long since_id, long tweet_id) {
        AphexTwitterApp.getRestClient().getUserTimeline(user_id, since_id, tweet_id, new JsonHttpResponseHandler() {
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
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
