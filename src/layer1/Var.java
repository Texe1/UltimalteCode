package layer1;

import java.util.Map;

public record Var(String name, Map<String, Object> members) {
    // TODO remove name, var enum with map

    Object get(String s) {
        return members.get(s);
    }
}
