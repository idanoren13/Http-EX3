package server.models;

public record IndependentJSONObject(int[] arguments, String operation) {
    public IndependentJSONObject (int[] arguments, String operation) {
        this.arguments = arguments;
        String temp = operation;
        if (operation != null) {
            temp = operation.toUpperCase();
        }
        this.operation = temp;
    }

}
