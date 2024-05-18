package CPU.parser;

import CPU.Info.InstructionSet;
import MEMO.InstructionMemory;
import UTILS.CustomException;

public class ParserImpl implements Parser {

    @Override
    public void loadInstructionMemory(InstructionMemory instructionMemory, String sourceCode) throws CustomException {
        var code = parse(sourceCode);
        for (var statement : code.statements) {
            var bytes = code.toBytes(statement);
            instructionMemory.writeInstruction(bytes);
        }
    }

    public Code parse(String sourceCode) throws CustomException {
        var lines = sourceCode.split("\n");
        var code = new Code();
        int lineNumber = 0;

        for (var line : lines) {
            var rawInstruction = getRawInstruction(line, lineNumber);
            code.addRawInstruction(rawInstruction);
            lineNumber++;
        }

        return code;
    }

    public Statement getRawInstruction(String line, int lineNumber) throws CustomException {
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

        return new Statement(operation, operand1, operand2);
    }
}
