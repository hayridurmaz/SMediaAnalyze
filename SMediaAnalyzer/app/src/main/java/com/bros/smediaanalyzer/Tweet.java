package com.bros.smediaanalyzer;

import java.util.ArrayList;
import java.util.List;
import com.bros.*;

/**
 * Created by Batuhan on 25.11.2017.
 */

public class Tweet implements Comparable<Tweet>{
    String comment;
    double polarity;
    public ArrayList<Topic> topics;



    public Tweet (String comment1) throws Exception {
        comment = comment1;
        polarity=0;
        System.out.println("Tweet comment "+comment1);


        topics= new ArrayList<Topic>();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(this);
        setPolarity();
        System.out.println("Tweet polarity: "+polarity);

        System.out.println("topicslength: "+topics.size());
    }

    public void setPolarity () {
        double sum=0;
        for (int i=0;i<this.topics.size();i++){
            sum+=topics.get(i).polarity;
        }
        if(topics==null || topics.size()==0){
            polarity =0;
        }
        polarity=sum/topics.size();
    }


    public int compareTo (Tweet tweet1) {
        return (int) ((this.polarity * 1000) - (tweet1.polarity * 1000));
    }
}
