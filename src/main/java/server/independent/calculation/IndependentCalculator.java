package server.independent.calculation;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.exceptions.DivisionByZeroException;
import server.exceptions.NegativeFactorialException;
import server.exceptions.NotEnoughArgumentsException;
import server.exceptions.TooManyArgumentsException;
import server.models.IndependentJSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
public class IndependentCalculator {
    @PostMapping(value = "/independent/calculate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> calculate(@RequestBody IndependentJSONObject jsonObject) {
        int[] arguments = jsonObject.arguments();
        String operation = jsonObject.operation();
        int result = 0;
        Map<String, Object> resultObject = new HashMap<>();

        try {
            switch (operation) {
                case "PLUS" -> {
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Plus");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Plus");
                    }
                    result = arguments[0] + arguments[1];
                }
                case "MINUS" -> {
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Minus");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Minus");
                    }
                    result = arguments[0] - arguments[1];
                }
                case "TIMES" -> {
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Times");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Times");
                    }
                    result = arguments[0] * arguments[1];
                }
                case "DIVIDE" -> {
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Divide");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Divide");
                    }
                    if (arguments[1] == 0) {
                        throw new DivisionByZeroException();
                    }
                    result = arguments[0] / arguments[1];
                }
                case "POW" -> {
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Pow");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Pow");
                    }
                    result = (int) Math.pow(arguments[0], arguments[1]);
                }
                case "ABS" -> {
                    if (arguments.length < 1) {
                        throw new NotEnoughArgumentsException("Abs");
                    }
                    if (arguments.length > 1) {
                        throw new TooManyArgumentsException("Abs");
                    }
                    for (int argument : arguments) {
                        result = Math.abs(argument);
                    }
                }
                case "FACT" -> {
                    if (arguments.length < 1) {
                        throw new NotEnoughArgumentsException("Factorial");
                    }
                    if (arguments.length == 1) {
                        if (arguments[0] < 0) {
                            throw new NegativeFactorialException();
                        }
                        result = 1;
                        for (int i = 1; i <= arguments[0]; i++) {
                            result *= i;
                        }
                    } else {
                        throw new TooManyArgumentsException("Factorial");
                    }
                }
                default -> {
                    resultObject.put("error-message", "Error: unknown operation: " + operation);
                    return ResponseEntity.status(CONFLICT).body(resultObject);
                }
            }
        } catch (TooManyArgumentsException e) {
            resultObject.put("error-message","Error: Too many arguments for the operation " + e.getMessage());
            return ResponseEntity.status(CONFLICT).body(resultObject);
        } catch (NotEnoughArgumentsException e) {
            resultObject.put("error-message","Error: Not enough arguments for the operation " + e.getMessage());
            return ResponseEntity.status(CONFLICT).body(resultObject);
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            resultObject.put("error-message", e.getMessage());
            return ResponseEntity.status(CONFLICT).body(resultObject);
        }

        resultObject.put("result", result);

        return ResponseEntity.ok(resultObject);
    }
}
