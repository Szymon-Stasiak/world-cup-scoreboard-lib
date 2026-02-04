package scoreboard.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static scoreboard.Constants.*;

class ValidatorsTest {


    @ParameterizedTest
    @MethodSource("fixtures.DaraProviders#invalidTeamNames")
    void givenInvalidNames_whenValidateNames_thenIllegalArgumentExceptionThrown(String home, String away) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames(home, away));
        String expectedMessage = (home == null || home.isBlank() || away == null || away.isBlank())
                ? ERR_INVALID_NAMES : ERR_SAME_TEAMS;
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void givenInvalidNameWithKeySeparator_whenValidateNames_thenIllegalArgumentExceptionThrown() {
        String home = "Team-A";
        String away = "TeamB";
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames(home, away));
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames(away, home));

        assertEquals(ERR_INVALID_TEAM_NAME_CHARACTERS, exception1.getMessage());
        assertEquals(ERR_INVALID_TEAM_NAME_CHARACTERS, exception2.getMessage());
    }

}