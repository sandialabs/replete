package replete.logging;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggingInitializer {
    public static final String SYSTEM_LOG_DIR_PROP_NAME = "LogDir";       // Same one used by Log4j currently

    public static void init() {
        init(null, null);
    }
    public static void init(String propertiesPath) {
        init(propertiesPath, null);
    }
    public static void init(String propertiesPath, File possibleLogDirPath) {
        String logDir = ".";       // Start with this as the default
        if(possibleLogDirPath != null) {
            possibleLogDirPath.mkdirs();
            if(possibleLogDirPath.exists()) {
                logDir = possibleLogDirPath.getAbsolutePath();
            }
        }

        // Set the log dir path to a JVM system path so that the
        // Log4j (1.2.17) library can access it for dynamic log paths
        // during the calls below to PropertyConfigurator.configure(...).
        System.setProperty(SYSTEM_LOG_DIR_PROP_NAME, logDir);

        if(new File("log4j.properties").exists()) {
            PropertyConfigurator.configure("log4j.properties");

        } else if(propertiesPath != null) {
            URL url = Thread.currentThread().getContextClassLoader().getResource(propertiesPath);
            PropertyConfigurator.configure(url);

        } else {
            URL url = LoggingInitializer.class.getResource("null-log4j.properties");
            PropertyConfigurator.configure(url);
        }

        registerAppendersStatically();
    }

    private static void registerAppendersStatically() {
        List<Logger> allLoggers = new ArrayList<>();
        allLoggers.add(Logger.getRootLogger());
        Enumeration loggers = Logger.getRootLogger().getLoggerRepository().getCurrentLoggers();
        while(loggers.hasMoreElements()) {
            Logger logger = (Logger) loggers.nextElement();
            allLoggers.add(logger);
        }
        for(Logger logger : allLoggers) {
            Enumeration apps = logger.getAllAppenders();
            while(apps.hasMoreElements()) {
                Appender app = (Appender) apps.nextElement();
                if(app instanceof EventCountingAppender) {
                    EventCountingAppender eac = (EventCountingAppender) app;
                    EventCountingAppender.setInstance(app.getName(), eac);
                }
            }
        }
    }
}
