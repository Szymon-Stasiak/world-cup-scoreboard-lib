package scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static ReflectionUtils.AdvancedGetter.getFieldValue;
import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardTest {

    private ScoreBoard scoreBoard;

    private static final String HOME_TEAM = "TeamA";
    private static final String AWAY_TEAM = "TeamB";
    private static final String OTHER_TEAM = "TeamC";
    private static final String MATCH_KEY = "TeamA-TeamB";

    private static final String ERR_GAME_EXISTS = "Game between %s and %s already exists.";
    private static final String ERR_SAME_TEAMS = "Team names must be different.";
    private static final String ERR_TEAMS_PLAYING = "One or two of the teams is already playing.";
    private static final String ERR_INVALID_NAMES = "Team names cannot be null or empty";
    private static final String ERR_GAME_NOT_FOUND = "No ongoing game between %s and %s found.";
    private static final String ERR_NEGATIVE_SCORE = "Score cannot be negative chenge to make conficts and resolve them letter";

    @BeforeEach
    void setUp() {
        scoreBoard = new ScoreBoard();
    }

    @Test
    void givenNoExistingGame_whenStartingNewGame_thenScoreIsZeroAndStartTimeIsSet() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);

        Map<String, Match> ongoingGames = getOngoingGames();
        Match match = ongoingGames.get(MATCH_KEY);

        assertEquals(1, ongoingGames.size());
        assertNotNull(match, "Match should exist in internal state");
        assertMatchState(match, HOME_TEAM, AWAY_TEAM, 0, 0);
        assertNotNull(match.getStartTime());
    }

    @Test
    void givenExistingGame_whenStartingNewGame_thenThrowsException() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);

        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM),
                String.format(ERR_GAME_EXISTS, HOME_TEAM, AWAY_TEAM));

        Map<String, Match> ongoingGames = getOngoingGames();
        assertEquals(1, ongoingGames.size());
        assertNotNull(ongoingGames.get(MATCH_KEY));
    }

    @Test
    void givenGameWithSameTeamNames_whenStartingNewGame_thenThrowsException() {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.startNewGame(HOME_TEAM, HOME_TEAM),
                ERR_SAME_TEAMS);

        assertTrue(getOngoingGames().isEmpty());
    }

    @Test
    void givenGameWithOneTeamCurrentlyPlaying_whenStartingNewGame_thenThrowsException() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);

        assertThrowsWithMessage(IllegalStateException.class,
                () -> scoreBoard.startNewGame(HOME_TEAM, OTHER_TEAM),
                ERR_TEAMS_PLAYING);

        assertEquals(1, getOngoingGames().size());
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenStartingNewGame_thenExceptionIsThrown(String home, String away) {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.startNewGame(home, away),
                ERR_INVALID_NAMES);

        assertTrue(getOngoingGames().isEmpty());
    }

    @Test
    void givenMultipleProperGames_whenStartingNewGames_thenAllGamesAreAdded() {
        int numberOfGames = 10;
        for (int i = 1; i <= numberOfGames; i++) {
            scoreBoard.startNewGame("Team" + (i * 2 - 1), "Team" + (i * 2));
        }

        Map<String, Match> ongoingGames = getOngoingGames();
        assertEquals(numberOfGames, ongoingGames.size());

        long lastTime = 0;
        for (int i = 1; i <= numberOfGames; i++) {
            String home = "Team" + (i * 2 - 1);
            String away = "Team" + (i * 2);
            Match match = ongoingGames.get(home + "-" + away);

            assertNotNull(match);
            assertNotEquals(lastTime, match.getStartTime());
            lastTime = match.getStartTime();
        }
    }

    @Test
    void givenOngoingGame_whenFinishingGame_thenGameIsRemoved() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

        assertTrue(getOngoingGames().isEmpty());
    }

    @Test
    void givenNonExistingGame_whenFinishingGame_thenThrowsException() {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));

        assertTrue(getOngoingGames().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenFinishingGame_thenExceptionIsThrown(String home, String away) {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.finishGame(home, away),
                ERR_INVALID_NAMES);

        assertTrue(getOngoingGames().isEmpty());
    }

    @Test
    void givenOngoingGame_whenFinishingTwice_thenThrowsException() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));

        assertTrue(getOngoingGames().isEmpty());
    }

    @Test
    void givenOngoingGame_whenUpdatingScore_thenScoreIsUpdated() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);
        scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3);

        Map<String, Match> ongoingGames = getOngoingGames();
        assertEquals(1, ongoingGames.size());

        Match match = ongoingGames.get(MATCH_KEY);
        assertMatchState(match, HOME_TEAM, AWAY_TEAM, 2, 3);
    }

    @Test
    void givenNonExistingGame_whenUpdatingScore_thenThrowsException() {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));

        assertTrue(getOngoingGames().isEmpty());
    }

    @Test
    void givenOngoingGame_whenUpdatingScoreWithNegativeValues_thenThrowsException() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);

        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, -1, 3),
                ERR_NEGATIVE_SCORE);

        assertEquals(1, getOngoingGames().size());
    }

    @Test
    void givenOngoingGame_whenUpdatingScoreTwice_thenScoreIsUpdatedCorrectly() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);
        scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3);
        scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 4, 5);

        Map<String, Match> ongoingGames = getOngoingGames();
        assertEquals(1, ongoingGames.size());

        assertMatchState(ongoingGames.get(MATCH_KEY), HOME_TEAM, AWAY_TEAM, 4, 5);
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenUpdatingScore_thenExceptionIsThrown(String home, String away) {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(home, away, 2, 3),
                ERR_INVALID_NAMES);

        assertTrue(getOngoingGames().isEmpty());
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
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void givenFinishedGame_whenUpdatingScore_thenExceptionIsThrown() {
        scoreBoard.startNewGame(HOME_TEAM, AWAY_TEAM);
        scoreBoard.finishGame(HOME_TEAM, AWAY_TEAM);

        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> scoreBoard.updateScore(HOME_TEAM, AWAY_TEAM, 2, 3),
                String.format(ERR_GAME_NOT_FOUND, HOME_TEAM, AWAY_TEAM));

        assertTrue(getOngoingGames().isEmpty());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Match> getOngoingGames() {
        return (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingMatches");
    }

    private void createGameWithScore(String home, String away, int homePts, int awayPts) {
        scoreBoard.startNewGame(home, away);
        scoreBoard.updateScore(home, away, homePts, awayPts);
    }

    private void assertMatchState(Match match, String expectedHome, String expectedAway, int expectedHomePts, int expectedAwayPts) {
        assertAll("Match State",
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