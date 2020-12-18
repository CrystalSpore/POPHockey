package com.snreloaded;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class NHLPolling {

    // GET request without needing external libraries. GET on webAddr
    public static String getRequest(String webAddr) throws IOException {

        //setup, connect, & receive response code
        URL url = new URL(webAddr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();

        String response = "";

        //if not "OK" status, print what status code was
        if ( responseCode != 200 ) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else { //else read & return the data stream
            Scanner sc = new Scanner(url.openStream());
            while ( sc.hasNext() ) {
                response += sc.nextLine() + "\n";
            }
            sc.close();
            return response;
        }
    }

    //check stats for specific NHL team (based on NHL team ID number)
    public static NHLStats checkNHL(int teamNumber) throws IOException, ParseException {

        //GET request
        String response = getRequest("http://statsapi.web.nhl.com/api/v1/teams/"+teamNumber+"?expand=team.stats");

        System.out.println(response);

        System.out.println("\nBack from GET\n");

        //start JSON parse
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

        //serialize parsed JSON to NHLStats object for ease of use in Java
        NHLStats ret = new Gson().fromJson(stats.toJSONString(), NHLStats.class);

        System.out.println("\nBefore Return\n");

        return ret;
    }

    //GET & return list of all teams from NHL API
    public static String listTeams() throws IOException, ParseException {

        //GET request
        String response = getRequest("http://statsapi.web.nhl.com/api/v1/teams/");

        //Start JSON parse
        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));
        JSONArray teamsList = (JSONArray) initialObject.get("teams");

        //build list of teams for printing
        //  (formatting might need to be fixed for :'s to line up)
        JSONObject curTeam = (JSONObject)teamsList.get(0);
        String ret = curTeam.get("id") + "\t:\t" + curTeam.get("name");
        for ( int i = 1; i < teamsList.size(); i++ ) {
            curTeam = (JSONObject)teamsList.get(i);
            ret += "\n" + curTeam.get("id") + "\t:\t" + curTeam.get("name");
        }
        return ret;
    }
}
