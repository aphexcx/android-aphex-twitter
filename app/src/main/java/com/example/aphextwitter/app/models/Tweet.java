package com.example.aphextwitter.app.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Tweet implements Serializable {
    private String body;
    private long uid;
    private String created_at;
    private boolean favorited;
    private boolean retweeted;
    private User user;
    private JSONArray urls;
    private JSONArray media;

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.created_at = jsonObject.getString("created_at");
            tweet.favorited = jsonObject.getBoolean("favorited");
            tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.urls = jsonObject.getJSONObject("entities").getJSONArray("urls");
            try {
                tweet.media = jsonObject.getJSONObject("entities").getJSONArray("media");
            } catch (JSONException e) {
                tweet.media = new JSONArray();
            }
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject tweetJson;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson);
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getId() {
        return uid;
    }

    public String getCreated_at() {
        return created_at;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public JSONArray getUrls() {
        return urls;
    }

    public JSONArray getMedia() {
        return media;
    }
}