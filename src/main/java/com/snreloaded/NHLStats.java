package com.snreloaded;

import java.util.Map;

public class NHLStats {

    private final String conferenceAbbrev;
    private final int conferenceHomeSequence;
    private final int conferenceL10Sequence;
    private final String conferenceName;
    private final int conferenceRoadSequence;
    private final int conferenceSequence;
    private final String date;
    private final String divisionAbbrev;
    private final int divisionHomeSequence;
    private final int divisionL10Sequence;
    private final String divisionName;
    private final int divisionRoadSequence;
    private final int divisionSequence;
    private final int gameTypeId;
    private final int gamesPlayed;
    private final int goalDifferential;
    private final float goalDifferentialPctg;
    private final int goalAgainst;
    private final int goalFor;
    private final float goalsForPctg;
    private final int homeGamesPlayed;
    private final int homeGoalDifferential;
    private final int homeGoalsAgainst;
    private final int homeGoalsFor;
    private final int homeLosses;
    private final int homeOtLosses;
    private final int homePoints;
    private final int homeRegulationPlusOtWins;
    private final int homeRegulationWins;
    private final int homeTies;
    private final int homeWins;
    private final int l10GamesPlayer;
    private final int l10GoalDifferential;
    private final int l10GoalsAgainst;
    private final int l10GoalsFor;
    private final int l10Losses;
    private final int l10OtLosses;
    private final int l10Points;
    private final int l10RegulationPlusOtWins;
    private final int l10RegulationWins;
    private final int l10Ties;
    private final int l10Wins;
    private final int leagueHomeSequence;
    private final int leagueL10Sequence;
    private final int leagueRoadSequence;
    private final int leagueSequence;
    private final int losses;
    private final int otLosses;
    private final Map<String, String> placeName;
    private final float pointPctg;
    private final int points;
    private final float regulationPlusOtWinPctg;
    private final int regulationPlusOtWins;
    private final float regulationWinPctg;
    private final int regulationWins;
    private final int roadGamesPlayed;
    private final int roadGoalDifferential;
    private final int roadGoalsAgainst;
    private final int roadGoalsFor;
    private final int roadLosses;
    private final int roadOtLosses;
    private final int roadPoints;
    private final int roadRegulationPlusOtWins;
    private final int roadRegulationWins;
    private final int roadTies;
    private final int roadWins;
    private final int seasonId;
    private final int shootoutLosses;
    private final int shootoutWins;
    private final String streakCode;
    private final int streakCount;
    private final Map<String, String> teamName;
    private final Map<String, String> teamCommonName;
    private final Map<String, String> teamAbbrev;
    private final String teamLogo;
    private final int ties;
    private final int waiversSequence;
    private final int wildcardSequence;
    private final float winPctg;
    private final int wins;

