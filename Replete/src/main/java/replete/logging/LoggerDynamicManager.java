package replete.logging;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LoggerDynamicManager {
    private Map<Class<?>, Logger> loggers = new HashMap<>();
    public synchronized Logger getLogger(Class<?> clazz) {
        Logger logger = loggers.get(clazz);
        if(logger == null) {
            logger = Logger.getLogger(clazz);
            loggers.put(clazz, logger);
        }
        return logger;
    }
//    public void printLoggers() {
//        for(Class<?> clazz : loggers.keySet()) {
//            System.out.println(clazz.getSimpleName() + " => " + loggers.get(clazz).getName());
//        }
//    }
}
