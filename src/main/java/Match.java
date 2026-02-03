import static common.Constants.ERR_NEGATIVE_SCORE;
import static common.KeyGenerator.generateKey;
import static common.Constants.ERR_INVALID_NAMES;

public class Match {

    private final String homeTeam;
    private final String awayTeam;
    private int homeTeamPoints;
    private int awayTeamPoints;
    private final long startTime;

    public Match(String homeTeam, String awayTeam) {
        if (homeTeam == null || homeTeam.isEmpty() || awayTeam == null || awayTeam.isEmpty()) {
            throw new IllegalArgumentException(ERR_INVALID_NAMES);
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = 0;
        this.awayTeamPoints = 0;
        this.startTime = System.nanoTime();
    }

    public void updateScore(int homeTeamPoints, int awayTeamPoints) {
        if (homeTeamPoints < 0 || awayTeamPoints < 0) {
            throw new IllegalArgumentException(ERR_NEGATIVE_SCORE);
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

    @Override
    public String toString() {
        return generateKey(homeTeam, awayTeam);
    }
}