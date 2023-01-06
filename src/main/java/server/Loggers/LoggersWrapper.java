package server.Loggers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggersWrapper {
    private static final Logger requestLogger = LogManager.getLogger("request-logger");
    private static final Logger stackLogger = LogManager.getLogger("stack-logger");
    private static final Logger independentLogger = LogManager.getLogger("independent-logger");

    static int requestCounter = 1;

    public Boolean isRequestDebugEnabled() {
        return requestLogger.isDebugEnabled();
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
        return LogManager.getLogger(loggerName);
    }

    public void setLoggerLevel(String loggerName, String level) {
        Configurator.setLevel(loggerName,level.toUpperCase());
    }
}
