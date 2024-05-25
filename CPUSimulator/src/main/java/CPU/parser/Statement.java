package CPU.parser;

public class Statement {
    public String operation;
    public String operand1;
    public String operand2;

    public Statement(String operation, String operand1, String operand2) {
        assert operation != null && operation.isBlank() : "Operation must not be null or empty";

        this.operation = operation;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
}
