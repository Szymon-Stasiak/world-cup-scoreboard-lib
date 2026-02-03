import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private Match match;
    private final String homeTeam = "TeamA";
    private final String awayTeam = "TeamB";

    @BeforeEach
    void setUp() {
        match = new Match(homeTeam, awayTeam);
    }

    @Test
    void givenNewMatch_whenCreated_thenScoresAreZeroAndStartTimeIsSet() {
        assertEquals(0, match.getHomeTeamPoints());
        assertEquals(0, match.getAwayTeamPoints());
        assertNotNull(match.getStartTime());
    }

    @Test
    void givenMatch_whenGettingTeams_thenCorrectNamesAreReturned() {
        assertEquals(homeTeam, match.getHomeTeam());
        assertEquals(awayTeam, match.getAwayTeam());
    }

    @ParameterizedTest
    @MethodSource("validScoreCases")
    void givenValidScores_whenUpdateScore_thenScoresAreUpdatedCorrectly(int home, int away) {
        match.updateScore(home, away);

        assertEquals(home, match.getHomeTeamPoints());
        assertEquals(away, match.getAwayTeamPoints());
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

        assertEquals(0, match.getHomeTeamPoints());
        assertEquals(0, match.getAwayTeamPoints());
    }

    @ParameterizedTest
    @MethodSource("totalScoreCases")
    void givenUpdatedScores_whenGettingTotal_thenCorrectTotalIsReturned(
            int home,
            int away,
            int expectedTotal
    ) {
        match.updateScore(home, away);

        assertEquals(expectedTotal, match.getTotalScore());
    }


    @Test
    void givenMatch_whenCallingToString_thenProperFormatIsReturned() {
        assertEquals("TeamA - TeamB", match.toString());
    }

    @Test
    void givenScoreUpdate_whenUpdatingScore_thenStartTimeDoesNotChange() {

        var startTime = match.getStartTime();

        match.updateScore(1, 1);

        assertEquals(startTime, match.getStartTime());
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 2",
            "2, -1",
            "-1, -1"
    })
    void givenNegativeScore_whenUpdatingScore_thenExceptionIsThrown(int home, int away) {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> match.updateScore(home, away)
        );

        assertEquals("Score cannot be negative", exception.getMessage());
    }


    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidTeamNames_whenCreatingMatch_thenExceptionIsThrown(String home, String away) {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Match(home, away)
        );

        assertEquals("Team names cannot be null or empty", exception.getMessage());
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


}
