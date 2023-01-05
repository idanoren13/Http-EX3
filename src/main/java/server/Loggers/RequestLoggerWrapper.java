package server.Loggers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestLoggerWrapper {
    private static final Logger requestLogger = LogManager.getLogger("request-logger");

    static int requestCounter = 1;

    public Boolean isDebugEnabled() {
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

}
