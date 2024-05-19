package TestCPU.Instructions.ArithmeticInstructions;


import CPU.CPU;
import CPU.Info.NamedByte;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import UTILS.CustomException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
public class TestDiv {
    private static CPU cpuUnderTest;
    private static CPU spyCPU;
    class SUT {
        NamedByte mappedInstr;
        short firstOperand;
        short secondOperand;
        NamedByte modeFirstOperand;
        NamedByte modeSecondOperand;

        public SUT(String oppName, String firstModeName, String secondModeName, short firstOperand, short secondOperand) {
            this.mappedInstr = new NamedByte(oppName, "ARITHMETIC", (byte)0b00000101);
            this.firstOperand = firstOperand;
            this.secondOperand = secondOperand;
            this.modeFirstOperand = new NamedByte(firstModeName, (byte) 0b00000001);
            this.modeSecondOperand = new NamedByte(secondModeName, (byte) 0b00000001);
        }
    }

    @BeforeAll
    static void setUp() {
        cpuUnderTest = new CPU(new InstructionMemory(5, 1024),
                new Memory(5, 1024), new ParserImpl());
        spyCPU = Mockito.spy(cpuUnderTest);
    }

    @Test
    void testArithmeticDiv() {
        var instruction = new SUT("DIV", "DIRECT", "IMMEDIATE", (short) 0, (short) 2);
        try {
            doReturn((short) 20).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 2).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 20, (short) 2, (short) 10);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 10);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticDivByZero() {
        var instruction = new SUT("DIV", "DIRECT", "IMMEDIATE", (short) 0, (short) 0);
        try {
            doReturn((short) 20).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 0).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            assertThrows(CustomException.class, () -> spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand));
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticDivNegativeResult() {
        var instruction = new SUT("DIV", "DIRECT", "IMMEDIATE", (short) 0, (short) -2);
        try {
            doReturn((short) 20).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) -2).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 20, (short) -2, (short) -10);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) -10);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticDivZeroResult() {
        var instruction = new SUT("DIV", "DIRECT", "IMMEDIATE", (short) 0, (short) 2);
        try {
            doReturn((short) 0).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 2).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 0, (short) 2, (short) 0);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 0);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }
}

