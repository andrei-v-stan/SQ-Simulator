package TestCPU.Other;

import CPU.CPU;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCPUFlags {

    private CPU cpuUnderTest;

    @BeforeEach
    public void setUp() {
        cpuUnderTest = new CPU(new InstructionMemory(5, 1024), new Memory(5, 1024), new ParserImpl());
    }

    @Test
    void updateFlagsForArithmetic_PositiveOperands_ResultNegative_CFShouldBeTrue() {
        cpuUnderTest.updateFlagsForArithmetic((short) 5, (short) 3, (short) -2);

        assertTrue(cpuUnderTest.isCF());
        assertTrue(cpuUnderTest.isOF());
        assertTrue(cpuUnderTest.isSF());
        assertTrue(cpuUnderTest.isPF());
        assertFalse(cpuUnderTest.isEF());
    }

    @Test
    void updateFlagsForArithmetic_NegativeOperands_ResultPositive_OFShouldBeTrue() {
        cpuUnderTest.updateFlagsForArithmetic((short) -5, (short) -3, (short) 2);

        assertFalse(cpuUnderTest.isCF());
        assertTrue(cpuUnderTest.isOF());
        assertFalse(cpuUnderTest.isSF());
        assertFalse(cpuUnderTest.isPF());
        assertFalse(cpuUnderTest.isEF());
    }

    @Test
    void updateFlagsForArithmetic_ZeroResult_EFShouldBeTrue() {
        cpuUnderTest.updateFlagsForArithmetic((short) 0, (short) 3, (short) 0);

        assertTrue(cpuUnderTest.isCF());
        assertFalse(cpuUnderTest.isOF());
        assertFalse(cpuUnderTest.isSF());
        assertTrue(cpuUnderTest.isPF());
        assertTrue(cpuUnderTest.isEF());
    }

    @Test
    void calculateParity_EvenBitCount_ReturnsTrue() {
        assertTrue(cpuUnderTest.calculateParity((short) 10));
    }

    @Test
    void calculateParity_OddBitCount_ReturnsFalse() {
        assertFalse(cpuUnderTest.calculateParity((short) 7));
    }
}
