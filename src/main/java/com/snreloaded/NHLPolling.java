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
    private static String getRequest(String webAddr) throws IOException {

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
    public static NHLStats teamStats(int teamNumber) throws IOException, ParseException {

        //GET request
        String response = getRequest("http://statsapi.web.nhl.com/api/v1/teams/"+teamNumber+"?expand=team.stats");

        //start JSON parse
        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));

        //Below is "teams List" but will only be 1 team
        JSONArray teamsList = (JSONArray) initialObject.get("teams");
        JSONObject team = (JSONObject) teamsList.get(0);
        JSONArray teamStatsArr = (JSONArray) team.get("teamStats");
        JSONObject teamStats = (JSONObject) teamStatsArr.get(0);
        JSONArray splits = (JSONArray) teamStats.get("splits");
        JSONObject stats = (JSONObject) ((JSONObject)splits.get(0)).get("stat");

        //serialize parsed JSON to NHLStats object for ease of use in Java
        return new Gson().fromJson(stats.toJSONString(), NHLStats.class);
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