    public NHLStats(String conferenceAbbrev, int conferenceHomeSequence, int conferenceL10Sequence, String conferenceName, int conferenceRoadSequence, int conferenceSequence, String date, String divisionAbbrev, int divisionHomeSequence, int divisionL10Sequence, String divisionName, int divisionRoadSequence, int divisionSequence, int gameTypeId, int gamesPlayed, int goalDifferential, float goalDifferentialPctg, int goalAgainst, int goalFor, float goalsForPctg, int homeGamesPlayed, int homeGoalDifferential, int homeGoalsAgainst, int homeGoalsFor, int homeLosses, int homeOtLosses, int homePoints, int homeRegulationPlusOtWins, int homeRegulationWins, int homeTies, int homeWins, int l10GamesPlayer, int l10GoalDifferential, int l10GoalsAgainst, int l10GoalsFor, int l10Losses, int l10OtLosses, int l10Points, int l10RegulationPlusOtWins, int l10RegulationWins, int l10Ties, int l10Wins, int leagueHomeSequence, int leagueL10Sequence, int leagueRoadSequence, int leagueSequence, int losses, int otLosses, Map<String, String> placeName, float pointPctg, int points, float regulationPlusOtWinPctg, int regulationPlusOtWins, float regulationWinPctg, int regulationWins, int roadGamesPlayed, int roadGoalDifferential, int roadGoalsAgainst, int roadGoalsFor, int roadLosses, int roadOtLosses, int roadPoints, int roadRegulationPlusOtWins, int roadRegulationWins, int roadTies, int roadWins, int seasonId, int shootoutLosses, int shootoutWins, String streakCode, int streakCount, Map<String, String> teamName, Map<String, String> teamCommonName, Map<String, String> teamAbbrev, String teamLogo, int ties, int waiversSequence, int wildcardSequence, float winPctg, int wins) {
        this.conferenceAbbrev = conferenceAbbrev;
        this.conferenceHomeSequence = conferenceHomeSequence;
        this.conferenceL10Sequence = conferenceL10Sequence;
        this.conferenceName = conferenceName;
        this.conferenceRoadSequence = conferenceRoadSequence;
        this.conferenceSequence = conferenceSequence;
        this.date = date;
        this.divisionAbbrev = divisionAbbrev;
        this.divisionHomeSequence = divisionHomeSequence;
        this.divisionL10Sequence = divisionL10Sequence;
        this.divisionName = divisionName;
        this.divisionRoadSequence = divisionRoadSequence;
        this.divisionSequence = divisionSequence;
        this.gameTypeId = gameTypeId;
        this.gamesPlayed = gamesPlayed;
        this.goalDifferential = goalDifferential;
        this.goalDifferentialPctg = goalDifferentialPctg;
        this.goalAgainst = goalAgainst;
        this.goalFor = goalFor;
        this.goalsForPctg = goalsForPctg;
        this.homeGamesPlayed = homeGamesPlayed;
        this.homeGoalDifferential = homeGoalDifferential;
        this.homeGoalsAgainst = homeGoalsAgainst;
        this.homeGoalsFor = homeGoalsFor;
        this.homeLosses = homeLosses;
        this.homeOtLosses = homeOtLosses;
        this.homePoints = homePoints;
        this.homeRegulationPlusOtWins = homeRegulationPlusOtWins;
        this.homeRegulationWins = homeRegulationWins;
        this.homeTies = homeTies;
        this.homeWins = homeWins;
        this.l10GamesPlayer = l10GamesPlayer;
        this.l10GoalDifferential = l10GoalDifferential;
        this.l10GoalsAgainst = l10GoalsAgainst;
        this.l10GoalsFor = l10GoalsFor;
        this.l10Losses = l10Losses;
        this.l10OtLosses = l10OtLosses;
        this.l10Points = l10Points;
        this.l10RegulationPlusOtWins = l10RegulationPlusOtWins;
        this.l10RegulationWins = l10RegulationWins;
        this.l10Ties = l10Ties;
        this.l10Wins = l10Wins;
        this.leagueHomeSequence = leagueHomeSequence;
        this.leagueL10Sequence = leagueL10Sequence;
        this.leagueRoadSequence = leagueRoadSequence;
        this.leagueSequence = leagueSequence;
        this.losses = losses;
        this.otLosses = otLosses;
        this.placeName = placeName;
        this.pointPctg = pointPctg;
        this.points = points;
        this.regulationPlusOtWinPctg = regulationPlusOtWinPctg;
        this.regulationPlusOtWins = regulationPlusOtWins;
        this.regulationWinPctg = regulationWinPctg;
        this.regulationWins = regulationWins;
        this.roadGamesPlayed = roadGamesPlayed;
        this.roadGoalDifferential = roadGoalDifferential;
        this.roadGoalsAgainst = roadGoalsAgainst;
        this.roadGoalsFor = roadGoalsFor;
        this.roadLosses = roadLosses;
        this.roadOtLosses = roadOtLosses;
        this.roadPoints = roadPoints;
        this.roadRegulationPlusOtWins = roadRegulationPlusOtWins;
        this.roadRegulationWins = roadRegulationWins;
        this.roadTies = roadTies;
        this.roadWins = roadWins;
        this.seasonId = seasonId;
        this.shootoutLosses = shootoutLosses;
        this.shootoutWins = shootoutWins;
        this.streakCode = streakCode;
        this.streakCount = streakCount;
        this.teamName = teamName;
        this.teamCommonName = teamCommonName;
        this.teamAbbrev = teamAbbrev;
        this.teamLogo = teamLogo;
        this.ties = ties;
        this.waiversSequence = waiversSequence;
        this.wildcardSequence = wildcardSequence;
        this.winPctg = winPctg;
        this.wins = wins;
    }

    public String getConferenceAbbrev() {
        return conferenceAbbrev;
    }

    public int getConferenceHomeSequence() {
        return conferenceHomeSequence;
    }

