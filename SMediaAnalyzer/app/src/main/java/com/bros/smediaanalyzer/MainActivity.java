package com.bros.smediaanalyzer;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import twitter4j.*;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity {

    static TextView outputText;
    static EditText inputText;
    static Button searchButton;
    static ArrayList<Tweet> tweetsList;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = (TextView) findViewById(R.id.OutputText);

        inputText = (EditText) findViewById(R.id.InputText);

        searchButton = (Button) findViewById(R.id.SearchButton);


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Query query = new Query((inputText.getText()).toString());
                    query.setCount(100);
                    QueryResult result = (twitterConnection()).search(query);
                    List<Status> tweets = result.getTweets();

                    for (Status tweet : tweets) {
                        tweetsList.add(new Tweet(tweet.getText(), 0));
                        String a = outputText.getText() + "\n@" + tweet.getUser().getScreenName() + ":" + tweet.getText();
                        outputText.setText(a);
                    }

                }

                catch (TwitterException e) {
                    
                }
            }
        });



    }

    public static Twitter twitterConnection () {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("paWSUKBcxTSsRcWRmuIBCmBKv")
                .setOAuthConsumerSecret("2LEK7SAgJ83GWFVYeCCI4wBSYPk75iTQT8ppGb1M9CDbu4omQv")
                .setOAuthAccessToken("826394362791784448-73u1EnNWhF0vhRLNMEajbkAuQ1hja6f")
                .setOAuthAccessTokenSecret("WCbwH3Bwc3UKrt8E8ihqe7r73Y3f6rHGWVDHnGffKh03h");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        return twitter;
    }
}
