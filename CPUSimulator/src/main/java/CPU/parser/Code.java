package CPU.parser;

import CPU.Info.AddressingModes;
import CPU.Info.InstructionSet;
import UTILS.CustomException;

import java.util.*;
import java.util.regex.Pattern;

public class Code {
    public List<Statement> statements;

    public Code() {
        this.statements = new ArrayList<>();
    }

    public void addRawInstruction(Statement statement) {
        statements.add(statement);
    }

    public byte[] toBytes(Statement statement) throws CustomException {
        var result = new byte[7];

        var namedByte = InstructionSet.searchByName(statement.operation);
        if (namedByte == null) {
            throw new CustomException("Instruction does not exist: " + statement.operation);
        }
        result[0] = namedByte.opcode;

        if (statement.operand1 != null) {
            writeToBytes(result, statement.operand1, false);
        }

        if(statement.operand2 != null) {
            writeToBytes(result, statement.operand2, true);
        }

        return result;
    }

    void writeToBytes(byte[] result, String operand, boolean isSecond) throws CustomException {
        var offset = 0;
        if(isSecond) {
            offset += 3;
        }

        byte[] bytes = new byte[0];
        if(Convertor.isShortConst(operand)) {
            result[1 + offset] = AddressingModes.getCode("IMMEDIATE");
            bytes = Convertor.getBytesFromShort(operand);
        }
        else if(Convertor.isCharConst(operand)) {
            result[1 + offset] = AddressingModes.getCode("IMMEDIATE");
            bytes = Convertor.getBytesFromChar(operand);
        }
        else if(Convertor.isHexConst(operand)) {
            result[1 + offset] = AddressingModes.getCode("IMMEDIATE");
            bytes = Convertor.getBytesFromHex(operand);
        }
        else if(Convertor.isRegister(operand)) {
            result[1 + offset] = AddressingModes.getCode("REGISTER");
            bytes = Convertor.getBytesFromRegister(operand);
        }
        else if(Convertor.isDirectMemory(operand)) {
            result[1 + offset] = AddressingModes.getCode("DIRECT");
            bytes = Convertor.getBytesFromDirectMemory(operand);
        }
        else if(Convertor.isIndirectMemory(operand)) {
            result[1 + offset] = AddressingModes.getCode("INDIRECT");
            bytes = Convertor.getBytesFromIndirectMemory(operand);
        }
        else {
            throw new CustomException("Operand type not recognized: " + operand);
        }
        result[2 + offset] = bytes[0];
        result[3 + offset] = bytes[1];
    }
}
