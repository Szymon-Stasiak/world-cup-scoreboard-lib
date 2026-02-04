package scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import scoreboard.common.Validators;


import static scoreboard.Constants.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {

    private ScoreBoard scoreBoard;

    private static final String HOME_TEAM = "TeamA";
    private static final String AWAY_TEAM = "TeamB";
    private static final String OTHER_TEAM = "TeamC";

    @BeforeEach
    void setUp() {
        scoreBoard = new ScoreBoard();
    }

    @Test
    void givenNoExistingGame_whenStartingNewGame_thenScoreIsZeroAndStartTimeIsSet() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        List<Match> summary = scoreBoard.getSummary();
        assertEquals(1, summary.size());
        Match match = summary.get(0);
        assertMatchState(match, HOME_TEAM, AWAY_TEAM, 0, 0);
        assertNotNull(match.getStartTime());
    }

    @Test
    void givenExistingGame_whenStartingNewGame_thenThrowsException() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM),
                String.format(ERR_GAME_EXISTS, HOME_TEAM, AWAY_TEAM));
        assertEquals(1, scoreBoard.getSummary().size());
    }

    @Test
    void givenGameWithSameTeamNames_whenStartingNewGame_thenThrowsException() {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.startNewMatch(HOME_TEAM, HOME_TEAM),
                ERR_SAME_TEAMS);
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenGameWithOneTeamCurrentlyPlaying_whenStartingNewGame_thenThrowsException() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        assertThrowsWithMessage(IllegalStateException.class,
                () -> scoreBoard.startNewMatch(HOME_TEAM, OTHER_TEAM),
                ERR_TEAMS_PLAYING);
        assertEquals(1, scoreBoard.getSummary().size());
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenStartingNewGame_thenExceptionIsThrown(String home, String away) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames(home, away));
        String expectedMessage = (home == null || home.isBlank() || away == null || away.isBlank())
                ? ERR_INVALID_NAMES : ERR_SAME_TEAMS;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void givenMultipleProperGames_whenStartingNewGames_thenAllGamesAreAdded() {
        int numberOfGames = 10;
        for (int i = 1; i <= numberOfGames; i++) {
            scoreBoard.startNewMatch("Team" + (i * 2 - 1), "Team" + (i * 2));
        }
        List<Match> summary = scoreBoard.getSummary();
        assertEquals(numberOfGames, summary.size());
        long uniqueMatches = summary.stream()
                .map(Match::toString)
                .distinct()
                .count();
        assertEquals(numberOfGames, uniqueMatches);
    }

    @Test
    void givenMultipleNotProperGames_whenStartingNewGames_thenExceptionsAreThrown() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        assertThrowsWithMessage(IllegalArgumentException.class, () -> scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM), String.format(ERR_GAME_EXISTS, HOME_TEAM, AWAY_TEAM));
        assertThrowsWithMessage(IllegalStateException.class, () -> scoreBoard.startNewMatch(HOME_TEAM, OTHER_TEAM), ERR_TEAMS_PLAYING);
        assertThrowsWithMessage(IllegalStateException.class, () -> scoreBoard.startNewMatch(OTHER_TEAM, AWAY_TEAM), ERR_TEAMS_PLAYING);
        assertThrowsWithMessage(IllegalStateException.class, () -> scoreBoard.startNewMatch(AWAY_TEAM, HOME_TEAM), ERR_TEAMS_PLAYING);
        assertThrowsWithMessage(IllegalStateException.class, () -> scoreBoard.startNewMatch(AWAY_TEAM, OTHER_TEAM), ERR_TEAMS_PLAYING);
        assertThrowsWithMessage(IllegalStateException.class, () -> scoreBoard.startNewMatch(OTHER_TEAM, HOME_TEAM), ERR_TEAMS_PLAYING);
        assertThrowsWithMessage(IllegalArgumentException.class, () -> scoreBoard.startNewMatch(AWAY_TEAM, AWAY_TEAM), ERR_SAME_TEAMS);
        assertEquals(1, scoreBoard.getSummary().size());
    }

    @Test
    void givenOngoingGame_whenFinishingGame_thenGameIsRemoved() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishMatch(HOME_TEAM, AWAY_TEAM);
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenNonExistingGame_whenFinishingGame_thenThrowsException() {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.finishMatch(HOME_TEAM, AWAY_TEAM),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));
        assertTrue(scoreBoard.getSummary().isEmpty());
    }


    @Test
    void givenOngoingGame_whenFinishingTwice_thenThrowsException() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishMatch(HOME_TEAM, AWAY_TEAM);
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.finishMatch(HOME_TEAM, AWAY_TEAM),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenOngoingGame_whenUpdatingScore_thenScoreIsUpdated() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3);
        List<Match> summary = scoreBoard.getSummary();
        assertEquals(1, summary.size());
        assertMatchState(summary.get(0), HOME_TEAM, AWAY_TEAM, 2, 3);
    }

    @Test
    void givenNonExistingGame_whenUpdatingScore_thenThrowsException() {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenOngoingGame_whenUpdatingScoreWithNegativeValues_thenThrowsException() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, -1, 3),
                ERR_NEGATIVE_SCORE);
        assertEquals(1, scoreBoard.getSummary().size());
    }

    @Test
    void givenOngoingGame_whenUpdatingScoreTwice_thenScoreIsUpdatedCorrectly() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3);
        scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 4, 5);
        assertMatchState(scoreBoard.getSummary().get(0), HOME_TEAM, AWAY_TEAM, 4, 5);
    }

    @Test
    void givenMultipleOngoingGames_whenGettingSummary_thenCorrectOrderIsReturned() {
        createGameWithScore("Team1", "Team2", 1, 1);
        createGameWithScore("Team3", "Team4", 2, 2);
        createGameWithScore("Team5", "Team6", 0, 0);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertMatchState(summary.get(0), "Team3", "Team4", 2, 2);
        assertMatchState(summary.get(1), "Team1", "Team2", 1, 1);
        assertMatchState(summary.get(2), "Team5", "Team6", 0, 0);
    }

    @Test
    void givenMultipleOngoingGamesWithSameSumScore_whenGettingSummary_thenCorrectOrderIsReturned() {
        createGameWithScore("Team1", "Team2", 2, 2);
        createGameWithScore("Team3", "Team4", 1, 3);
        createGameWithScore("Team5", "Team6", 0, 4);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertMatchState(summary.get(0), "Team5", "Team6", 0, 4);
        assertMatchState(summary.get(1), "Team3", "Team4", 1, 3);
        assertMatchState(summary.get(2), "Team1", "Team2", 2, 2);
    }

    @Test
    void givenNoOngoingGames_whenGettingSummary_thenEmptyListIsReturned() {
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenManyOngoingGamesWithVariousScores_whenGettingSummary_thenCorrectOrderIsReturned() {
        createGameWithScore("TeamA", "TeamB", 2, 3);
        createGameWithScore("TeamC", "TeamD", 4, 3);
        createGameWithScore("TeamE", "TeamF", 1, 1);
        createGameWithScore("TeamG", "TeamH", 5, 2);
        createGameWithScore("TeamI", "TeamJ", 0, 0);
        createGameWithScore("TeamK", "TeamL", 1, 4);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals(6, summary.size());
        assertMatchState(summary.get(0), "TeamG", "TeamH", 5, 2);
        assertMatchState(summary.get(1), "TeamC", "TeamD", 4, 3);
        assertMatchState(summary.get(2), "TeamK", "TeamL", 1, 4);
        assertMatchState(summary.get(3), "TeamA", "TeamB", 2, 3);
        assertMatchState(summary.get(4), "TeamE", "TeamF", 1, 1);
        assertMatchState(summary.get(5), "TeamI", "TeamJ", 0, 0);
    }

    @Test
    void givenFinishedGame_whenGettingSummary_thenGameIsNotIncluded() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishMatch(HOME_TEAM, AWAY_TEAM);

        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenFinishedGame_whenUpdatingScore_thenExceptionIsThrown() {
        scoreBoard.startNewMatch(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishMatch(HOME_TEAM, AWAY_TEAM);
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    private void createGameWithScore(String home, String away, int homeScore, int awayScore) {
        scoreBoard.startNewMatch(home, away);
        scoreBoard.updateScore(home, away, homeScore, awayScore);
    }

    private void assertMatchState(Match match, String expectedHome, String expectedAway, int expectedHomePts, int expectedAwayPts) {
        assertAll("scoreboard.Match properties",
                () -> assertEquals(expectedHome, match.getHomeTeam()),
                () -> assertEquals(expectedAway, match.getAwayTeam()),
                () -> assertEquals(expectedHomePts, match.getHomeTeamPoints()),
                () -> assertEquals(expectedAwayPts, match.getAwayTeamPoints())
        );
    }

    private <T extends Throwable> void assertThrowsWithMessage(Class<T> expectedType, Executable executable, String expectedMessage) {
        T exception = assertThrows(expectedType, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}