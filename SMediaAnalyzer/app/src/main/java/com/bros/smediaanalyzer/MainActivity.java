package com.bros.smediaanalyzer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t1;
        t1 = (TextView) findViewById(R.id.ttt);

        try {
            // The factory instance is re-useable and thread safe.
            Twitter twitter = TwitterFactory.getSingleton();
            Query query = new Query("source:twitter4j yusukey");

            QueryResult result = twitter.search(query);
            /*
            for (Status status : result.getTweets()) {
                t1.setText(t1.getText() + "@" + status.getUser().getScreenName() + ":" + status.getText());
            }
            */
        }

        catch (TwitterException e) {
            e.printStackTrace();
        }

    }
}
