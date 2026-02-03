package common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyGeneratorTest {

    @Test
    void givenTeamNames_whenGenerateKey_thenCorrectKeyIsReturned() {
        String homeTeam = "TeamA";
        String awayTeam = "TeamB";
        String expectedKey = "TeamA-TeamB";

        String actualKey = KeyGenerator.generateKey(homeTeam, awayTeam);

        assertEquals(expectedKey, actualKey);
    }

}