package server.models;

public record IndependentJSONObject(int[] arguments, String operation) {
    public IndependentJSONObject (int[] arguments, String operation) {
        this.arguments = arguments;
        this.operation = operation.toUpperCase();
    }
}
