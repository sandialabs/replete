package replete.logging;

import java.util.HashMap;
import java.util.Map;

import replete.collections.Pair;

public class LogCodeDynamicManager {


    ////////////
    // FIELDS //
    ////////////

    private Map<Pair<LogCodeDynamic, Class<?>>, LogCode> constructedCodes = new HashMap<>();


    ////////////
    // CREATE //
    ////////////

    public static LogCodeDynamic create(String category, String code, String description) {
        return new LogCodeDynamic(category, code, description);
    }
    public static LogCodeDynamic create(String category, String code, String description, boolean important) {
        return new LogCodeDynamic(category, code, description, important);
    }


    /////////
    // GET //
    /////////

    // Still ultimately relies on LogCodeManager to keep track of all generated log codes.
    public synchronized LogCode getCode(LogCodeDynamic codeCreator, Class<?> clazz) {
        Pair<LogCodeDynamic, Class<?>> pair = new Pair<>(codeCreator, clazz);
        LogCode code = constructedCodes.get(pair);
        if(code == null) {
            code = codeCreator.create(clazz);
            constructedCodes.put(pair, code);
        }
        return code;
    }
}
