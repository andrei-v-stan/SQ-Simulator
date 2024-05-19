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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
public class TestMul {
    private static CPU cpuUnderTest;
    private static CPU spyCPU;
    class SUT {
        NamedByte mappedInstr;
        short firstOperand;
        short secondOperand;
        NamedByte modeFirstOperand;
        NamedByte modeSecondOperand;

        public SUT(String oppName, String firstModeName, String secondModeName, short firstOperand, short secondOperand) {
            this.mappedInstr = new NamedByte(oppName, "ARITHMETIC", (byte)0b00000100);
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
    void testArithmeticMul() {
        var instruction = new SUT("MUL", "DIRECT", "IMMEDIATE", (short) 0, (short) 5);
        try {
            doReturn((short) 4).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 5).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 4, (short) 5, (short) 20);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 20);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticMulOverflow() {
        var instruction = new SUT("MUL", "DIRECT", "IMMEDIATE", (short) 0, (short) 2);
        try {
            doReturn((short) 16384).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 2).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 16384, (short) 2, (short) -32768);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) -32768);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticMulNegativeResult() {
        var instruction = new SUT("MUL", "DIRECT", "IMMEDIATE", (short) 0, (short) -5);
        try {
            doReturn((short) 4).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) -5).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 4, (short) -5, (short) -20);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) -20);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticMulZeroResult() {
        var instruction = new SUT("MUL", "DIRECT", "IMMEDIATE", (short) 0, (short) 0);
        try {
            doReturn((short) 20).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 0).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 20, (short) 0, (short) 0);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 0);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }
}
