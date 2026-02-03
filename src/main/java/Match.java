import java.time.Instant;

public class Match {

    private final String homeTeam;
    private final String awayTeam;
    private int homeTeamPoints;
    private int awayTeamPoints;
    private final Instant startTime;

    public Match(String homeTeam, String awayTeam) {
        if (homeTeam == null || homeTeam.isEmpty() || awayTeam == null || awayTeam.isEmpty()) {
            throw new IllegalArgumentException("Team names cannot be null or empty");
        }
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamPoints = 0;
        this.awayTeamPoints = 0;
        this.startTime = Instant.now();
    }

    public void updateScore(int homeTeamPoints, int awayTeamPoints) {
        if (homeTeamPoints < 0 || awayTeamPoints < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
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

    public Instant getStartTime() {
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
        return homeTeam + " - " + awayTeam;
    }
}