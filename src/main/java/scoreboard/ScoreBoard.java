package scoreboard;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static scoreboard.common.Validators.validateNames;

public class ScoreBoard {

    private final Map<String, Match> ongoingMatches;

    public ScoreBoard() {
        this.ongoingMatches = new HashMap<>();
    }

    public void startNewMatch(String homeTeam, String awayTeam) {
        validateNames(homeTeam, awayTeam);

        String key = generateKey(homeTeam, awayTeam);
        if (ongoingMatches.containsKey(key)) {
            throw new IllegalArgumentException(String.format(Constants.ERR_GAME_EXISTS, homeTeam, awayTeam));
        }

        if (isAnyTeamAlreadyPlaying(homeTeam, awayTeam)) {
            throw new IllegalStateException(Constants.ERR_TEAMS_PLAYING);
        }

        ongoingMatches.put(key, new Match(homeTeam, awayTeam));
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        validateNames(homeTeam, awayTeam);

        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException(Constants.ERR_NEGATIVE_SCORE);
        }

        Match match = ongoingMatches.get(generateKey(homeTeam, awayTeam));
        if (match == null) {
            throw new IllegalArgumentException(String.format(Constants.ERR_GAME_NOT_FOUND, homeTeam, awayTeam));
        }

        match.updateScore(homeScore, awayScore);
    }

    public void finishMatch(String homeTeam, String awayTeam) {
        validateNames(homeTeam, awayTeam);

        String key = generateKey(homeTeam, awayTeam);
        if (ongoingMatches.remove(key) == null) {
            throw new IllegalArgumentException(String.format(Constants.ERR_GAME_NOT_FOUND, homeTeam, awayTeam));
        }
    }

    public List<Match> getSummary() {
        return ongoingMatches.values().stream()
                .map(Match::new)
                .sorted(Comparator.comparingInt(Match::getTotalScore).reversed()
                        .thenComparing(Comparator.comparingLong(Match::getStartTime).reversed()))
                .toList();
    }

    private boolean isAnyTeamAlreadyPlaying(String home, String away) {
        return ongoingMatches.values().stream().anyMatch(m -> m.getHomeTeam().equals(home) || m.getAwayTeam().equals(home) || m.getHomeTeam().equals(away) || m.getAwayTeam().equals(away));
    }

    private String generateKey(String home, String away) {
        return home + "-" + away;
    }
}