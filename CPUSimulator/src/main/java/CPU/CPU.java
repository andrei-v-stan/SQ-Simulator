package CPU;
import CORE.ConfigFileReader;
import CORE.Configurator;
import CPU.Info.AddressingModes;
import CPU.Info.InstructionSet;
import CPU.Info.NamedByte;
import CPU.parser.Parser;
import MEMO.InstructionMemory;
import MEMO.Memory;
import MEMO.MemoryManager;
import UTILS.CustomException;

import  java.util.*;
public class CPU {
    public HashMap<String, Register> registers;
    private InstructionMemory instructionMemory;
    private Memory memo;
    private Parser parser;

    private boolean EF, CF, OF, SF, PF;

    public CPU(InstructionMemory instructionMemory, Memory memo, Parser parser) {
        this.instructionMemory = instructionMemory;
        this.memo = memo;
        this.parser = parser;
        Initialize();
    }

    private void Initialize(){
        registers = new HashMap<String, Register>();
        registers.put("AX", new Register((byte) 0b00000001));
        registers.put("BX", new Register((byte) 0b00000010));
        registers.put("CX", new Register((byte) 0b00000011));
        registers.put("DX", new Register((byte) 0b00000100));
        registers.put("EX", new Register((byte) 0b00000101));
        registers.put("FX", new Register((byte) 0b00000110));
        registers.put("GX", new Register((byte) 0b00000111));
        registers.put("HX", new Register((byte) 0b00001000));

        registers.put("SP", new Register((byte) 0b00001001));
        registers.put("PC", new Register((byte) 0b00001010));
    }

    public void execute(int programOffset, int instructionCount) throws CustomException {


        for (int currentInstruction=0; currentInstruction<= instructionCount; currentInstruction++){

            var rawInstruction = instructionMemory.readInstruction(currentInstruction + programOffset);
            try {
                prepareInstruction(rawInstruction);
            }catch (Exception e){
                throw new CustomException(String.format("Error at line  %d\n%s",currentInstruction, e.getMessage()));
            }
        }

    }
    // in the future execute with breakpoints

    public void loadInstructionMemory(String sourceCode) throws CustomException {
        parser.loadInstructionMemory(instructionMemory, sourceCode);
    }

    private void prepareInstruction(byte[] rawInstruction) throws CustomException {
        byte operator = rawInstruction[0];

        // Extract operand specification (2 bits)
        byte firstOperandSpec = rawInstruction[1];

        // Extract operand (16 bits)
        short firstOperand = (short) (((rawInstruction[2] & 0xFF) << 8) | (rawInstruction[3] & 0xFF));

        byte secondOperandSpec= rawInstruction[4];
        short secondOperand = (secondOperandSpec != 0) ? (short) (((rawInstruction[5] & 0xFF) << 8) | (rawInstruction[6] & 0xFF)) : 0;

        var mappedInstr= InstructionSet.searchByCode(operator);
        var modeFirstOperand= AddressingModes.searchByCode(firstOperandSpec);
        var modeSecondOperand= (secondOperandSpec != 0) ? AddressingModes.searchByCode(secondOperandSpec) : null;

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
                break;
            case "STACK":
                executeInstructionStack(mappedInstr, firstOperand, modeFirstOperand);
                break;
            case "IO":
                executeInstructionIO(mappedInstr, firstOperand, modeFirstOperand, secondOperand, modeSecondOperand);
                break;
            case "FCT":
                break;
        }
    }

    private void executeInstructionIO(NamedByte mappedInstr, short firstOperand, NamedByte modeFirstOperand, short secondOperand, NamedByte modeSecondOperand) throws CustomException {
        var firstValue= resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue= resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue== null || (secondValue == null && mappedInstr.name.equals("WRITE"))){
            throw new CustomException("Could not resolve addressing of operands");
        }
        switch (mappedInstr.name){
            case "READ":
                // READY TO WRITE KEYBOARD.isReady = true
                // await write
                var charByte= memo.read(Configurator.configFR.getKeyboardBufferPage(),
                        Configurator.configFR.getKeyboardBufferPageOffset(),
                        Configurator.configFR.getKeyboardBufferLength())[0];
                writeToAddress(modeFirstOperand.name, firstOperand, (short) charByte);
                break;
            case "WRITE":
                memo.write(firstValue,(short) (secondValue & 0x7FFF));
                break;
        }

    }

    private void executeInstructionStack(NamedByte mappedInstr, short operand, NamedByte modeOperand) throws CustomException {
        var firstValue= resolveAddressing(modeOperand.name, operand);
        if (firstValue== null){
            throw new CustomException("Could not resolve addressing of operands");
        }
        var sp= registers.get("SP");
        switch (mappedInstr.name){
            case "PUSH":
                if (MemoryManager.isLocationUsed((short) (sp.getValue()+16), 16)){
                    throw new CustomException("Stack Overflow");
                }
                memo.write(sp.getValue(), operand);
                sp.setValue((short) (sp.getValue()+16));

                break;
            case "POP":
                if (MemoryManager.isLocationUsed((short) (sp.getValue()-16), 16)){
                    throw new CustomException("Stack Underflow");
                }
                writeToAddress(modeOperand.name, operand, memo.read(sp.getValue(), 16));

                break;
        }

    }

    private void executeInstructionJump(NamedByte mappedInstr, short operand, NamedByte modeOperand) throws CustomException {
        var firstValue= resolveAddressing(modeOperand.name, operand);
        if (firstValue== null){
            throw new CustomException("Could not resolve addressing of operands");
        }
        short line = switch (mappedInstr.name) {
            case "JMP" -> operand;
            case "JE" -> EF ? operand : -1;
            case "JNE" -> !EF ? operand : -1;
            case "JL" -> (SF != OF) ? operand : -1;
            case "JG" -> (!EF && SF == OF) ? operand : -1;
            case "JLE" -> (SF != OF || EF) ? operand : -1;
            case "JGE" -> (SF == OF) ? operand : -1;
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
                registers.get("HX").setValue(result);// <- relook

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
                return registers.get("HX").getValue();

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
        int bitCount = 0;
        for (int i = 0; i < 16; i++) {
            if (((result >> i) & 1) == 1) {
                bitCount++;
            }
        }
        return bitCount % 2 == 0;
    }

}

