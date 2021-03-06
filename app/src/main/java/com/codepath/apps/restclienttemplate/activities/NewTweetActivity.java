package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.Utils;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class NewTweetActivity extends AppCompatActivity {

    private TwitterClient client;
    private User user;
    private TextView tvCharacterCount;
    private EditText etTweetBody;

    private int bodyCount;

    private static int maxBodyCount = 140;

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int result = bodyCount - s.length();
            tvCharacterCount.setText(String.valueOf(result));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);

        client = TwitterApp.getRestClient(this);

        bodyCount = maxBodyCount;

        etTweetBody = findViewById(R.id.etTweetBody);
        tvCharacterCount = findViewById(R.id.tvCharcterCount);

        tvCharacterCount.setText(String.valueOf(bodyCount));
        etTweetBody.addTextChangedListener(mTextEditorWatcher);

        user = (User) getIntent().getSerializableExtra("user");

        setupViews();

        Utils.handleNetworkAvailability(this);
    }

    private void setupViews() {
        TextView tvUserId = findViewById(R.id.tvUserId);
        TextView tvUserName = findViewById(R.id.tvUserName);
        ImageView ivProfileImage = findViewById(R.id.ivProfileImage);

        tvUserId.setText(user.getScreenName());
        tvUserName.setText(user.getName());

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

        Glide.with(NewTweetActivity.this)
                .load(user.getProfileImageUrl())
                .apply(requestOptions)
                .into(ivProfileImage);
    }

    public void onClickTweetBtn(View view) {
        EditText etTweetBody = findViewById(R.id.etTweetBody);
        final String body = etTweetBody.getText().toString();
        client.postTweet(body, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());

                Intent i = new Intent();
                i.putExtra("body", body);
                setResult(RESULT_OK, i);
                finish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
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

    public void onClickCloseBtn(View view) {
        finish();
    }
}
