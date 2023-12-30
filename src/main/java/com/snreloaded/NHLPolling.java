package com.snreloaded;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NHLPolling {

    public static JSONArray priorTeams = new JSONArray();
    public static long seasonId = 0;

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

    public static void gatherSeasonId() throws IOException, ParseException {
        String response = getRequest("https://api-web.nhle.com/v1/schedule/now");

        //Start JSON parse
        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));
        JSONArray gameWeek = (JSONArray) initialObject.get("gameWeek");
        JSONArray games = (JSONArray) ((JSONObject) gameWeek.get(0)).get("games");
        seasonId = (Long) ((JSONObject) games.get(0)).get("season");
    }

    private static JSONArray filterToCurrentSeasonTeams(JSONArray fullTeamList) throws IOException, ParseException {
        // make a clone of the fullTeamList to modify, to prevent concurrent modification exception
        JSONArray results = (JSONArray) fullTeamList.clone();

        if (priorTeams.isEmpty()) {
            for (Object o : fullTeamList) {
                JSONObject curTeam = (JSONObject) o;
                String TEAM_ABBR = (String) curTeam.get("triCode");

                if (TEAM_ABBR == null) {
                    results.remove(curTeam);
                    priorTeams.add(curTeam);
                    continue;
                }
                // GET request for the current season schedule based upon a team's 3-letter code
                String response = getRequest("https://api-web.nhle.com/v1/club-schedule-season/" + TEAM_ABBR + "/now");

                //Start JSON parse
                JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));
                JSONArray games = (JSONArray) initialObject.get("games");

                // If the team has no games for the current season,
                //   then they aren't in the roster for the current season
                if (games.isEmpty()) {
                    results.remove(curTeam);
                    priorTeams.add(curTeam);
                }
            }
        } else {
            results.removeAll(priorTeams);
        }

        return results;
    }

    //check stats for specific NHL team (based on NHL team ID number)
    public static NHLStats teamStats(int teamNumber) throws IOException, ParseException {

        //GET request
        String response = getRequest("https://api-web.nhle.com/v1/standings/now");

        //start JSON parse
        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));

        JSONArray standings = (JSONArray) initialObject.get("standings");
        JSONObject team = null;

        String teamTriCode = "";
        for (Object o : listTeams()) {
            JSONObject curTeam = (JSONObject) o;
            if ( (Long) curTeam.get("id") == teamNumber ) {
                teamTriCode = (String) curTeam.get("triCode");
            }
        }

        JSONObject standingsToPrint = null;

        for (Object o : standings) {
            JSONObject curStandings = (JSONObject) o;
            String teamAbbrev = (String) ((JSONObject) curStandings.get("teamAbbrev")).get("default");

            if ( teamAbbrev.equals(teamTriCode) ) {
                standingsToPrint = curStandings;
                break;
            }
        }

        if (standingsToPrint == null)
        {
            throw new IOException("Team not found for " + teamNumber + " within this list:\n" + standings);
        }

        //serialize parsed JSON to NHLStats object for ease of use in Java
        return new Gson().fromJson(standingsToPrint.toJSONString(), NHLStats.class);
    }

    //GET & return list of all teams from NHL API
    public static JSONArray listTeams() throws IOException, ParseException {
        System.out.println(new Date(System.currentTimeMillis()) + "Start List Teams function");

        //GET request
        String response = getRequest("https://api.nhle.com/stats/rest/en/team");

        //Start JSON parse
        JSONObject initialObject = (JSONObject) (new JSONParser().parse(response));
        JSONArray teamsList = (JSONArray) initialObject.get("data");

        // filter "bad data"
        teamsList = filterToCurrentSeasonTeams(teamsList);

        System.out.println(new Date(System.currentTimeMillis()) + "End List Teams function");
        return teamsList;
    }

    public static String printListTeams(JSONArray teamsList) {
        // build list of teams for printing
        JSONObject curTeam = (JSONObject)teamsList.get(0);
        String ret = String.format("%2d\t:\t%s", (Long) curTeam.get("id"), curTeam.get("fullName"));
        for ( int i = 1; i < teamsList.size(); i++ ) {
            curTeam = (JSONObject)teamsList.get(i);

            ret += String.format("\n%2d\t:\t%s", (Long) curTeam.get("id"), curTeam.get("fullName"));
        }

        // sort the output of the return value, for easier use
        ArrayList<String> retList = new ArrayList<>(Arrays.asList(ret.split("\n")));
        Collections.sort(retList);

        ret = "";
        for ( String s : retList )
        {
            ret += s + "\n";
        }

        return ret;
    }
}
