package scoreboard.common;

import org.junit.jupiter.api.Test;
import scoreboard.common.Validators;

import static org.junit.jupiter.api.Assertions.*;
import static scoreboard.Constants.*;

class ValidatorsTest {

    @Test
    void givenValidNames_whenValidateNames_thenNoExceptionThrown() {
        assertDoesNotThrow(() -> Validators.validateNames("TeamA", "TeamB"));
    }

    @Test
    void givenNullHomeName_whenValidateNames_thenIllegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames(null, "TeamB"));
        assertEquals(ERR_INVALID_NAMES, exception.getMessage());
    }

    @Test
    void givenNullAwayName_whenValidateNames_thenIllegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames("TeamA", null));
        assertEquals(ERR_INVALID_NAMES, exception.getMessage());
    }

    @Test
    void givenEmptyHomeName_whenValidateNames_thenIllegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames("", "TeamB"));
        assertEquals(ERR_INVALID_NAMES, exception.getMessage());
    }

    @Test
    void givenEmptyAwayName_whenValidateNames_thenIllegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames("TeamA", ""));
        assertEquals(ERR_INVALID_NAMES, exception.getMessage());
    }

    @Test
    void givenSameTeamNames_whenValidateNames_thenIllegalArgumentExceptionThrown() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Validators.validateNames("TeamA", "TeamA"));
        assertEquals(ERR_SAME_TEAMS, exception.getMessage());
    }

}