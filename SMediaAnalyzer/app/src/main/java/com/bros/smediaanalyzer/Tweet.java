package com.bros.smediaanalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Batuhan on 25.11.2017.
 */

public class Tweet implements Comparable<Tweet>{
    String comment;
    double polarity;
    ArrayList<Sentence> sentences;


    public Tweet (String comment1, double polarity1) {
        comment = comment1;
        polarity = polarity1;
        sentences= new ArrayList<Sentence>();
    }

    public void setPolarity (double polarity1) {

    }

    public int compareTo (Tweet tweet1) {
        return (int) ((this.polarity * 1000) - (tweet1.polarity * 1000));
    }
}
