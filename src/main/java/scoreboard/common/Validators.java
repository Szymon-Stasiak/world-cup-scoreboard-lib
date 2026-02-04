package scoreboard.common;

import static scoreboard.Constants.*;

public class Validators {
    public static void validateNames(String home, String away) {
        if (home == null || away == null || home.isBlank() || away.isBlank()) {
            throw new IllegalArgumentException(ERR_INVALID_NAMES);
        }
        if (home.equals(away)) {
            throw new IllegalArgumentException(ERR_SAME_TEAMS);
        }
        if(home.contains(KEY_SEPARATOR) || away.contains(KEY_SEPARATOR)) {
            throw new IllegalArgumentException(ERR_INVALID_TEAM_NAME_CHARACTERS);
        }
    }

    private Validators() {
        // Prevent instantiation
    }
}
