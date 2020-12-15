package com.snreloaded;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NHLPolling {
    public static String getRequest(String webAddr) throws IOException {
        URL url = new URL("http://statsapi.web.nhl.com/api/v1/teams/");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();

        String response = "";
        if ( responseCode != 200 ) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            Scanner sc = new Scanner(url.openStream());
            while ( sc.hasNext() ) {
                response += sc.nextLine() + "\n";
            }
            /*System.out.println("\nJSON data in string format");
            System.out.println(response);*/
            sc.close();
            return response;
        }
    }

    public static NHLStats checkNHL(int teamNumber) throws IOException, ParseException {
        String response = getRequest("http://statsapi.web.nhl.com/api/v1/teams/"+teamNumber+"?expand=team.stats");

        System.out.println("\nBack from GET\n");

        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));

        System.out.println("\nAfter Parser\n");

        //Below is "teams List" but will only be 1 team
        JSONArray teamsList = (JSONArray) initialObject.get("teams");
        JSONObject team = (JSONObject) teamsList.get(0);

        System.out.println("\nAfter getting team\n");

        JSONArray teamStatsArr = (JSONArray) team.get("teamStats");

        System.out.println("\nAfter teamStatsArr\n");

        JSONObject teamStats = (JSONObject) teamStatsArr.get(0);

        System.out.println("\nAfter getting \"teamStats\"\n");

        JSONArray splits = (JSONArray) teamStats.get("splits");

        System.out.println("\nBefore stats\n");

        JSONObject stats = (JSONObject) ((JSONObject)splits.get(0)).get("stat");

        System.out.println("\nAfter JSON manipulation\n");

        NHLStats ret = new Gson().fromJson(stats.toJSONString(), NHLStats.class);

        System.out.println("\nBefore Return\n");

        return ret;
    }

    public static String listTeams() throws IOException, ParseException {
        String response = getRequest("here");
        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));
        JSONArray teamsList = (JSONArray) initialObject.get("teams");

        JSONObject curTeam = (JSONObject)teamsList.get(0);
        String ret = curTeam.get("id") + "\t:\t" + curTeam.get("name");
        for ( int i = 1; i < teamsList.size(); i++ ) {
            curTeam = (JSONObject)teamsList.get(i);
            ret += "\n" + curTeam.get("id") + "\t:\t" + curTeam.get("name");
        }
        return ret;
    }
}
