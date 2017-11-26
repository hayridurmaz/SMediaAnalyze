package com.bros.smediaanalyzer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
    static HashMap<String,Integer> topicCountHashMap;
    static HashMap<String,Double> topicPolarityHashMap;
    static HashMap<String, Double> topTopicsHashMap;
    Double polarityOfSearch;
    String output;
    Query query;
    static ImageView downImage, upImage;

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

        downImage = (ImageView) findViewById(R.id.DownImage);
        upImage = (ImageView) findViewById(R.id.UpImage);

        downImage.setVisibility(View.INVISIBLE);
        upImage.setVisibility(View.INVISIBLE);

        topicCountHashMap= new HashMap<>();
        topicPolarityHashMap= new HashMap<>();
        
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    downImage.setVisibility(View.INVISIBLE);
                    upImage.setVisibility(View.INVISIBLE);

                    outputText.setText("Yükleniyor...");
                    tweetsList = new ArrayList<Tweet>();
                    query = new Query((inputText.getText()).toString());
                    query.setCount(100);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                           try{
                                QueryResult result = twitterConnection().search(query);
                                List<Status> tweets = result.getTweets();
                                int count=0;
                                System.out.println("Tweets size "+tweets.size());
                                for (Status tweet : tweets) {
                                    if(!tweet.getLang().equals("en") || tweet.isRetweet()){
                                        continue;
                                    }
                                    System.out.println("Tweet text:"+tweet.getText());
                                    Tweet tweet1 = new Tweet(tweet.getText());
                                    //tweet1.setPolarity();
                                    /*for (Topic topic : tweet1.topics){
                                        setTopicHashMap(topic);
                                    }*/
                                    tweetsList.add(tweet1);
                                    if(tweetsList.size()>100){
                                        break;
                                    }

                                }
                                for (int i=0; i<tweetsList.size();i++){
                                    //SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(tweetsList.get(i));
                                    tweetsList.get(i).setKutup();
                                    for (Topic topic : tweetsList.get(i).topics){
                                        setTopicHashMap(topic);
                                    }
                                }
                                System.out.println("tweetList size: "+tweetsList.size());
                                polarityOfSearch=PolarityOfSearch();
                                System.out.println("POLARITY"+polarityOfSearch);
                                findTopTenTopics();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        //String a = outputText.getText() + "\n@" + tweet.getUser().getScreenName() + ":" + tweet.getText();
                                        NumberFormat formatter = new DecimalFormat("#0.00");

                                        output=formatter.format(((polarityOfSearch*100)+100)/2)+"% of  ";

                                        if (polarityOfSearch < 0) {
                                            downImage.setVisibility(View.VISIBLE);
                                        }

                                        if (polarityOfSearch >= 0) {
                                            upImage.setVisibility(View.VISIBLE);
                                        }

                                        output+=" comments about "+ inputText.getText().toString() +" are positive.";

                                        /*
                                        for(String currentKey : topTopicsHashMap.keySet()){

                                            double currentPolarity = topTopicsHashMap.get(currentKey);
                                            output+="People generally ";
                                            if(currentPolarity<0){
                                                output+=" do not ";
                                            }
                                            output+=" like "+currentKey+" about this product at the rate of "+currentPolarity+"\n";
                                        }
                                        */
                                        outputText.setText(output);
                                    }
                                });
                            }
                            catch (Exception e){

                            }


                        }
                    }).start();




                }
                catch (Exception e) {

                }
            }
        });



    }
    public static void findTopTenTopics(){
        topTopicsHashMap = new HashMap<String, Double>();
        for(String currentKey : topicCountHashMap.keySet()){
            if(topicCountHashMap.get(currentKey) > 10 ){
                topTopicsHashMap.put(currentKey,topicPolarityHashMap.get(currentKey));
            }
        }
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
    public static double PolarityOfSearch(){ //hashmap-her topic için count, count>10 tut.
        double sum=0;
        int count=0;
        for(int i=0;i<tweetsList.size();i++){
            if(!Double.isNaN(tweetsList.get(i).polarity)){
                sum+=tweetsList.get(i).polarity;
                count++;
            }

        }
        if(tweetsList==null || tweetsList.size()==0){
            return 0;
        }
        System.out.println("*******sum "+sum + " tweetslist size"+tweetsList.size());
        return sum/count;
    }
    public static void setTopicHashMap(Topic topic){
        if(topicCountHashMap.containsKey(topic.topic)){
            int temp = topicCountHashMap.get(topic.topic);
            topicCountHashMap.put(topic.topic,temp+1);
            double pol = topic.polarity;
            double oldPol = topicPolarityHashMap.get(topic.topic);
            topicPolarityHashMap.put(topic.topic,pol+oldPol);
        }
        else{
            topicCountHashMap.put(topic.topic,1);
            topicPolarityHashMap.put(topic.topic,topic.polarity);
        }
    }
}
