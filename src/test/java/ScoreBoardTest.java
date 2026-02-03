import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;


import static ReflectionUtils.AdvancedGetter.getFieldValue;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreBoardTest {

    ScoreBoard scoreBoard;
    private final String homeTeam = "TeamA";
    private final String awayTeam = "TeamB";
    private final String expectedMatchKey = "TeamA - TeamB";


    @BeforeEach
    void setUp() {
        scoreBoard = new ScoreBoard();
    }

    @Test
    void givenNoExistingGame_whenStartingNewGame_thenScoreIsZeroAndStartTimeIsSet() {
        scoreBoard.startNewGame(homeTeam, awayTeam);

        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");
        Match match = ongoingGames.get(expectedMatchKey);

        assertEquals(1, ongoingGames.size());
        assertEquals(match, ongoingGames.get(expectedMatchKey));
        assertEquals(0, match.getHomeTeamPoints());
        assertEquals(0, match.getAwayTeamPoints());
        assertNotNull(match.getStartTime());
    }

    @Test
    void givenExistingGame_whenStartingNewGame_thenThrowsException() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.startNewGame(homeTeam, awayTeam);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(1, ongoingGames.size());
        assertNotNull(ongoingGames.get(expectedMatchKey));
        assertEquals("Game between TeamA and TeamB already exists.", exception.getMessage());
    }

    @Test
    void givenGameWithSameTeamNames_whenStartingNewGame_thenThrowsException() {
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.startNewGame(homeTeam, homeTeam);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("Team names must be different.", exception.getMessage());
    }

    @Test
    void givenGameWithOneTeamCurrentlyPlaying_whenStartingNewGame_thenThrowsException() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.startNewGame(homeTeam, "TeamC");
        });

        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(1, ongoingGames.size());
        assertEquals("One or two of the teams is already playing.", exception.getMessage());
    }


    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenStartingNewGame_thenExceptionIsThrown(String home, String away) {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> scoreBoard.startNewGame(home, away));
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("Team names cannot be null or empty", exception.getMessage());
    }

    @Test
    void givenMultipleProperGames_whenStartingNewGames_thenAllGamesAreAdded() {
        int numberOfGames = 10;
        for (int i = 1; i <= numberOfGames; i++) {
            String home = "Team" + (i * 2 - 1);
            String away = "Team" + (i * 2);
            scoreBoard.startNewGame(home, away);
        }

        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");
        assertEquals(numberOfGames, ongoingGames.size());

        Instant lastTime = null;
        for (int i = 1; i <= numberOfGames; i++) {
            String home = "Team" + (i * 2 - 1);
            String away = "Team" + (i * 2);
            String matchKey = home + " - " + away;
            Match match = ongoingGames.get(matchKey);

            assertNotNull(match);
            assertNotEquals(lastTime, match.getStartTime());
            lastTime = match.getStartTime();
        }
    }

    @Test
    void givenOngoingGame_whenFinishingGame_thenGameIsRemoved() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        scoreBoard.finishGame(homeTeam, awayTeam);

        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
    }

    @Test
    void givenNonExistingGame_whenFinishingGame_thenThrowsException() {
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.finishGame(homeTeam, awayTeam);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("No ongoing game between TeamA and TeamB found.", exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenFinishingGame_thenExceptionIsThrown(String home, String away) {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> scoreBoard.finishGame(home, away));
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("Team names cannot be null or empty", exception.getMessage());
    }

    @Test
    void givenOngoingGame_whenFinishingTwice_thenThrowsException() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        scoreBoard.finishGame(homeTeam, awayTeam);

        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.finishGame(homeTeam, awayTeam);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("No ongoing game between TeamA and TeamB found.", exception.getMessage());
    }

    @Test
    void givenOngoingGame_whenUpdatingScore_thenScoreIsUpdated() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        scoreBoard.updateScore(homeTeam, awayTeam, 2, 3);

        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");
        Match match = ongoingGames.get(expectedMatchKey);
        assertEquals(1, ongoingGames.size());


        assertEquals(2, match.getHomeTeamPoints());
        assertEquals(3, match.getAwayTeamPoints());
    }

    @Test
    void givenNonExistingGame_whenUpdatingScore_thenThrowsException() {
        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.updateScore(homeTeam, awayTeam, 2, 3);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("No ongoing game between TeamA and TeamB found.", exception.getMessage());
    }

    @Test
    void givenOngoingGame_whenUpdatingScoreWithNegativeValues_thenThrowsException() {
        scoreBoard.startNewGame(homeTeam, awayTeam);

        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.updateScore(homeTeam, awayTeam, -1, 3);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(1, ongoingGames.size());
        assertEquals("Score cannot be negative", exception.getMessage());
    }

    @Test
    void givenOngoingGame_whenUpdatingScoreTwice_thenScoreIsUpdatedCorrectly() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        scoreBoard.updateScore(homeTeam, awayTeam, 2, 3);
        scoreBoard.updateScore(homeTeam, awayTeam, 4, 5);

        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");
        Match match = ongoingGames.get(expectedMatchKey);
        assertEquals(1, ongoingGames.size());

        assertEquals(4, match.getHomeTeamPoints());
        assertEquals(5, match.getAwayTeamPoints());
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenUpdatingScore_thenExceptionIsThrown(String home, String away) {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore(home, away, 2, 3));
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("Team names cannot be null or empty", exception.getMessage());
    }

    @Test
    void givenMultipleOngoingGames_whenGettingSummary_thenCorrectOrderIsReturned() {
        scoreBoard.startNewGame("Team1", "Team2");
        scoreBoard.updateScore("Team1", "Team2", 1, 1);

        scoreBoard.startNewGame("Team3", "Team4");
        scoreBoard.updateScore("Team3", "Team4", 2, 2);

        scoreBoard.startNewGame("Team5", "Team6");
        scoreBoard.updateScore("Team5", "Team6", 0, 0);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());
        assertEquals("Team3", summary.get(0).getHomeTeam());
        assertEquals("Team4", summary.get(0).getAwayTeam());
        assertEquals(2, summary.get(0).getHomeTeamPoints());
        assertEquals(2, summary.get(0).getAwayTeamPoints());
        assertEquals("Team1", summary.get(1).getHomeTeam());
        assertEquals("Team2", summary.get(1).getAwayTeam());
        assertEquals(1, summary.get(1).getHomeTeamPoints());
        assertEquals(1, summary.get(1).getAwayTeamPoints());
        assertEquals("Team5", summary.get(2).getHomeTeam());
        assertEquals("Team6", summary.get(2).getAwayTeam());
        assertEquals(0, summary.get(2).getHomeTeamPoints());
        assertEquals(0, summary.get(2).getAwayTeamPoints());
    }


    @Test
    void givenMultipleOngoingGamesWithSameSumScore_whenGettingSummary_thenCorrectOrderIsReturned() {
        scoreBoard.startNewGame("Team1", "Team2");
        scoreBoard.updateScore("Team1", "Team2", 2, 2);

        scoreBoard.startNewGame("Team3", "Team4");
        scoreBoard.updateScore("Team3", "Team4", 1, 3);

        scoreBoard.startNewGame("Team5", "Team6");
        scoreBoard.updateScore("Team5", "Team6", 0, 4);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals(3, summary.size());

        assertEquals("Team5", summary.get(0).getHomeTeam());
        assertEquals("Team6", summary.get(0).getAwayTeam());
        assertEquals(0, summary.get(0).getHomeTeamPoints());
        assertEquals(4, summary.get(0).getAwayTeamPoints());
        assertEquals("Team3", summary.get(1).getHomeTeam());
        assertEquals("Team4", summary.get(1).getAwayTeam());
        assertEquals(1, summary.get(1).getHomeTeamPoints());
        assertEquals(3, summary.get(1).getAwayTeamPoints());
        assertEquals("Team1", summary.get(2).getHomeTeam());
        assertEquals("Team2", summary.get(2).getAwayTeam());
        assertEquals(2, summary.get(2).getHomeTeamPoints());
        assertEquals(2, summary.get(2).getAwayTeamPoints());
    }

    @Test
    void givenNoOngoingGames_whenGettingSummary_thenEmptyListIsReturned() {
        List<Match> summary = scoreBoard.getSummary();

        assertTrue(summary.isEmpty());
    }

    @Test
    void givenManyOngoingGamesWithVariousScores_whenGettingSummary_thenCorrectOrderIsReturned() {
        scoreBoard.startNewGame("TeamA", "TeamB");
        scoreBoard.updateScore("TeamA", "TeamB", 2, 3);

        scoreBoard.startNewGame("TeamC", "TeamD");
        scoreBoard.updateScore("TeamC", "TeamD", 4, 3);

        scoreBoard.startNewGame("TeamE", "TeamF");
        scoreBoard.updateScore("TeamE", "TeamF", 1, 1);

        scoreBoard.startNewGame("TeamG", "TeamH");
        scoreBoard.updateScore("TeamG", "TeamH", 5, 2);

        scoreBoard.startNewGame("TeamI", "TeamJ");
        scoreBoard.updateScore("TeamI", "TeamJ", 0, 0);

        scoreBoard.startNewGame("TeamK", "TeamL");
        scoreBoard.updateScore("TeamK", "TeamL", 1, 4);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals(6, summary.size());

        assertEquals("TeamG", summary.get(0).getHomeTeam());
        assertEquals("TeamH", summary.get(0).getAwayTeam());
        assertEquals(5, summary.get(0).getHomeTeamPoints());
        assertEquals(2, summary.get(0).getAwayTeamPoints());

        assertEquals("TeamC", summary.get(1).getHomeTeam());
        assertEquals("TeamD", summary.get(1).getAwayTeam());
        assertEquals(4, summary.get(1).getHomeTeamPoints());
        assertEquals(3, summary.get(1).getAwayTeamPoints());

        assertEquals("TeamK", summary.get(2).getHomeTeam());
        assertEquals("TeamL", summary.get(2).getAwayTeam());
        assertEquals(1, summary.get(2).getHomeTeamPoints());
        assertEquals(4, summary.get(2).getAwayTeamPoints());

        assertEquals("TeamA", summary.get(3).getHomeTeam());
        assertEquals("TeamB", summary.get(3).getAwayTeam());
        assertEquals(2, summary.get(3).getHomeTeamPoints());
        assertEquals(3, summary.get(3).getAwayTeamPoints());

        assertEquals("TeamE", summary.get(4).getHomeTeam());
        assertEquals("TeamF", summary.get(4).getAwayTeam());
        assertEquals(1, summary.get(4).getHomeTeamPoints());
        assertEquals(1, summary.get(4).getAwayTeamPoints());

        assertEquals("TeamI", summary.get(5).getHomeTeam());
        assertEquals("TeamJ", summary.get(5).getAwayTeam());
        assertEquals(0, summary.get(5).getHomeTeamPoints());
        assertEquals(0, summary.get(5).getAwayTeamPoints());
    }

    @Test
    void givenFinishedGame_whenGettingSummary_thenGameIsNotIncluded() {
        scoreBoard.startNewGame(homeTeam, awayTeam);
        scoreBoard.finishGame(homeTeam, awayTeam);

        List<Match> summary = scoreBoard.getSummary();

        assertTrue(summary.isEmpty());
    }

    @Test
    void givenFinishedGame_whenUpdatingScore_thenExceptionIsThrown(){
        scoreBoard.startNewGame(homeTeam, awayTeam);
        scoreBoard.finishGame(homeTeam, awayTeam);

        var exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            scoreBoard.updateScore(homeTeam, awayTeam, 2, 3);
        });
        HashMap<String, Match> ongoingGames = (HashMap<String, Match>) getFieldValue(scoreBoard, "ongoingGames");

        assertEquals(0, ongoingGames.size());
        assertEquals("No ongoing game between TeamA and TeamB found.", exception.getMessage());
    }


}








