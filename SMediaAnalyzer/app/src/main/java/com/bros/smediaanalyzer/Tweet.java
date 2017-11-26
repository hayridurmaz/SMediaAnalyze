package com.bros.smediaanalyzer;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        /*SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(this);
        setPolarity();*/
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

    public void setKutup() throws FileNotFoundException {
        //Tweet objesinin Kutup değerini bulan method.
        try {


            double sum = 0;
            int count=0;

            String[] splited = comment.split(" ");//yorumu kelimelere ayırmak

            for (int i = 0; i < splited.length; i++) {//her kelimeyi arıyor.
                //System.err.println(i);

                BufferedReader br = new BufferedReader(new FileReader("/sdcard/Download/senticnet4.txt"));//senticNet dosyası okumak için
                String line = br.readLine();//senticnet dosyasındaki kelimelere bakıyor
                while (line != null) {

                    line = line.trim();
                    int ilkBosluk = line.indexOf("	");//ilk boşluğa kadar kelime, son boşluktan sonrası kutup değeri; bkz. senticnet4.txt

                    String sozcuk = line.substring(0, ilkBosluk);
                    sozcuk = sozcuk.trim();

                    if (splited[i].equalsIgnoreCase(sozcuk)) {
                        int index = line.lastIndexOf("	") + 1;

                        String toBeParsed = line.substring(index);//kutup değeri
                        toBeParsed = toBeParsed.trim();

                        if (isDouble(toBeParsed)) {//alınan string numerical mı? kontrolü
                            double skor = Double.parseDouble(toBeParsed);
                            /*System.out.println(skor);
                            System.err.println("Öncesi: "+sum);*/
                            sum += skor;
                            count++;
                           /* System.out.println("**burda**");
                            System.err.println("Sum "+sum);
                            System.out.println("**burda**");*/


                        } else {
                            System.err.println("Bir double parse edilemedi.");
                        }

                        break;

                    }

                    line = br.readLine();//bir sonraki line oku.
                }

            }
            System.out.println("polarity:"+(double)(sum/count)+"-"+comment);
            this.polarity = sum/count;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isDouble(String str){
        try{
            Double.parseDouble(str);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public int compareTo (Tweet tweet1) {
        return (int) ((this.polarity * 1000) - (tweet1.polarity * 1000));
    }
}
