package scoreboard;

public class Constants {

    public static final String KEY_SEPARATOR = "-";


    public static final String ERR_GAME_EXISTS = "Game between %s and %s already exists.";
    public static final String ERR_SAME_TEAMS = "Team names must be different.";
    public static final String ERR_TEAMS_PLAYING = "One or two of the teams is already playing.";
    public static final String ERR_INVALID_NAMES = "Team names cannot be null or empty";
    public static final String ERR_GAME_NOT_FOUND = "No ongoing game between %s and %s found.";
    public static final String ERR_NEGATIVE_SCORE = "Score cannot be negative";
    public static final String ERR_INVALID_TEAM_NAME_CHARACTERS = "Team names cannot contain the character '-'";

    private Constants() {
        // Prevent instantiation
    }
}
