package TestCPU.Instructions;

import CPU.CPU;
import CPU.Info.NamedByte;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import UTILS.CustomException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class LogicalInstructions {

    private static CPU cpuUnderTest;
    private static CPU spyCPU;

    class SUT {
        NamedByte mappedInstr;
        short firstOperand;
        short secondOperand;
        NamedByte modeFirstOperand;
        NamedByte modeSecondOperand;

        public SUT(String oppName, String firstModeName, String secondModeName, short firstOperand, short secondOperand) {
            this.mappedInstr = new NamedByte(oppName, "LOGICAL", (byte) 0b00000010);
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
    void testLogicalAND() {
        var instruction = new SUT("AND", "DIRECT", "IMMEDIATE", (short) 0, (short) 20);
        try {
            doReturn((short) 10).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 20).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionLogical(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 0);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLogicalOR() {
        var instruction = new SUT("OR", "DIRECT", "IMMEDIATE", (short) 0, (short) 20);
        try {
            doReturn((short) 10).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 20).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionLogical(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 30);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLogicalXOR() {
        var instruction = new SUT("XOR", "DIRECT", "IMMEDIATE", (short) 0, (short) 20);
        try {
            doReturn((short) 10).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 20).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionLogical(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 30);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLogicalNOT() {
        var instruction = new SUT("NOT", "DIRECT", "", (short) 0, (short) 0);
        try {
            doReturn((short) 10).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            spyCPU.executeInstructionLogical(instruction.mappedInstr, instruction.firstOperand, (short) 0,
                    instruction.modeFirstOperand, null);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) -11);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }
}
