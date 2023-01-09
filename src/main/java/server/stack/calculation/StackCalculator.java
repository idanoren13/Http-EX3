package server.stack.calculation;

import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.Loggers.LoggersWrapper;
import server.exceptions.DivisionByZeroException;
import server.exceptions.NegativeFactorialException;
import server.exceptions.NotEnoughArgumentsException;
import server.models.IndependentJSONObject;

import java.util.*;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
public class StackCalculator {
    Deque<Integer> stack = new ArrayDeque<>();
    private final LoggersWrapper requestLogger;
    private final Logger stackLogger;

    public StackCalculator(LoggersWrapper requestLogger) {
        this.requestLogger = requestLogger;
        this.stackLogger = requestLogger.getLogger("stack-logger");
    }


    @GetMapping("/stack/size")
    public ResponseEntity size() {
        requestLogger.handleRequest("/stack/size", "GET");
        stackLogger.info("Stack size is: " + stack.size() + " | request #" + (requestLogger.getRequestCounter() - 1));
        stackLogger.debug("Stack content (first == top): " +
                List.of(stack.toArray()).toString() + " | request #" + (requestLogger.getRequestCounter() - 1));
//                 + " | request #" + (requestLogger.getRequestCounter() - 1));
        long timeStart = System.currentTimeMillis();
        requestLogger.handleRequestDuration(System.currentTimeMillis() - timeStart);

        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    private String getStackArguments() {
        StringBuilder result = new StringBuilder();
        for (Integer integer : stack) {
            result.insert(0, integer).insert(0, ", ");
//            result.append(integer).append(",");
        }

        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    @PutMapping(value = "/stack/arguments", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity push(@RequestBody IndependentJSONObject jsonObject) {
        requestLogger.handleRequest("/stack/arguments", "PUT");
        long timeStart = System.currentTimeMillis();
        int[] arguments = jsonObject.arguments();

        for (int argument : arguments) {
            stack.push(argument);
        }

        requestLogger.handleRequestDuration(System.currentTimeMillis() - timeStart);
        stackLogger.info("Adding total of " + arguments.length + " argument(s) to the stack | Stack size: " + stack.size() + " | request #" + (requestLogger.getRequestCounter() - 1));
        stackLogger.debug("Adding arguments: " + Arrays.toString(arguments) + " | Stack size before " + (stack.size() - arguments.length) + " | stack size after " + stack.size() + " | request #" + (requestLogger.getRequestCounter() - 1));

        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    @GetMapping(value = "/stack/operate")
    public ResponseEntity operate(@RequestParam String operation) {
        requestLogger.handleRequest("/stack/operate", "GET");
        long timeStart = System.currentTimeMillis();
        Integer result = 0;
        Integer first = null;
        Integer second = null;

        try {
            switch (operation.toUpperCase()) {
                case "PLUS":
                    if (stack.size() < 2) {
                        throw new NotEnoughArgumentsException("Plus", 2);
                    }
                    first = stack.pop();
                    second = stack.pop();
                    result = first + second;
                    break;
                case "MINUS":
                    if (stack.size() < 2) {
                        throw new NotEnoughArgumentsException("Minus", 2);
                    }
                    first = stack.pop();
                    second = stack.pop();
                    result = first - second;
                    break;
                case "TIMES":
                    if (stack.size() < 2) {
                        throw new NotEnoughArgumentsException("Times", 2);
                    }
                    first = stack.pop();
                    second = stack.pop();
                    result = first * second;
                    break;
                case "DIVIDE":
                    if (stack.size() < 2) {
                        throw new NotEnoughArgumentsException("Divide", 2);
                    }

                    first = stack.pop();
                    second = stack.pop();

                    if (second == 0) {
                        throw new DivisionByZeroException();
                    }
                    result = first / second;
                    break;
                case "POW":
                    if (stack.size() < 2) {
                        throw new NotEnoughArgumentsException("Pow", 2);
                    }
                    first = stack.pop();
                    second = stack.pop();
                    result = (int) Math.pow(first, second);
                    break;
                case "ABS":
                    if (stack.size() < 1) {
                        throw new NotEnoughArgumentsException("Abs", 1);
                    }
                    result = Math.abs(stack.pop());
                    break;
                case "FACT":
                    if (stack.size() < 1) {
                        throw new NotEnoughArgumentsException("Factorial", 1);
                    }
                    first = stack.pop();
                    if (first < 0) {
                        stack.push(first);
                        throw new NegativeFactorialException();
                    }
                    result = 1;
                    for (int i = 1; i <= first; i++) {
                        result *= i;
                    }

                    break;
                default:
                    return ResponseEntity.status(CONFLICT).body(Map.of("error-message", "Error: unknown operation: " + operation));
            }
        } catch (NotEnoughArgumentsException e) {
            String errorMessage = "Error: cannot implement operation " + e.getMessage() + ". It requires "
                    + e.getNumberOfRequiredArguments() + " arguments and the stack has only " + stack.size() + " arguments";
            stackLogger.error("Server encountered an error ! message: " + errorMessage + " | request #" + (requestLogger.getRequestCounter() - 1));
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", errorMessage));
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            stackLogger.error("Server encountered an error ! message: " + e.getMessage() + " | request #" + (requestLogger.getRequestCounter() - 1));
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", e.getMessage()));
        }

        requestLogger.handleRequestDuration(System.currentTimeMillis() - timeStart);
        stackLogger.info("Performing operation " + operation + "." + " Result is " + result + " | stack size: " + stack.size() + " | request #" + (requestLogger.getRequestCounter() - 1));
        stackLogger.debug("“Performing operation: " + operation + "(" + getUsedArguments(first, second) + ") = " + result + " | request #" + (requestLogger.getRequestCounter() - 1));

        return ResponseEntity.ok(Map.of("result", result));
    }

    private String getUsedArguments(Integer first, Integer second) {
        return null == second ? first.toString() : first + "," + second;
    }

    @DeleteMapping("/stack/arguments")
    public ResponseEntity delete(@RequestParam int count) {
        requestLogger.handleRequest("/stack/arguments", "DELETE");
        long timeStart = System.currentTimeMillis();

        if (count > stack.size()) {
            String errorMessage = "Error: cannot remove " + count + " from the stack. It has only " + stack.size() + " arguments";
            stackLogger.error("Server encountered an error ! message: " + errorMessage + " | request #" + (requestLogger.getRequestCounter() - 1));
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message", "error-message"));
        }

        for (int i = 0; i < count; i++) {
            stack.pop();
        }

        requestLogger.handleRequestDuration(System.currentTimeMillis() - timeStart);
        stackLogger.info("Removing total " + count + "argument(s) from the stack | Stack size: " + stack.size() + " | request #" + (requestLogger.getRequestCounter() - 1));
        return ResponseEntity.ok(Map.of("result", stack.size()));
    }
}
