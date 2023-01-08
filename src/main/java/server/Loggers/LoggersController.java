package server.Loggers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggersController {
    private final LoggersWrapper loggersWrapper;

    public LoggersController(LoggersWrapper loggersWrapper) {
        this.loggersWrapper = loggersWrapper;
    }

    @GetMapping("logs/level")
    public ResponseEntity getLogLevel(@RequestParam("logger-name") String loggerName) {
        loggersWrapper.handleRequest("/logs/level", "GET");
        long timeStart = System.currentTimeMillis();
        Logger logger = loggersWrapper.getLogger(loggerName);
        ResponseEntity response;
        if (logger == null) {
            response = ResponseEntity.badRequest().body("Logger with name " + loggerName + " not found");
        }
        else {
            response = ResponseEntity.ok(logger.getLevel().toString());
        }

        loggersWrapper.handleRequestDuration(System.currentTimeMillis() - timeStart);

        return response;
    }

    @PutMapping("logs/level")
    public ResponseEntity setLogLevel(@RequestParam("logger-name") String loggerName, @RequestParam("logger-level") String level) {
        loggersWrapper.handleRequest("/logs/level", "PUT");
        Logger logger = loggersWrapper.getLogger(loggerName);
        long timeStart = System.currentTimeMillis();
        ResponseEntity response;
        if (logger == null || Level.getLevel(level) == null) {
            response = ResponseEntity.badRequest().body("Logger with name " + loggerName + " not found");
        }
        else {
            loggersWrapper.setLoggerLevel(loggerName, level);
            response = ResponseEntity.ok(logger.getLevel().toString());
        }

        loggersWrapper.handleRequestDuration(System.currentTimeMillis() - timeStart);

        return response;
    }
}