    public int getConferenceL10Sequence() {
        return conferenceL10Sequence;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public int getConferenceRoadSequence() {
        return conferenceRoadSequence;
    }

    public int getConferenceSequence() {
        return conferenceSequence;
    }

    public String getDate() {
        return date;
    }

    public String getDivisionAbbrev() {
        return divisionAbbrev;
    }

    public int getDivisionHomeSequence() {
        return divisionHomeSequence;
    }

    public int getDivisionL10Sequence() {
        return divisionL10Sequence;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public int getDivisionRoadSequence() {
        return divisionRoadSequence;
    }

    public int getDivisionSequence() {
        return divisionSequence;
    }

    public int getGameTypeId() {
        return gameTypeId;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGoalDifferential() {
        return goalDifferential;
    }

    public float getGoalDifferentialPctg() {
        return goalDifferentialPctg;
    }

    public int getGoalAgainst() {
        return goalAgainst;
    }

    public int getGoalFor() {
        return goalFor;
    }

    public float getGoalsForPctg() {
        return goalsForPctg;
    }

    public int getHomeGamesPlayed() {
        return homeGamesPlayed;
    }

    public int getHomeGoalDifferential() {
        return homeGoalDifferential;
    }

    public int getHomeGoalsAgainst() {
        return homeGoalsAgainst;
    }

    public int getHomeGoalsFor() {
        return homeGoalsFor;
    }

    public int getHomeLosses() {
        return homeLosses;
    }

    public int getHomeOtLosses() {
        return homeOtLosses;
    }

    public int getHomePoints() {
        return homePoints;
    }

    public int getHomeRegulationPlusOtWins() {
        return homeRegulationPlusOtWins;
    }

    public int getHomeRegulationWins() {
        return homeRegulationWins;
    }

    public int getHomeTies() {
        return homeTies;
    }

    public int getHomeWins() {
        return homeWins;
    }

    public int getL10GamesPlayer() {
        return l10GamesPlayer;
    }

    public int getL10GoalDifferential() {
        return l10GoalDifferential;
    }

    public int getL10GoalsAgainst() {
        return l10GoalsAgainst;
    }

    public int getL10GoalsFor() {
        return l10GoalsFor;
    }

    public int getL10Losses() {
        return l10Losses;
    }

    public int getL10OtLosses() {
        return l10OtLosses;
    }

    public int getL10Points() {
        return l10Points;
    }

    public int getL10RegulationPlusOtWins() {
        return l10RegulationPlusOtWins;
    }

    public int getL10RegulationWins() {
        return l10RegulationWins;
    }

    public int getL10Ties() {
        return l10Ties;
    }

    public int getL10Wins() {
        return l10Wins;
    }

    public int getLeagueHomeSequence() {
        return leagueHomeSequence;
    }

    public int getLeagueL10Sequence() {
        return leagueL10Sequence;
    }

    public int getLeagueRoadSequence() {
        return leagueRoadSequence;
    }

    public int getLeagueSequence() {
        return leagueSequence;
    }

    public int getLosses() {
        return losses;
    }

    public int getOtLosses() {
        return otLosses;
    }

    public String getPlaceName() {
        return placeName.get("default");
    }

    public float getPointPctg() {
        return pointPctg;
    }

    public int getPoints() {
        return points;
    }

    public float getRegulationPlusOtWinPctg() {
        return regulationPlusOtWinPctg;
    }

    public int getRegulationPlusOtWins() {
        return regulationPlusOtWins;
    }

    public float getRegulationWinPctg() {
        return regulationWinPctg;
    }

    public int getRegulationWins() {
        return regulationWins;
    }

    public int getRoadGamesPlayed() {
        return roadGamesPlayed;
    }

    public int getRoadGoalDifferential() {
        return roadGoalDifferential;
    }

    public int getRoadGoalsAgainst() {
        return roadGoalsAgainst;
    }

    public int getRoadGoalsFor() {
        return roadGoalsFor;
    }

    public int getRoadLosses() {
        return roadLosses;
    }

    public int getRoadOtLosses() {
        return roadOtLosses;
    }

    public int getRoadPoints() {
        return roadPoints;
    }

    public int getRoadRegulationPlusOtWins() {
        return roadRegulationPlusOtWins;
    }

    public int getRoadRegulationWins() {
        return roadRegulationWins;
    }

    public int getRoadTies() {
        return roadTies;
    }

    public int getRoadWins() {
        return roadWins;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public int getShootoutLosses() {
        return shootoutLosses;
    }

    public int getShootoutWins() {
        return shootoutWins;
    }

    public String getStreakCode() {
        return streakCode;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public String getTeamName() {
        return teamName.get("default");
    }

    public String getTeamCommonName() {
        return teamCommonName.get("default");
    }

    public String getTeamAbbrev() {
        return teamAbbrev.get("default");
    }

    public String getTeamLogo() {
        return teamLogo;
    }

    public int getTies() {
        return ties;
    }

    public int getWaiversSequence() {
        return waiversSequence;
    }

    public int getWildcardSequence() {
        return wildcardSequence;
    }

    public float getWinPctg() {
        return winPctg;
    }

    public int getWins() {
        return wins;
    }

    @Override
    public String toString() {
        return getTeamCommonName()+ " {" +
                "\n\tconferenceAbbrev = " + conferenceAbbrev +
                "\n\tconferenceHomeSequence = " + conferenceHomeSequence +
                "\n\tconferenceL10Sequence = " + conferenceL10Sequence +
                "\n\tconferenceName = " + conferenceName +
                "\n\tconferenceRoadSequence = " + conferenceRoadSequence +
                "\n\tconferenceSequence = " + conferenceSequence +
                "\n\tdate = " + date +
                "\n\tdivisionAbbrev = " + divisionAbbrev +
                "\n\tdivisionHomeSequence = " + divisionHomeSequence +
                "\n\tdivisionL10Sequence = " + divisionL10Sequence +
                "\n\tdivisionName = " + divisionName +
                "\n\tdivisionRoadSequence = " + divisionRoadSequence +
                "\n\tdivisionSequence = " + divisionSequence +
                "\n\tgameTypeId = " + gameTypeId +
                "\n\tgamesPlayed = " + gamesPlayed +
                "\n\tgoalDifferential = " + goalDifferential +
                "\n\tgoalDifferentialPctg = " + goalDifferentialPctg +
                "\n\tgoalAgainst = " + goalAgainst +
                "\n\tgoalFor = " + goalFor +
                "\n\tgoalsForPctg = " + goalsForPctg +
                "\n\thomeGamesPlayed = " + homeGamesPlayed +
                "\n\thomeGoalDifferential = " + homeGoalDifferential +
                "\n\thomeGoalsAgainst = " + homeGoalsAgainst +
                "\n\thomeGoalsFor = " + homeGoalsFor +
                "\n\thomeLosses = " + homeLosses +
                "\n\thomeOtLosses = " + homeOtLosses +
                "\n\thomePoints = " + homePoints +
                "\n\thomeRegulationPlusOtWins = " + homeRegulationPlusOtWins +
                "\n\thomeRegulationWins = " + homeRegulationWins +
                "\n\thomeTies = " + homeTies +
                "\n\thomeWins = " + homeWins +
                "\n\tl10GamesPlayer = " + l10GamesPlayer +
                "\n\tl10GoalDifferential = " + l10GoalDifferential +
                "\n\tl10GoalsAgainst = " + l10GoalsAgainst +
                "\n\tl10GoalsFor = " + l10GoalsFor +
                "\n\tl10Losses = " + l10Losses +
                "\n\tl10OtLosses = " + l10OtLosses +
                "\n\tl10Points = " + l10Points +
                "\n\tl10RegulationPlusOtWins = " + l10RegulationPlusOtWins +
                "\n\tl10RegulationWins = " + l10RegulationWins +
                "\n\tl10Ties = " + l10Ties +
                "\n\tl10Wins = " + l10Wins +
                "\n\tleagueHomeSequence = " + leagueHomeSequence +
                "\n\tleagueL10Sequence = " + leagueL10Sequence +
                "\n\tleagueRoadSequence = " + leagueRoadSequence +
                "\n\tleagueSequence = " + leagueSequence +
                "\n\tlosses = " + losses +
                "\n\totLosses = " + otLosses +
                "\n\tplaceName = " + getPlaceName() +
                "\n\tpointPctg = " + pointPctg +
                "\n\tpoints = " + points +
                "\n\tregulationPlusOtWinPctg = " + regulationPlusOtWinPctg +
                "\n\tregulationPlusOtWins = " + regulationPlusOtWins +
                "\n\tregulationWinPctg = " + regulationWinPctg +
                "\n\tregulationWins = " + regulationWins +
                "\n\troadGamesPlayed = " + roadGamesPlayed +
                "\n\troadGoalDifferential = " + roadGoalDifferential +
                "\n\troadGoalsAgainst = " + roadGoalsAgainst +
                "\n\troadGoalsFor = " + roadGoalsFor +
                "\n\troadLosses = " + roadLosses +
                "\n\troadOtLosses = " + roadOtLosses +
                "\n\troadPoints = " + roadPoints +
                "\n\troadRegulationPlusOtWins = " + roadRegulationPlusOtWins +
                "\n\troadRegulationWins = " + roadRegulationWins +
                "\n\troadTies = " + roadTies +
                "\n\troadWins = " + roadWins +
                "\n\tseasonId = " + seasonId +
                "\n\tshootoutLosses = " + shootoutLosses +
                "\n\tshootoutWins = " + shootoutWins +
                "\n\tstreakCode = " + streakCode +
                "\n\tstreakCount = " + streakCount +
                "\n\tteamName = " + getTeamName() +
                "\n\tteamCommonName = " + getTeamCommonName() +
                "\n\tteamAbbrev = " + getTeamAbbrev() +
                "\n\tteamLogo = " + teamLogo +
                "\n\tties = " + ties +
                "\n\twaiversSequence = " + waiversSequence +
                "\n\twildcardSequence = " + wildcardSequence +
                "\n\twinPctg = " + winPctg +
                "\n\twins = " + wins +
                "\n}";
    }
}