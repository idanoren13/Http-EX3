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
        Logger logger = loggersWrapper.getLogger(loggerName);
        if (logger == null) {
            return ResponseEntity.badRequest().body("Logger with name " + loggerName + " not found");
        }
        else {
            return ResponseEntity.ok(logger.getLevel().toString());
        }
    }

    @PutMapping("logs/level")
    public ResponseEntity setLogLevel(@RequestParam("logger-name") String loggerName, @RequestParam("level") String level) {
        Logger logger = loggersWrapper.getLogger(loggerName);
        if (logger == null || Level.getLevel(level) == null) {
            return ResponseEntity.badRequest().body("Logger with name " + loggerName + " not found");
        }
        else {
            loggersWrapper.setLoggerLevel(loggerName, level);
            return ResponseEntity.ok(logger.getLevel().toString());
        }
    }
}
