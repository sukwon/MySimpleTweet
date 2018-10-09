package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.TweetAdapter;
import com.codepath.apps.restclienttemplate.listener.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TweetListFragment extends Fragment {

    protected TweetAdapter tweetAdapter;
    protected ArrayList<Tweet> tweets;
    protected RecyclerView rvTweets;

    protected EndlessRecyclerViewScrollListener scrollListener;
    protected SwipeRefreshLayout swipeContainer;
    protected LinearLayoutManager linearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragments_tweets_list, container, false);

        rvTweets = v.findViewById(R.id.rvTweet);
        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(tweets);
        rvTweets.setAdapter(tweetAdapter);

        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchMoreTweets(page);
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                linearLayoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);

        setupPullToRefresh(v);

        return v;
    }

    public void addFirstItems(JSONArray response) {
        tweets.clear();
        tweetAdapter.notifyDataSetChanged();

        for (int i = 0; i <  response.length(); i++) {
            Tweet tweet;
            try {
                tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        swipeContainer.setRefreshing(false);
    }

    public void addMoreItems(JSONArray response) {

        for (int i = 0; i <  response.length(); i++) {
            Tweet tweet;
            try {
                tweet = Tweet.fromJSON(response.getJSONObject(i));
                tweets.add(tweet);
                tweetAdapter.notifyItemInserted(tweets.size() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void insertTweet(Tweet tweet, int index) {
        tweets.add(index, tweet);
        tweetAdapter.notifyItemInserted(index);
        rvTweets.smoothScrollToPosition(index);
    }

    // Pull to Refresh

    private void setupPullToRefresh(View view) {
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchFirstTweets();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    protected void fetchFirstTweets() {}
    protected void fetchMoreTweets(int page) {}
}
