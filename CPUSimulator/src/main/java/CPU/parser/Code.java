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
        assert statements != null : "Code statements are not initialized";

        statements.add(statement);

        assert !statements.isEmpty() : "No code statements were added";
    }

    public byte[] toBytes(Statement statement) throws CustomException {
        assert statement != null : "Statement must not be null";
        var namedByte = InstructionSet.searchByName(statement.operation);
        assert namedByte != null : "No instruction found with name '" +  statement.operation + "'";

        var result = new byte[7];
        result[0] = namedByte.opcode;

        if (statement.operand1 != null) {
            writeToBytes(result, statement.operand1, false);
        }

        if(statement.operand2 != null) {
            writeToBytes(result, statement.operand2, true);
        }

        assert result.length == 7 : "Result must always have length 7";
        return result;
    }

    public void writeToBytes(byte[] result, String operand, boolean isSecond) throws CustomException {
        assert operand != null : "Operand token must not be null";

        var offset = 0;
        if(isSecond) {
            offset += 3;
        }

        byte[] bytes;
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

        assert result.length == 7 : "Byte array result must have length 7";
    }
}
