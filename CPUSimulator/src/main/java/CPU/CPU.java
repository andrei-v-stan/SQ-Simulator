package CPU;
import CPU.Info.AddressingModes;
import CPU.Info.InstructionSet;
import CPU.Info.NamedByte;
import MEMO.InstructionMemory;
import MEMO.Memory;
import UTILS.CustomException;

import  java.util.*;
public class CPU {
    public HashMap<String, Register> registers;
    private ALU alu;
    private InstructionMemory instructionMemory;
    private Memory memo;

    private boolean EF, CF, OF, SF, PF;

    private void Initialize(){
        registers= new HashMap<String, Register>();
        registers.put("AX", new Register((byte) 0b00000001));
        registers.put("BX", new Register((byte) 0b00000010));
        registers.put("CX", new Register((byte) 0b00000011));
        registers.put("DX", new Register((byte) 0b00000100));
        registers.put("EX", new Register((byte) 0b00000101));
        registers.put("FX", new Register((byte) 0b00000110));
        registers.put("SP", new Register((byte) 0b00000111));
        registers.put("PC", new Register((byte) 0b00001000));
    }

    private void execute(int programOffset, int instructionCount){


        for (int currentInstruction=0; currentInstruction<= instructionCount; currentInstruction++){

            var rawInstruction = instructionMemory.readInstruction(currentInstruction + programOffset);

        }
        //while there are instruction
        // InstructionMemory.readInstruction()
        // DECODE Instruction
        // execute Instruction
            // -> Arithm, Logical, read write Memo, funct call, push pop, IO, jumps

    }
    // in the future execute with breakpoints

    private void prepareInstruction(byte[] rawInstruction) throws CustomException {
        byte operator = rawInstruction[0];

        // Extract operand specification (2 bits)
        byte firstOperandSpec = rawInstruction[1];

        // Extract operand (16 bits)
        short firstOperand = (short) (((rawInstruction[2] & 0xFF) << 8) | (rawInstruction[3] & 0xFF));

        byte secondOperandSpec= rawInstruction[4];
        short secondOperand = (secondOperandSpec == 0) ? (short) (((rawInstruction[5] & 0xFF) << 8) | (rawInstruction[6] & 0xFF)) : 0;


        var mappedInstr= InstructionSet.searchByCode(operator);
        var modeFirstOperand= AddressingModes.searchByCode(firstOperandSpec);
        var modeSecondOperand= (secondOperandSpec == 0) ? AddressingModes.searchByCode(secondOperandSpec) : null;

        if (mappedInstr == null){
            throw new CustomException("Could not find Instruction in instruction set");
        }

        if ( modeFirstOperand == null || modeSecondOperand == null){
            throw  new CustomException("Operand mode not recognized");
        }

        if( modeFirstOperand.name.equals("IMMEDIATE")){
            throw new CustomException("First operand addressing mode can not be immediate");
        }
        switch (mappedInstr.category){
            case "ARITHMETIC":
                executeInstructionArithmetic(mappedInstr, firstOperand, secondOperand, modeFirstOperand, modeSecondOperand);
                break;
            case "LOGICAL":
                executeInstructionLogical(mappedInstr, firstOperand, secondOperand, modeFirstOperand, modeSecondOperand);
                break;
            case "SHIFT":
                executeInstructionShift(mappedInstr, firstOperand, secondOperand, modeFirstOperand, modeSecondOperand);
                break;
            case "CMP":
                executeInstructionCompare(mappedInstr, firstOperand, secondOperand, modeFirstOperand, modeSecondOperand);
                break;
            case "JUMP":
                executeInstructionJump(mappedInstr, firstOperand, modeFirstOperand);
        }
    }

    private void executeInstructionJump(NamedByte mappedInstr, short firstOperand, NamedByte modeFirstOperand) throws CustomException {
        var firstValue= resolveAddressing(modeFirstOperand.name, firstOperand);
        if (firstValue== null){
            throw new CustomException("Could not resolve addressing of operands");
        }
        short line = switch (mappedInstr.name) {
            case "JMP" -> firstOperand;
            case "JE" -> EF ? firstOperand : -1;
            case "JNE" -> !EF ? firstOperand : -1;
            case "JL" -> (SF != OF) ? firstOperand : -1;
            case "JG" -> (!EF && SF == OF) ? firstOperand : -1;
            case "JLE" -> (SF != OF || EF) ? firstOperand : -1;
            case "JGE" -> (SF == OF) ? firstOperand : -1;
            default -> -1;
        };

        if (line > -1)
            registers.get("PC").setValue(line);
    }

