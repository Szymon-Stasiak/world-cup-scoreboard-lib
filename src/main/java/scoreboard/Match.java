package scoreboard;

import static scoreboard.common.Validators.validateNames;


public class Match {

    private final String homeTeam;
    private final String awayTeam;
    private int homeTeamPoints;
    private int awayTeamPoints;
    private final long startTime;

    public Match(String homeTeam, String awayTeam) {
        validateNames(homeTeam, awayTeam);
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = 0;
        this.awayTeamPoints = 0;
        this.startTime = System.nanoTime();
    }

    public void updateScore(int homeTeamPoints, int awayTeamPoints) {
        if (homeTeamPoints < 0 || awayTeamPoints < 0) {
            throw new IllegalArgumentException(Constants.ERR_NEGATIVE_SCORE);
        }
        this.homeTeamPoints = homeTeamPoints;
        this.awayTeamPoints = awayTeamPoints;
    }

    public int getTotalScore() {
        return homeTeamPoints + awayTeamPoints;
    }

    public int getHomeTeamPoints() {
        return homeTeamPoints;
    }

    public int getAwayTeamPoints() {
        return awayTeamPoints;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public Match(Match source) {
        this.homeTeam = source.homeTeam;
        this.awayTeam = source.awayTeam;
        this.homeTeamPoints = source.homeTeamPoints;
        this.awayTeamPoints = source.awayTeamPoints;
        this.startTime = source.startTime;
    }

}