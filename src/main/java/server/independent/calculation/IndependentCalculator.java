package server.independent.calculation;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.exceptions.DivisionByZeroException;
import server.exceptions.NegativeFactorialException;
import server.exceptions.NotEnoughArgumentsException;
import server.exceptions.TooManyArgumentsException;
import server.models.IndependentJSONObject;
import server.models.Operations;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
public class IndependentCalculator {
    @PatchMapping(value = "/independent/calculate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity calculate(@RequestBody IndependentJSONObject jsonObject) {
        int[] arguments = jsonObject.arguments();
        String operation = jsonObject.operation();
        int result = 0;
        try {
            switch (Operations.valueOf(operation)) {
                case PLUS:
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Plus");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Plus");
                    }
                    for (int argument : arguments) {
                        result += argument;
                    }
                    break;
                case MINUS:
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Minus");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Minus");
                    }
                    for (int argument : arguments) {
                        result -= argument;
                    }
                    break;
                case TIMES:
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Times");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Times");
                    }
                    result = 1;
                    for (int argument : arguments) {
                        result *= argument;
                    }
                    break;
                case DIVIDE:
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Divide");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Divide");
                    }
                    if (arguments[1] == 0) {
                        throw new DivisionByZeroException();
                    }
                    result = 1;
                    for (int argument : arguments) {
                        result /= argument;
                    }
                    break;
                case POW:
                    if (arguments.length < 2) {
                        throw new NotEnoughArgumentsException("Pow");
                    }
                    if (arguments.length > 2) {
                        throw new TooManyArgumentsException("Pow");
                    }
                    result = 1;
                    for (int argument : arguments) {
                        result = (int) Math.pow(result, argument);
                    }
                    break;
                case ABS:
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
                case FACT:
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

    private int factorial(int argument) {
        if (argument == 0)
            return 1;
        else
            return argument * factorial(argument - 1);
    }
}