    private void executeInstructionCompare(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        var firstValue= resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue= resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue== null || secondValue == null){
            throw new CustomException("Could not resolve addressing of operands");
        }
        updateFlagsForArithmetic(firstValue, secondValue, (short) (firstValue - secondValue));
    }

    private void executeInstructionShift(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        var firstValue= resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue= resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue== null || secondValue == null){
            throw new CustomException("Could not resolve addressing of operands");
        }

        short result = switch (mappedInstr.name) {
            case "SHL" -> (short) (firstValue << secondValue);
            case "SHR" -> (short) (firstValue >> secondValue);
            default -> 0;
        };

        writeToAddress(mappedInstr.name, firstOperand, result);

    }

    private void executeInstructionLogical(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        var firstValue= resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue= resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue== null || secondValue == null){
            throw new CustomException("Could not resolve addressing of operands");
        }

        short result = switch (mappedInstr.name) {
            case "NOT" -> (short) ~firstValue;
            case "AND" -> (short) (firstValue & secondValue);
            case "OR" -> (short) (firstValue | secondValue);
            case "XOR" -> (short) (firstValue ^ secondValue);
            default -> 0;
        };

        writeToAddress(mappedInstr.name, firstOperand, result);
    }

    private void executeInstructionArithmetic(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        var firstValue= resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue= resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue== null || secondValue == null){
            throw new CustomException("Could not resolve addressing of operands");
        }
        short result = switch (mappedInstr.name) {
            case "MOV" -> secondValue;
            case "ADD" -> (short) (firstValue + secondValue);
            case "SUB" -> (short) (firstValue - secondValue);
            case "MUL" -> (short) (firstValue * secondValue);
            case "DIV" -> {
                if (secondValue == 0) {
                    throw new CustomException("Can not divide by 0");
                }
                yield (short) (firstValue / secondValue);
            }
            default -> 0;
        };

        updateFlagsForArithmetic(firstValue, secondValue, result);
        writeToAddress(mappedInstr.name, firstOperand, result);
    }
    private  void writeToAddress(String mode, short location, short result) throws CustomException{
        switch (mode){
            case "DIRECT":
                memo.write(location, result);
                break;
            case "REGISTER":
                var register = searchRegister((byte) location);
                if (register == null){
                    throw new CustomException("Could not find register by code");
                }
                register.setValue(result);
            case "INDIRECT":
                registers.get("FX").setValue(result);

        }
    }

    private Short resolveAddressing(String mode, short operand) throws CustomException {
        switch (mode){
            case "IMMEDIATE":
                return operand;
            case "DIRECT":
                return memo.read(operand, 16);
            case "REGISTER":
                var register = searchRegister((byte)operand);
                if (register == null){
                    throw new CustomException("Could not find register by code");
                }
                return register.getValue();
            case "INDIRECT":
                return registers.get("FX").getValue();

        }
        return null;
    }
    private Register searchRegister(byte lookup){
        for (var entry : registers.entrySet()) {
            if (entry.getValue().getRegisterCode() == lookup) {
                return entry.getValue();
            }
        }
        return null;
    }
    private void updateFlagsForArithmetic(short operand1, short operand2, short result) {
        CF = (result < operand1) || (result < operand2);
        OF = ((operand1 > 0 && operand2 > 0 && result < 0) || (operand1 < 0 && operand2 < 0 && result > 0));
        SF = (result < 0);
        PF = calculateParity(result);
        EF = (result == 0);
    }

    private boolean calculateParity(short result) {
        // Count the number of set bits in the result
        int bitCount = 0;
        for (int i = 0; i < 16; i++) {
            if (((result >> i) & 1) == 1) {
                bitCount++;
            }
        }
        // Parity is even if bit count is even
        return bitCount % 2 == 0;
    }

}

