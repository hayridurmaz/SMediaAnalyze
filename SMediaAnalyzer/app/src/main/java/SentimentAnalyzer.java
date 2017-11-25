
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


/**
 * Created by Hayri Durmaz on 26.11.2017.
 */

public class SentimentAnalyzer {
    // User token, required for API access
    public static String oauth_token = "4a88c84113584c2b9887de8df77bdcc6";

    // API request data: Language and text to be analyzed
    public static String user_language = "eng";
    public static String user_text = "your house is good and the street is bad.";

    // Building the POST request to Sentiment analysis endpoint
    public static String endpoint = "https://svc02.api.bitext.com/sentiment/";
    public static String textdata = "{\"language\":\"" + "eng" + "\",\"text\":\"" + user_text + "\"}";

    SentimentAnalyzer(Tweet tweet) throws Exception {

        // Sending the POST request
        URL urlPOST = new URL(endpoint);
        HttpURLConnection connectionPOST = createAPIConnection("POST",urlPOST);
        DataOutputStream outStream = new DataOutputStream(connectionPOST.getOutputStream());
        outStream.write(textdata.getBytes("UTF8"));
        outStream.flush();
        outStream.close();

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

    public static HttpURLConnection createAPIConnection(String method, URL url) throws Exception
    {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "bearer " + oauth_token);

        if (method == "POST")
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
