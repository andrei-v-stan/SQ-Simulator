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
import UTILS.SyncHelper;

import java.util.*;

public class CPU {
    public HashMap<String, Register> registers;
    private InstructionMemory instructionMemory;
    private Memory memo;
    private Parser parser;

    private boolean EF, CF, OF, SF, PF;

    public Memory getMemo() {
        return memo;
    }

    public void setMemo(Memory memo) {
        this.memo = memo;
    }

    public CPU(InstructionMemory instructionMemory, Memory memo, Parser parser) {
        assert instructionMemory != null : "Instruction memory must be initialized";
        assert memo != null : "Memory must be initialized";
        assert parser != null : "Parser must be initialized";

        this.instructionMemory = instructionMemory;
        this.memo = memo;
        this.parser = parser;
        Initialize();
    }

    private void Initialize() {
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

        assert registers.size() == 10 : "There must be 10 registers initialized";
    }

    public void execute(int programOffset, int instructionCount) throws CustomException {
        assert programOffset >= 0 : "Program offset must not be negative";
        assert instructionCount >= 0 : "Instruction count must not be negative";

        int currentInstruction = programOffset;
        while (currentInstruction < instructionCount) {
            registers.get("PC").setValue((short) (currentInstruction + 1));
            var rawInstruction = instructionMemory.readInstruction(currentInstruction);
            try {
                prepareInstruction(rawInstruction);
            } catch (Exception e) {
                throw new CustomException(String.format("Error at line  %d\n%s", currentInstruction, e.getMessage()));
            }
            currentInstruction = registers.get("PC").getValue();
        }

        assert currentInstruction <= instructionCount : "Current instruction index must be less than or equal to instruction count";
    }
    // in the future execute with breakpoints

    public void loadInstructionMemory(String sourceCode) throws CustomException {
        assert sourceCode != null && !sourceCode.isBlank() : "Source code must not be null or empty";

        parser.loadInstructionMemory(instructionMemory, sourceCode);

        assert instructionMemory.getInstructionCount() > 0 : "No instructions registered in memory";
        assert instructionMemory.getPageSize() > 0 : "Invalid instruction memory page size";
    }

    private void prepareInstruction(byte[] rawInstruction) throws CustomException {
        assert rawInstruction.length == 7 : "Invalid raw instruction size";

        byte operator = rawInstruction[0];

        // Extract operand specification (2 bits)
        byte firstOperandSpec = rawInstruction[1];

        // Extract operand (16 bits)
        short firstOperand = (short) (((rawInstruction[2] & 0xFF) << 8) | (rawInstruction[3] & 0xFF));

        byte secondOperandSpec = rawInstruction[4];
        short secondOperand = (secondOperandSpec != 0) ? (short) (((rawInstruction[5] & 0xFF) << 8) | (rawInstruction[6] & 0xFF)) : 0;

        var mappedInstr = InstructionSet.searchByCode(operator);
        var modeFirstOperand = AddressingModes.searchByCode(firstOperandSpec);
        var modeSecondOperand = (secondOperandSpec != 0) ? AddressingModes.searchByCode(secondOperandSpec) : null;

        if (mappedInstr == null) {
            throw new CustomException("Could not find Instruction in instruction set");
        }

        if (!mappedInstr.name.equals("RET") && (modeFirstOperand == null || (modeSecondOperand == null && !InstructionSet.isUnaryOp(mappedInstr)))) {
            throw new CustomException("Operand mode not recognized");
        }

        if (!mappedInstr.name.equals("RET") && (modeFirstOperand.name.equals("IMMEDIATE") && !InstructionSet.isImmediateOp(mappedInstr))) {
            throw new CustomException("First operand addressing mode can not be immediate");
        }
        switch (mappedInstr.category) {
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
                executeInstructionFct(mappedInstr, firstOperand, modeFirstOperand);
                break;
            default:
                assert false : "Invalid raw instruction";
        }
    }

