package com.snreloaded;

public class NHLStats {
    private String teamName;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private int ot;
    private int pts;
    private String ptPctg;
    private float goalsPerGame;
    private float goalsAgainstPerGame;
    private float evGGARation;
    private String powerPlayPercentage;
    private float powerPlayGoals;
    private float powerPlayGoalsAgainst;
    private float powerPlayOpportunities;
    private String penaltyKillPercentage;
    private float shotsPerGame;
    private float shotsAllowed;
    private float winScoreFirst;
    private float winOppScoreFirst;
    private float winLeadFirstPer;
    private float winLeadSecondPer;
    private float winOutshootOpp;
    private float winOutshotByOpp;
    private float faceOffsTaken;
    private float faceOffsWon;
    private float faceOffsLost;
    private String faceOffWinPercentage;
    private float shootingPctg;
    private float savePctg;

    public NHLStats(String teamName, int gamesPlayed, int wins, int losses, int ot, int pts, String ptPctg, float goalsPerGame, float goalsAgainstPerGame, float evGGARation, String powerPlayPercentage, float powerPlayGoals, float powerPlayGoalsAgainst, float powerPlayOpportunities, String penaltyKillPercentage, float shotsPerGame, float shotsAllowed, float winScoreFirst, float winOppScoreFirst, float winLeadFirstPer, float winLeadSecondPer, float winOutshootOpp, float winOutshotByOpp, float faceOffsTaken, float faceOffsWon, float faceOffsLost, String faceOffWinPercentage, float shootingPctg, float savePctg) {
        this.teamName = teamName;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.ot = ot;
        this.pts = pts;
        this.ptPctg = ptPctg;
        this.goalsPerGame = goalsPerGame;
        this.goalsAgainstPerGame = goalsAgainstPerGame;
        this.evGGARation = evGGARation;
        this.powerPlayPercentage = powerPlayPercentage;
        this.powerPlayGoals = powerPlayGoals;
        this.powerPlayGoalsAgainst = powerPlayGoalsAgainst;
        this.powerPlayOpportunities = powerPlayOpportunities;
        this.penaltyKillPercentage = penaltyKillPercentage;
        this.shotsPerGame = shotsPerGame;
        this.shotsAllowed = shotsAllowed;
        this.winScoreFirst = winScoreFirst;
        this.winOppScoreFirst = winOppScoreFirst;
        this.winLeadFirstPer = winLeadFirstPer;
        this.winLeadSecondPer = winLeadSecondPer;
        this.winOutshootOpp = winOutshootOpp;
        this.winOutshotByOpp = winOutshotByOpp;
        this.faceOffsTaken = faceOffsTaken;
        this.faceOffsWon = faceOffsWon;
        this.faceOffsLost = faceOffsLost;
        this.faceOffWinPercentage = faceOffWinPercentage;
        this.shootingPctg = shootingPctg;
        this.savePctg = savePctg;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getOt() {
        return ot;
    }

    public int getPts() {
        return pts;
    }

    public String getPtPctg() {
        return ptPctg;
    }

    public float getGoalsPerGame() {
        return goalsPerGame;
    }

    public float getGoalsAgainstPerGame() {
        return goalsAgainstPerGame;
    }

    public float getEvGGARation() {
        return evGGARation;
    }

    public String getPowerPlayPercentage() {
        return powerPlayPercentage;
    }

    public float getPowerPlayGoals() {
        return powerPlayGoals;
    }

    public float getPowerPlayGoalsAgainst() {
        return powerPlayGoalsAgainst;
    }

    public float getPowerPlayOpportunities() {
        return powerPlayOpportunities;
    }

    public String getPenaltyKillPercentage() {
        return penaltyKillPercentage;
    }

    public float getShotsPerGame() {
        return shotsPerGame;
    }

    public float getShotsAllowed() {
        return shotsAllowed;
    }

    public float getWinScoreFirst() {
        return winScoreFirst;
    }

    public float getWinOppScoreFirst() {
        return winOppScoreFirst;
    }

    public float getWinLeadFirstPer() {
        return winLeadFirstPer;
    }

    public float getWinLeadSecondPer() {
        return winLeadSecondPer;
    }

    public float getWinOutshootOpp() {
        return winOutshootOpp;
    }

    public float getWinOutshotByOpp() {
        return winOutshotByOpp;
    }

    public float getFaceOffsTaken() {
        return faceOffsTaken;
    }

    public float getFaceOffsWon() {
        return faceOffsWon;
    }

    public float getFaceOffsLost() {
        return faceOffsLost;
    }

    public String getFaceOffWinPercentage() {
        return faceOffWinPercentage;
    }

    public float getShootingPctg() {
        return shootingPctg;
    }

    public float getSavePctg() {
        return savePctg;
    }

    @Override
    public String toString() {
        return teamName + " {" +
                "\n\tgamesPlayed=" + gamesPlayed +
                "\n\twins=" + wins +
                "\n\tlosses=" + losses +
                "\n\tot=" + ot +
                "\n\tpts=" + pts +
                "\n\tptPctg='" + ptPctg + '\'' +
                "\n\tgoalsPerGame=" + goalsPerGame +
                "\n\tgoalsAgainstPerGame=" + goalsAgainstPerGame +
                "\n\tevGGARation=" + evGGARation +
                "\n\tpowerPlayPercentage='" + powerPlayPercentage + '\'' +
                "\n\tpowerPlayGoals=" + powerPlayGoals +
                "\n\tpowerPlayGoalsAgainst=" + powerPlayGoalsAgainst +
                "\n\tpowerPlayOpportunities=" + powerPlayOpportunities +
                "\n\tpenaltyKillPercentage='" + penaltyKillPercentage + '\'' +
                "\n\tshotsPerGame=" + shotsPerGame +
                "\n\tshotsAllowed=" + shotsAllowed +
                "\n\twinScoreFirst=" + winScoreFirst +
                "\n\twinOppScoreFirst=" + winOppScoreFirst +
                "\n\twinLeadFirstPer=" + winLeadFirstPer +
                "\n\twinLeadSecondPer=" + winLeadSecondPer +
                "\n\twinOutshootOpp=" + winOutshootOpp +
                "\n\twinOutshotByOpp=" + winOutshotByOpp +
                "\n\tfaceOffsTaken=" + faceOffsTaken +
                "\n\tfaceOffsWon=" + faceOffsWon +
                "\n\tfaceOffsLost=" + faceOffsLost +
                "\n\tfaceOffWinPercentage='" + faceOffWinPercentage + '\'' +
                "\n\tshootingPctg=" + shootingPctg +
                "\n\tsavePctg=" + savePctg +
                "\n}";
    }
}
