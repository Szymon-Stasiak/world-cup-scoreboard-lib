package common;

public class KeyGenerator {

    public static String generateKey(String home, String away) {
        return home + "-" + away;
    }
}
