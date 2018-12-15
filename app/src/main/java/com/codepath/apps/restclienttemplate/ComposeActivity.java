package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.github.scribejava.apis.TwitterApi;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {
    private EditText etCompose;
    private Button btnTweet;
    public static final int MAX_TWEET_LENGTH = 140;
    private TwitterClient twitterClient;
    public static final String NEW_TWEET = "NEW_TWEET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compse);

        etCompose = findViewById(R.id.et_Compose);
        btnTweet = findViewById(R.id.btnTweet);
        twitterClient = TwitterApp.getRestClient(this);
        //set click listener on button
        btnTweet.setOnClickListener(v -> {
            String tweetContent = etCompose.getText().toString();
            //TODO: ERROR HANDLING
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "This tweet is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tweetContent.length() > MAX_TWEET_LENGTH) {
                Toast.makeText(this, "Too long mate!!!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Make API call to Twitter to publish the content in edit text
            twitterClient.composeTweet(tweetContent, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("TwitterClient", "onSuccess: Posted Tweet " + response.toString());
                    try {
                        Tweet newTweet = Tweet.fromJson(response);
                        if (newTweet != null) {
                            Intent data = new Intent();
                            data.putExtra(NEW_TWEET, Parcels.wrap(newTweet));
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e("TwitterClient", "Fail to convert from Json to tweet object");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("TwitterClient", "onFailure: Posted Tweet " + responseString);

                }
            });
        });

    }
}
