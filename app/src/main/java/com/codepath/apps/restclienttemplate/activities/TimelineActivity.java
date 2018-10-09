package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.fragments.TweetListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private TweetListFragment fragmentTweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        fragmentTweetsList = (TweetListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);

        client = TwitterApp.getRestClient(this);
        fetchFirstTweets();

        Toolbar toolbar = findViewById(R.id.tool_bar);
        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(toolbar);
    }

    // Network

    public void fetchFirstTweets() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                fragmentTweetsList.addFirstItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }

    public void fetchMoreTweets(int page){
        if (fragmentTweetsList.tweets.size() == 0)
            return;

        Tweet lastTweet = fragmentTweetsList.tweets.get(fragmentTweetsList.tweets.size()-1);
        long max_id = lastTweet.getUid();

        client.getHomeTimeline(max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response){
                fragmentTweetsList.addMoreItems(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse){
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    // Tool bar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_tweet) {
            launchArticleActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Navigation

    private final int NEW_TWEET_ACTIVITY_REQUEST_CODE = 20;

    private void launchArticleActivity() {
        Intent i = new Intent(getApplicationContext(), NewTweetActivity.class);
        startActivityForResult(i, NEW_TWEET_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == NEW_TWEET_ACTIVITY_REQUEST_CODE) {
            String body = data.getStringExtra("body");
            User user = (User) data.getSerializableExtra("user");

            Tweet tweet = new Tweet(user, body);
            fragmentTweetsList.tweets.add(0, tweet);
            fragmentTweetsList.tweetAdapter.notifyItemInserted(0);
            fragmentTweetsList.rvTweets.smoothScrollToPosition(0);
        }
    }
}
