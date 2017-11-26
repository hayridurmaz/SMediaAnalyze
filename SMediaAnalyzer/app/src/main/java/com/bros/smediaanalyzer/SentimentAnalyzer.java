package com.bros.smediaanalyzer;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.bros.smediaanalyzer.Topic;
import com.bros.smediaanalyzer.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.simple.parser.*;
import org.json.simple.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Created by Hayri Durmaz on 26.11.2017.
 */

public class SentimentAnalyzer {
    // User token, required for API access
    public static String oauth_token;

    // API request data: Language and text to be analyzed
    public static String user_language;
    public static String user_text;

    // Building the POST request to Sentiment analysis endpoint
    public static String endpoint;
    public static String textdata;

    ArrayList<Topic> topicArrayList = new ArrayList<>();

    public SentimentAnalyzer(Tweet tweet) throws Exception {
        oauth_token = "07aee56185614be28de5b216cf4cdbfe";
        user_language = "eng";
        user_text = tweet.comment;
        endpoint = "https://svc02.api.bitext.com/sentiment/";
        textdata = "{\"language\":\"" + "eng" + "\",\"text\":\"" + user_text + "\"}";


       /*RequestQueue mRequestQueue;

// Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

// Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

// Start the queue
        mRequestQueue.start();

        String url ="http://www.example.com";

// Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Do something with the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

// Add the request to the RequestQueue.
        mRequestQueue.add(stringRequest);*/

        // Sending the POST request
        URL urlPOST = new URL(endpoint);
        HttpURLConnection connectionPOST = createAPIConnection("POST",urlPOST);
        DataOutputStream outStream = new DataOutputStream(connectionPOST.getOutputStream());
        outStream.write(textdata.getBytes("UTF8"));
        outStream.flush();
        outStream.close();
        System.out.println("Sonuc: " + connectionPOST.getResponseCode());

        // Processing the response code of the POST request
        switch (connectionPOST.getResponseCode())
        {
            case 201:  // 201 is the code for succesful request processing
                break;
            // 401 is the error code corresponding to an invalid token
            case 401:  System.out.println ( "Your authentication token is not correct" );
                System.exit(0);
            case 402:  System.out.println ( "No contract found for that language" );
                System.exit(0);
            default:
                break;
        }

        // Processing the result of the POST request
        String sResponse1 = getAPIResponse(connectionPOST);
        JSONObject jResponse1 = new JSONObject(sResponse1);
        String action_id = jResponse1.getString("resultid");		// Identifier to request the analysis results
        String post_msg = jResponse1.getString("message");			// Error message, if applicable
        boolean post_result = jResponse1.getBoolean("success");		// Success of the request

        System.out.println ( "POST: " + post_msg + "\n");

        if (post_result)
        {
            System.out.println ( "Waiting for analisis results...\n" );

            // GET request loop, using the response identifier returned in the POST answer
            // Ask for the result of the analysis launched before
            // Try until the analysis is ready and the API returns it

            URL urlGET = new URL(endpoint + action_id + "/");
            String sResponse2 = "";
            HttpURLConnection connectionGET = null;
            while (sResponse2 == "")
            {
                connectionGET = createAPIConnection("GET",urlGET);
                if (connectionGET.getResponseCode() == 200)
                {
                    sResponse2 = getAPIResponse(connectionGET);
                }
            }
            // The loop ends when we have response to the GET request
            System.out.println ( "POST: " + connectionGET.getResponseMessage() + "\n");

            // In the GET response we have the result of the analysis
            System.out.println ( "Analisys results:\n" );
            System.out.println(sResponse2); // Print it with specified indentation
            System.out.println ( "" );
            try{
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(sResponse2);
                JSONObject jsonObject = objectToJSONObject(obj);
                JSONArray subObject = jsonObject.optJSONArray("sentimentanalysis");
                for(int i = 0; i < subObject.length(); i++){
                    double pol = Double.parseDouble(subObject.getJSONObject(i).get("score").toString());
                    String top = subObject.getJSONObject(i).get("topic").toString();
                    Topic topic = new Topic(top,pol);
                    tweet.topics.add(topic);

                }



                //System.out.println(jsonObject.get("score"));
                //System.out.println(jsonObject.isNull("score"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    public SentimentAnalyzer(String str) throws Exception {
        oauth_token = "4a88c84113584c2b9887de8df77bdcc6";
        user_language = "eng";
        user_text = str;
        endpoint = "https://svc02.api.bitext.com/sentiment/";
        textdata = "{\"language\":\"" + "eng" + "\",\"text\":\"" + user_text + "\"}";


        // Sending the POST request
        URL urlPOST = new URL(endpoint);
        HttpURLConnection connectionPOST = createAPIConnection("POST",urlPOST);
        DataOutputStream outStream = new DataOutputStream(connectionPOST.getOutputStream());
        outStream.write(textdata.getBytes("UTF8"));
        outStream.flush();
        outStream.close();
        System.out.println("Sonuc: " + connectionPOST.getResponseCode());

        // Processing the response code of the POST request
        switch (connectionPOST.getResponseCode())
        {
            case 201:  // 201 is the code for succesful request processing
                break;
            // 401 is the error code corresponding to an invalid token
            case 401:  System.out.println ( "Your authentication token is not correct" );
                System.exit(0);
            case 402:  System.out.println ( "No contract found for that language" );
                System.exit(0);
            default:
                break;
        }

        // Processing the result of the POST request
        String sResponse1 = getAPIResponse(connectionPOST);
        JSONObject jResponse1 = new JSONObject(sResponse1);
        String action_id = jResponse1.getString("resultid");		// Identifier to request the analysis results
        String post_msg = jResponse1.getString("message");			// Error message, if applicable
        boolean post_result = jResponse1.getBoolean("success");		// Success of the request

        System.out.println ( "POST: " + post_msg + "\n");

        if (post_result)
        {
            System.out.println ( "Waiting for analisis results...\n" );

            // GET request loop, using the response identifier returned in the POST answer
            // Ask for the result of the analysis launched before
            // Try until the analysis is ready and the API returns it

            URL urlGET = new URL(endpoint + action_id + "/");
            String sResponse2 = "";
            HttpURLConnection connectionGET = null;
            while (sResponse2 == "")
            {
                connectionGET = createAPIConnection("GET",urlGET);
                if (connectionGET.getResponseCode() == 200)
                {
                    sResponse2 = getAPIResponse(connectionGET);
                }
            }
            // The loop ends when we have response to the GET request
            System.out.println ( "POST: " + connectionGET.getResponseMessage() + "\n");

            // In the GET response we have the result of the analysis
            System.out.println ( "Analisys results:\n" );
            System.out.println(sResponse2); // Print it with specified indentation
            System.out.println ( "" );
            try{
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(sResponse2);
                JSONObject jsonObject = objectToJSONObject(obj);
                JSONArray subObject = jsonObject.optJSONArray("sentimentanalysis");
                for(int i = 0; i < subObject.length(); i++){
                    double pol = Double.parseDouble(subObject.getJSONObject(i).get("score").toString());
                    String top = subObject.getJSONObject(i).get("topic").toString();
                    Topic topic = new Topic(top,pol);
                    //tweet.topics.add(topic);
                    topicArrayList.add(topic);
                }



                //System.out.println(jsonObject.get("score"));
                //System.out.println(jsonObject.isNull("score"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    public ArrayList<Topic> getTopicArrayList(){
        return topicArrayList;
    }

    public static HttpURLConnection createAPIConnection(String method, URL url) throws Exception
    {


        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "bearer " + oauth_token);

        if (Objects.equals(method, "POST"))
        {
            conn.setDoOutput(true);
            conn.setDoInput(true);
        }
        return conn;
    }

    public static String getAPIResponse(HttpURLConnection conn) throws Exception
    {

        String sResponse = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String decodedString;

        while ((decodedString = in.readLine()) != null)
        {
            sResponse = sResponse+"\n"+decodedString;
        }
        sResponse = sResponse.trim();
        in.close();
        return sResponse;
    }
    public static JSONObject objectToJSONObject(Object object){
        Object json = null;
        JSONObject jsonObject = null;
        try {
            json = new JSONTokener(object.toString()).nextValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json instanceof JSONObject) {
            jsonObject = (JSONObject) json;
        }
        return jsonObject;
    }
}
