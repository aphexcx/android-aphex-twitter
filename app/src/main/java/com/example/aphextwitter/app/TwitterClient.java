package com.example.aphextwitter.app;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "d1l7pWk3r3rf5GVooY6e5KA4I";       // Change this
    public static final String REST_CONSUMER_SECRET = "4O8MglMDOvQnCNrX8UIcPROEIs5EyXmadRjwSBU6v0fIR9PwpO"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://aphextwitterapp"; // Change this (here and in manifest)

    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }

    // DEFINE METHODS for different API endpoints here
//    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
//        String url = getApiUrl("statuses/home_timeline.json");
//        RequestParams params = new RequestParams();
//        params.put("include_entities", "true");
//        client.get(url, params, handler);
//    }

    public void getHomeTimeline(long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("include_entities", "true");
        if (since_id > 0) {
            params.put("since_id", String.valueOf(since_id));
        }
        if (max_id > -1) {
            params.put("max_id", String.valueOf(max_id));
        }
        client.get(url, params, handler);
    }

    public void getUserTimeline(long user_id, long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("include_entities", "true");
        if (user_id > -1) {
            params.put("user_id", String.valueOf(user_id));
        }
        if (since_id > 0) {
            params.put("since_id", String.valueOf(since_id));
        }
        if (max_id > -1) {
            params.put("max_id", String.valueOf(max_id));
        }
        client.get(url, params, handler);
    }

    public void getMentions(long since_id, long max_id, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("include_entities", "true");
        if (since_id > 0) {
            params.put("since_id", String.valueOf(since_id));
        }
        if (max_id > -1) {
            params.put("max_id", String.valueOf(max_id));
        }
        client.get(url, params, handler);
    }

    public void getCurrentCredentials(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("account/verify_credentials.json");
        client.get(url, null, handler);
    }

    public void postUpdate(String status, AsyncHttpResponseHandler handler) {
        String url = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", status);
        client.post(url, params, handler);
    }

    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
}