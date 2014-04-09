package com.example.aphextwitter.app.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable{
    private String name;
    private long uid;
    private String screenName;
    private String tagline;
    private String profileImageUrl;
    private String profileBgImageUrl;
    private int numTweets;
    private int followersCount;
    private int friendsCount;

    public static User fromJson(JSONObject json) {
        User u = new User();
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.tagline = json.getString("description");
            u.profileImageUrl = json.getString("profile_image_url");
            u.profileBgImageUrl = json.getString("profile_background_image_url");
            u.numTweets = json.getInt("statuses_count");
            u.followersCount = json.getInt("followers_count");
            u.friendsCount = json.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl.replaceAll("_normal.", "_bigger.");
    }

    public String getProfileBackgroundImageUrl() {
        return profileBgImageUrl;
    }

    public int getNumTweets() {
        return numTweets;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }


    public String getTagline() {
        return tagline;
    }
}