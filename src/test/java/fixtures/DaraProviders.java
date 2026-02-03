package fixtures;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class DaraProviders {
    static Stream<Arguments> invalidTeamNames() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(null, "TeamB"),
                org.junit.jupiter.params.provider.Arguments.of("", "TeamB"),
                org.junit.jupiter.params.provider.Arguments.of("TeamA", null),
                org.junit.jupiter.params.provider.Arguments.of("TeamA", "")
        );
    }
}
