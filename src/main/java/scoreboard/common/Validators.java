package scoreboard.common;

import static scoreboard.Constants.ERR_INVALID_NAMES;
import static scoreboard.Constants.ERR_SAME_TEAMS;

public class Validators {
    public static void validateNames(String home, String away) {
        if (home == null || away == null || home.isBlank() || away.isBlank()) {
            throw new IllegalArgumentException(ERR_INVALID_NAMES);
        }
        if (home.equals(away)) {
            throw new IllegalArgumentException(ERR_SAME_TEAMS);
        }
    }
}
