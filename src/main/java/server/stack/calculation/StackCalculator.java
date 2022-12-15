package server.stack.calculation;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.exceptions.DivisionByZeroException;
import server.exceptions.NegativeFactorialException;
import server.exceptions.NotEnoughArgumentsException;
import server.models.IndependentJSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestController
public class StackCalculator {
    Stack<Integer> stack = new Stack<>();

    @GetMapping("/stack/size")
    public ResponseEntity size() {
        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    @PutMapping(value = "/stack/arguments", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity push(@RequestBody IndependentJSONObject jsonObject) {
        int[] arguments = jsonObject.arguments();

        for (int argument : arguments) {
            stack.push(argument);
        }

        return ResponseEntity.ok(Map.of("result", stack.size()));
    }

    @GetMapping(value = "/stack/operate")
    public ResponseEntity operate(@RequestParam String operation) {
        int result = 0;
        int first;
        int second;

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
                    return ResponseEntity.status(CONFLICT).body(Map.of("error-message","Error: unknown operation: " + operation));
            }
        } catch (NotEnoughArgumentsException e) {
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message","Error: cannot implement operation " + e.getMessage() + ". It requires "
                    + e.getNumberOfRequiredArguments() + " arguments and the stack has only " + stack.size() + " arguments"));
        } catch (NegativeFactorialException | DivisionByZeroException e) {
            return ResponseEntity.status(CONFLICT).body(Map.of("error-message",e.getMessage()));
        }

        return ResponseEntity.ok(Map.of("result", result));
    }

    @DeleteMapping("/stack/arguments")
    public ResponseEntity delete(@RequestParam int count) {
        if (count > stack.size()) {
            return ResponseEntity.status(CONFLICT).body("Error: cannot remove " + count + " from the stack. It has only " + stack.size() + " arguments");
        }

        for (int i = 0; i < count; i++) {
            stack.pop();
        }

        return ResponseEntity.ok(Map.of("result", stack.size()));
    }
}
