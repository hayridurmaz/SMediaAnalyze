package com.bros.smediaanalyzer;

/**
 * Created by Hayri Durmaz on 26.11.2017.
 */

public class Sentence {
    String value, topic;
    double polarity;

    public Sentence(String value, String topic, double polarity) {
        this.value = value;
        this.topic = topic;
        this.polarity = polarity;
    }

}
