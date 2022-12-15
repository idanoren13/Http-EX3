package server.independent.calculation;

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

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
public class IndependentCalculator {
    @PostMapping(value = "/independent/calculate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity calculate(@RequestBody IndependentJSONObject jsonObject) {
        int[] arguments = jsonObject.arguments();
        String operation = jsonObject.operation();
        int result = 0;
        try {
            switch (operation) {
                case "PLUS":
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Plus");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Plus");
                    }
                    result = arguments[0] + arguments[1];
                    break;
                case "MINUS":
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Minus");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Minus");
                    }
                    result = arguments[0] - arguments[1];
                    break;
                case "TIMES":
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Times");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Times");
                    }
                    result = arguments[0] * arguments[1];
                    break;
                case "DIVIDE":
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
                    break;
                case "POW":
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Pow");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Pow");
                    }
                    result = (int) Math.pow(arguments[0], arguments[1]);
                    break;
                case "ABS":
                    if (arguments.length < 1) {
                        throw new NotEnoughArgumentsException("Abs");
                    }
                    if (arguments.length > 1) {
                        throw new TooManyArgumentsException("Abs");
                    }
                    for (int argument : arguments) {
                        result = Math.abs(argument);
                    }
                    break;
                case "FACT":
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
                    break;
                default:
                    return ResponseEntity.status(CONFLICT).body("Error: unknown operation: " + operation);
            }
        } catch (TooManyArgumentsException e) {
            return ResponseEntity.status(CONFLICT).body("Error: Too many arguments for the operation " + e.getMessage());
        } catch (NotEnoughArgumentsException e) {
            return ResponseEntity.status(CONFLICT).body("Error: Not enough arguments for the operation " + e.getMessage());
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            return ResponseEntity.status(CONFLICT).body(e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}