    private void executeInstructionFct(NamedByte mappedInstr, short operand, NamedByte modeOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeOperand.name)) : "Invalid mode operand name";

        switch (mappedInstr.name) {
            case "CALL":
                var firstValue = resolveAddressing(modeOperand.name, operand);

                if (firstValue == null) {
                    throw new CustomException("Could not resolve addressing of operands");
                }

                writeToStack(registers.get("AX").getValue());
                writeToStack(registers.get("BX").getValue());
                writeToStack(registers.get("CX").getValue());
                writeToStack(registers.get("DX").getValue());
                writeToStack(registers.get("EX").getValue());
                writeToStack(registers.get("FX").getValue());
                writeToStack(registers.get("GX").getValue());
                writeToStack(registers.get("HX").getValue());

                writeToStack(registers.get("PC").getValue());

                registers.get("PC").setValue(firstValue);
                break;
            case "RET":
                registers.get("PC").setValue(readFromStack());

                registers.get("HX").setValue(readFromStack());
                registers.get("GX").setValue(readFromStack());
                registers.get("FX").setValue(readFromStack());
                registers.get("EX").setValue(readFromStack());
                registers.get("DX").setValue(readFromStack());
                registers.get("CX").setValue(readFromStack());
                registers.get("BX").setValue(readFromStack());
                registers.get("AX").setValue(readFromStack());
                break;
        }
    }


    private void executeInstructionIO(NamedByte mappedInstr, short firstOperand, NamedByte modeFirstOperand, short secondOperand, NamedByte modeSecondOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeFirstOperand.name)) : "Invalid mode first operand name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeSecondOperand.name)) : "Invalid mode second operand name";

        var firstValue = resolveAddressing(modeFirstOperand.name, firstOperand);

        if (firstValue == null) {
            throw new CustomException("Could not resolve addressing of operands");
        }

        switch (mappedInstr.name) {
            case "READ":

                // READY TO WRITE KEYBOARD.isReady = true
                // await write
                SyncHelper.notifyThread();
                SyncHelper.waitForNotification();

                var charByte = memo.read(Configurator.configFR.getKeyboardBufferPage(),
                        Configurator.configFR.getKeyboardBufferPageOffset(),
                        Configurator.configFR.getKeyboardBufferLength())[0];
                writeToAddress(modeFirstOperand.name, firstOperand, (short) charByte);
                break;
            case "WRITE":
                var secondValue = resolveAddressing(modeSecondOperand.name, secondOperand);

                if (secondValue == null) {
                    throw new CustomException("Could not resolve addressing of operands");
                }
                memo.write(firstValue, (short) (secondValue & 0x7FFF), 1);
                break;
        }

    }

    private void executeInstructionStack(NamedByte mappedInstr, short operand, NamedByte modeOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeOperand.name)) : "Invalid mode operand name";

        var firstValue = resolveAddressing(modeOperand.name, operand);
        if (firstValue == null) {
            throw new CustomException("Could not resolve addressing of operands");
        }
        var sp = registers.get("SP");
        switch (mappedInstr.name) {
            case "PUSH":
                if (MemoryManager.isLocationUsed((short) (sp.getValue() + 2), 2)) {
                    throw new CustomException("Stack Overflow");
                }
                memo.write(sp.getValue(), operand);
                sp.setValue((short) (sp.getValue() + 2));

                break;
            case "POP":
                if (MemoryManager.isLocationUsed((short) (sp.getValue() - 2), 2)) {
                    throw new CustomException("Stack Underflow");
                }
                writeToAddress(modeOperand.name, operand, memo.read(sp.getValue(), 2));

                break;
        }

    }

    private void executeInstructionJump(NamedByte mappedInstr, short operand, NamedByte modeOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeOperand.name)) : "Invalid mode operand name";

        var firstValue = resolveAddressing(modeOperand.name, operand);
        if (firstValue == null) {
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

    public void executeInstructionCompare(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeFirstOperand.name)) : "Invalid mode first operand name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeSecondOperand.name)) : "Invalid mode second operand name";

        var firstValue = resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue = resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue == null || secondValue == null) {
            throw new CustomException("Could not resolve addressing of operands");
        }
        updateFlagsForArithmetic(firstValue, secondValue, (short) (firstValue - secondValue));
    }

    public void executeInstructionShift(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeFirstOperand.name)) : "Invalid mode first operand name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeSecondOperand.name)) : "Invalid mode second operand name";

        var firstValue = resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue = resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue == null || secondValue == null) {
            throw new CustomException("Could not resolve addressing of operands");
        }

        short result = switch (mappedInstr.name) {
            case "SHL" -> (short) (firstValue << secondValue);
            case "SHR" -> (short) (firstValue >> secondValue);
            default -> 0;
        };

        writeToAddress(modeFirstOperand.name, firstOperand, result);
    }

    public void executeInstructionLogical(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeFirstOperand.name)) : "Invalid mode first operand name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeSecondOperand.name)) : "Invalid mode second operand name";

        var firstValue = resolveAddressing(modeFirstOperand.name, firstOperand);
        Short secondValue = null;
        if (modeSecondOperand != null)
            secondValue = resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue == null || (secondValue == null && !mappedInstr.name.equals("NOT"))) {
            throw new CustomException("Could not resolve addressing of operands");
        }

        short result = switch (mappedInstr.name) {
            case "NOT" -> (short) ~firstValue;
            case "AND" -> (short) (firstValue & secondValue);
            case "OR" -> (short) (firstValue | secondValue);
            case "XOR" -> (short) (firstValue ^ secondValue);
            default -> 0;
        };

        writeToAddress(modeFirstOperand.name, firstOperand, result);
    }

    public void executeInstructionArithmetic(NamedByte mappedInstr, short firstOperand, short secondOperand, NamedByte modeFirstOperand, NamedByte modeSecondOperand) throws CustomException {
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(mappedInstr.name)) : "Invalid mapped instruction name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeFirstOperand.name)) : "Invalid mode first operand name";
        assert InstructionSet.instructions.stream().anyMatch(i -> i.name.equals(modeSecondOperand.name)) : "Invalid mode second operand name";

        var firstValue = resolveAddressing(modeFirstOperand.name, firstOperand);
        var secondValue = resolveAddressing(modeSecondOperand.name, secondOperand);

        if (firstValue == null || secondValue == null) {
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
        writeToAddress(modeFirstOperand.name, firstOperand, result);
    }

    public void writeToAddress(String mode, short location, short result) throws CustomException {
        switch (mode) {
            case "DIRECT":
                memo.write(location, result);
                break;
            case "REGISTER":
                var register = searchRegister((byte) location);
                if (register == null) {
                    throw new CustomException("Could not find register by code");
                }
                register.setValue(result);
            case "INDIRECT":
                registers.get("HX").setValue(result);// <- relook
            default:
                assert false : "Invalid memory mode: " + "mode";
        }
    }

    public Short resolveAddressing(String mode, short operand) throws CustomException {
        switch (mode) {
            case "IMMEDIATE":
                return operand;
            case "DIRECT":
                return memo.read(operand, 2);
            case "REGISTER":
                var register = searchRegister((byte) operand);
                if (register == null) {
                    throw new CustomException("Could not find register by code");
                }
                return register.getValue();
            case "INDIRECT":
                return registers.get("HX").getValue();
            default:
                assert false : "invalid addressing mode: " + mode;
        }
        return null;
    }

    private Register searchRegister(byte lookup) {
        for (var entry : registers.entrySet()) {
            if (entry.getValue().getRegisterCode() == lookup) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void updateFlagsForArithmetic(short operand1, short operand2, short result) {
        CF = (result < operand1) || (result < operand2);
        OF = ((operand1 > 0 && operand2 > 0 && result < 0) || (operand1 < 0 && operand2 < 0 && result > 0));
        SF = (result < 0);
        PF = calculateParity(result);
        EF = (result == 0);
    }

    public boolean calculateParity(short result) {
        int bitCount = 0;
        for (int i = 0; i < 16; i++) {
            if (((result >> i) & 1) == 1) {
                bitCount++;
            }
        }
        return bitCount % 2 == 0;
    }

    public void writeToStack(short data) {
        var sp = registers.get("SP");
        final short oldSpValue = sp.getValue();

        memo.write(sp.getValue(), data);
        sp.setValue((short) (sp.getValue() + 2));

        assert sp.getValue() > oldSpValue : "Error at writing in stack";
    }

    public short readFromStack() {
        var sp = registers.get("SP");
        final short oldSpValue = sp.getValue();

        sp.setValue((short) (sp.getValue() - 2));
        var buff = memo.read(sp.getValue(), 2);

        assert sp.getValue() < oldSpValue : "Error at reading from stack";
        return buff;
    }

    public HashMap<String, Register> getRegisters() {
        return registers;
    }

    public boolean isEF() {
        return EF;
    }

    public void setEF(boolean EF) {
        this.EF = EF;
    }

    public boolean isCF() {
        return CF;
    }

    public void setCF(boolean CF) {
        this.CF = CF;
    }

    public boolean isOF() {
        return OF;
    }

    public void setOF(boolean OF) {
        this.OF = OF;
    }

    public boolean isSF() {
        return SF;
    }

    public void setSF(boolean SF) {
        this.SF = SF;
    }

    public boolean isPF() {
        return PF;
    }

    public void setPF(boolean PF) {
        this.PF = PF;
    }
}

