package TestCPU.Instructions.ArithmeticInstructions;

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

public class TestAdd {

    private static CPU cpuUnderTest;
    private static CPU spyCPU;
    class SUT{
        NamedByte mappedInstr;
        short firstOperand;
        short secondOperand;
        NamedByte modeFirstOperand;
        NamedByte modeSecondOperand;

        public SUT(String oppName, String firstModeName, String secondModeName, short firstOperand, short secondOperand) {
            this.mappedInstr = new NamedByte(oppName, "ARITHMETIC", (byte)0b00000010);
            this.firstOperand = firstOperand;
            this.secondOperand = secondOperand;
            this.modeFirstOperand = new NamedByte(firstModeName, (byte) 0b00000001);
            this.modeSecondOperand = new NamedByte(secondModeName, (byte) 0b00000001);;
        }
    }

    @BeforeAll
    static void setUp(){
        cpuUnderTest= new CPU(new InstructionMemory(5, 1024),
                new Memory(5, 1024), new ParserImpl());
        spyCPU= Mockito.spy(cpuUnderTest);
    }
    @Test
    void testArithmeticAdd(){
    var instruction= new SUT("ADD","DIRECT", "IMMEDIATE", (short) 0, (short) 20);
        try {
            doReturn((short) 10).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 20).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr,instruction.firstOperand, instruction.secondOperand, instruction.modeFirstOperand,
                    instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 10, (short) 20, (short) 30);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 30);

        } catch (CustomException e) {
            throw new RuntimeException(e);
        }

    }
    void testArithmeticOverflow() {
        var instruction = new SUT("ADD", "DIRECT", "IMMEDIATE", (short) 0, (short) 32767);
        try {
            doReturn((short) 32767).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) 1).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 32767, (short) 1, (short) -32768);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) -32768);

        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticNegativeResult() {
        var instruction = new SUT("ADD", "DIRECT", "IMMEDIATE", (short) 0, (short) -30);
        try {
            doReturn((short) 20).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) -30).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 20, (short) -30, (short) -10);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) -10);

        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArithmeticZeroResult() {
        var instruction = new SUT("ADD", "DIRECT", "IMMEDIATE", (short) 0, (short) -20);
        try {
            doReturn((short) 20).when(spyCPU).resolveAddressing("DIRECT", instruction.firstOperand);
            doReturn((short) -20).when(spyCPU).resolveAddressing("IMMEDIATE", instruction.secondOperand);
            spyCPU.executeInstructionArithmetic(instruction.mappedInstr, instruction.firstOperand, instruction.secondOperand,
                    instruction.modeFirstOperand, instruction.modeSecondOperand);
            verify(spyCPU).updateFlagsForArithmetic((short) 20, (short) -20, (short) 0);
            verify(spyCPU).writeToAddress(instruction.modeFirstOperand.name, instruction.firstOperand, (short) 0);

        } catch (CustomException e) {
            throw new RuntimeException(e);
        }
    }


}
