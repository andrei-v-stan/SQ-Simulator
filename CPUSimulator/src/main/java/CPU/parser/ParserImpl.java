package CPU.parser;

import CPU.Info.InstructionSet;
import MEMO.InstructionMemory;
import UTILS.CustomException;

public class ParserImpl implements Parser {

    @Override
    public void loadInstructionMemory(InstructionMemory instructionMemory, String sourceCode) throws CustomException {
        assert instructionMemory != null : "Instruction memory must be initialized";
        assert sourceCode != null : "Source code must not be null";

        var code = parse(sourceCode);
        for (var statement : code.statements) {
            var bytes = code.toBytes(statement);
            instructionMemory.writeInstruction(bytes);
        }
    }

    public Code parse(String sourceCode) throws CustomException {
        assert sourceCode != null : "Source code must not be null";

        var lines = sourceCode.split("\n");
        var code = new Code();
        int lineNumber = 0;

        for (var line : lines) {
            var rawInstruction = getRawInstruction(line, lineNumber);
            code.addRawInstruction(rawInstruction);
            lineNumber++;
        }

        assert !code.statements.isEmpty() : "Code must not be empty";

        return code;
    }

    public Statement getRawInstruction(String line, int lineNumber) throws CustomException {
        assert line != null && !line.isBlank() : "Line must not be null or empty";
        assert lineNumber >= 0 : "Line number must not be negative";

        var tokens = line.split("\\s+");
        if (tokens.length > 3 || tokens.length < 1) {
            throw new CustomException("Invalid instruction syntax, line: " + lineNumber);
        }

        var operation = tokens[0].toUpperCase();
        String operand1 = null, operand2 = null;

        if (tokens.length >= 2) {
            operand1 = tokens[1];
            if (tokens.length == 3) {
                operand2 = tokens[2];
                operand1 = operand1.replaceAll(",$", "");
            }
        }

        assert InstructionSet.searchByName(operation) != null : "No operation found with name '" + operation + "'";

        return new Statement(operation, operand1, operand2);
    }
}
