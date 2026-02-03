import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {
    private static final String ERR_GAME_EXISTS = "Game between %s and %s already exists.";
    private static final String ERR_SAME_TEAMS = "Team names must be different.";
    private static final String ERR_TEAMS_PLAYING = "One or two of the teams is already playing.";
    private static final String ERR_INVALID_NAMES = "Team names cannot be null or empty";
    private static final String ERR_GAME_NOT_FOUND = "No ongoing game between %s and %s found.";
    private static final String ERR_NEGATIVE_SCORE = "Score cannot be negative";

    private final Map<String, Match> ongoingMatches;

    public ScoreBoard() {
        this.ongoingMatches = new HashMap<>();
    }

    public void startNewGame(String homeTeam, String awayTeam) {
        validateNames(homeTeam, awayTeam);

        String key = generateKey(homeTeam, awayTeam);
        if (ongoingMatches.containsKey(key)) {
            throw new IllegalArgumentException(String.format(ERR_GAME_EXISTS, homeTeam, awayTeam));
        }

        if (isAnyTeamAlreadyPlaying(homeTeam, awayTeam)) {
            throw new IllegalStateException(ERR_TEAMS_PLAYING);
        }

        ongoingMatches.put(key, new Match(homeTeam, awayTeam));
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        validateNames(homeTeam, awayTeam);

        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException(ERR_NEGATIVE_SCORE);
        }

        Match match = ongoingMatches.get(generateKey(homeTeam, awayTeam));
        if (match == null) {
            throw new IllegalArgumentException(String.format(ERR_GAME_NOT_FOUND, homeTeam, awayTeam));
        }

        match.updateScore(homeScore, awayScore);
    }

    public void finishGame(String homeTeam, String awayTeam) {
        validateNames(homeTeam, awayTeam);

        String key = generateKey(homeTeam, awayTeam);
        if (ongoingMatches.remove(key) == null) {
            throw new IllegalArgumentException(String.format(ERR_GAME_NOT_FOUND, homeTeam, awayTeam));
        }
    }

    public List<Match> getSummary() {
        return ongoingMatches.values().stream().sorted(Comparator.comparingInt(Match::getTotalScore).reversed().thenComparing(Comparator.comparingLong(Match::getStartTime).reversed())).toList();
    }


    private void validateNames(String home, String away) {
        if (home == null || away == null || home.isBlank() || away.isBlank()) {
            throw new IllegalArgumentException(ERR_INVALID_NAMES);
        }
        if (home.equals(away)) {
            throw new IllegalArgumentException(ERR_SAME_TEAMS);
        }
    }

    private boolean isAnyTeamAlreadyPlaying(String home, String away) {
        return ongoingMatches.values().stream().anyMatch(m -> m.getHomeTeam().equals(home) || m.getAwayTeam().equals(home) || m.getHomeTeam().equals(away) || m.getAwayTeam().equals(away));
    }

    private String generateKey(String home, String away) {
        return home.toLowerCase() + "-" + away.toLowerCase();
    }
}