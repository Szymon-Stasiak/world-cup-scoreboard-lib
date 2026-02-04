package scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static scoreboard.Constants.ERR_NEGATIVE_SCORE;

class MatchTest {

    private Match match;

    private static final String HOME_TEAM = "TeamA";
    private static final String AWAY_TEAM = "TeamB";

    @BeforeEach
    void setUp() {
        match = new Match(HOME_TEAM, AWAY_TEAM);
    }

    @Test
    void givenNewMatch_whenCreated_thenScoresAreZeroAndStartTimeIsSet() {
        assertMatchScore(match, 0, 0);
    }

    @Test
    void givenMatch_whenGettingTeams_thenCorrectNamesAreReturned() {
        assertEquals(HOME_TEAM, match.getHomeTeam());
        assertEquals(AWAY_TEAM, match.getAwayTeam());
    }

    @ParameterizedTest
    @MethodSource("validScoreCases")
    void givenValidScores_whenUpdateScore_thenScoresAreUpdatedCorrectly(int home, int away) {
        match.updateScore(home, away);
        assertMatchScore(match, home, away);
    }

    @Test
    void givenMultipleScoreUpdates_whenCalculatingTotal_thenResultIsConsistent() {
        match.updateScore(1, 1);
        match.updateScore(2, 2);

        assertEquals(4, match.getTotalScore());
    }

    @Test
    void givenZeroScores_whenUpdateScore_thenScoresRemainZero() {
        match.updateScore(0, 0);
        assertMatchScore(match, 0, 0);
    }

    @ParameterizedTest
    @MethodSource("totalScoreCases")
    void givenUpdatedScores_whenGettingTotal_thenCorrectTotalIsReturned(int home, int away, int expectedTotal) {
        match.updateScore(home, away);

        assertEquals(expectedTotal, match.getTotalScore());
    }

    @Test
    void givenScoreUpdate_whenUpdatingScore_thenStartTimeDoesNotChange() {
        var initialStartTime = match.getStartTime();

        match.updateScore(1, 1);

        assertEquals(initialStartTime, match.getStartTime());
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 2",
            "2, -1",
            "-1, -1"
    })
    void givenNegativeScore_whenUpdatingScore_thenExceptionIsThrown(int home, int away) {
        assertThrowsWithMessage(IllegalArgumentException.class,
                () -> match.updateScore(home, away),
                ERR_NEGATIVE_SCORE);
    }

    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenCreatingMatch_thenExceptionIsThrown(String home, String away) {
        assertThrows(IllegalArgumentException.class, () ->
                new Match(home, away)
        );
    }

    @Test
    void copyConstructor_shouldCreateIndependentCopy() {
        Match original = new Match(HOME_TEAM, AWAY_TEAM);
        original.updateScore(2, 1);

        Match copy = new Match(original);

        assertEquals(original.getHomeTeam(), copy.getHomeTeam());
        assertEquals(original.getAwayTeam(), copy.getAwayTeam());
        assertEquals(original.getHomeTeamPoints(), copy.getHomeTeamPoints());
        assertEquals(original.getAwayTeamPoints(), copy.getAwayTeamPoints());
        assertEquals(original.getStartTime(), copy.getStartTime());

        assertNotSame(original, copy);

        copy.updateScore(5, 5);
        assertEquals(2, original.getHomeTeamPoints());
        assertEquals(1, original.getAwayTeamPoints());
    }

    static Stream<Arguments> validScoreCases() {
        return Stream.of(
                Arguments.of(3, 2),
                Arguments.of(0, 0),
                Arguments.of(10, 0),
                Arguments.of(0, 7),
                Arguments.of(5, 5),
                Arguments.of(100, 200),
                Arguments.of(Integer.MAX_VALUE, 0)
        );
    }

    static Stream<Arguments> totalScoreCases() {
        return Stream.of(
                Arguments.of(3, 2, 5),
                Arguments.of(0, 0, 0),
                Arguments.of(10, 0, 10),
                Arguments.of(0, 7, 7),
                Arguments.of(5, 5, 10),
                Arguments.of(100, 200, 300),
                Arguments.of(Integer.MAX_VALUE, 0, Integer.MAX_VALUE)
        );
    }

    private void assertMatchScore(Match match, int expectedHome, int expectedAway) {
        assertAll("scoreboard.Match score",
                () -> assertEquals(expectedHome, match.getHomeTeamPoints()),
                () -> assertEquals(expectedAway, match.getAwayTeamPoints())
        );
    }

    private <T extends Throwable> void assertThrowsWithMessage(Class<T> expectedType, Executable executable, String expectedMessage) {
        T exception = assertThrows(expectedType, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}