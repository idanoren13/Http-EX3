package server.Loggers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class LoggersWrapper {
    private static final Logger requestLogger = LogManager.getLogger("request-logger");
    private static final Logger stackLogger = LogManager.getLogger("stack-logger");
    private static final Logger independentLogger = LogManager.getLogger("independent-logger");

    String[] loggerNames = {"request-logger","stack-logger","independent-logger"};
    static int requestCounter = 1;

    public int getRequestCounter() {
        return requestCounter;
    }

    public void handleRequest(String resource, String verb) {
        requestLogger.info("Incoming request | #" + requestCounter + " | resource: " + resource +
                " | HTTP Verb " + verb.toUpperCase() + " | request #" + requestCounter);
        requestCounter++;
    }

    public void handleRequestDuration(long duration) {
        requestLogger.debug("request #" + (requestCounter - 1) + " duration: " + duration + "ms" + " | request #"
                + (requestCounter - 1));
    }

    public Logger getLogger(String loggerName) {
        List<?> names = Arrays.stream(loggerNames).toList();

        if (names.contains(loggerName)) {
            return LogManager.getLogger(loggerName);
        }

        return null;
    }

    public void setLoggerLevel(String loggerName, String level) {
        Configurator.setLevel(loggerName, level.toUpperCase());
    }
}
