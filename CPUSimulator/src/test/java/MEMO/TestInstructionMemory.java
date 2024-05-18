package MEMO;

import UTILS.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestInstructionMemory {

    private InstructionMemory instructionMemory;

    @BeforeEach
    void setUp() {
        instructionMemory = new InstructionMemory(4, 1024); // 4 pages, each 1024 bytes
    }

    @Test
    void testInitialization() {
        assertNotNull(instructionMemory);
    }

    @Test
    void testWriteAndReadInstruction() {
        byte[] instruction = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        instructionMemory.writeInstruction(instruction);
        byte[] readInstruction = instructionMemory.readInstruction(0);

        assertNotNull(readInstruction);
        assertArrayEquals(instruction, readInstruction);
    }

    @Test
    void testWriteAndReadMultipleInstructions() {
        byte[] instruction1 = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
        byte[] instruction2 = {0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E};

        instructionMemory.writeInstruction(instruction1);
        instructionMemory.writeInstruction(instruction2);

        byte[] readInstruction1 = instructionMemory.readInstruction(0);
        byte[] readInstruction2 = instructionMemory.readInstruction(1);

        assertNotNull(readInstruction1);
        assertArrayEquals(instruction1, readInstruction1);

        assertNotNull(readInstruction2);
        assertArrayEquals(instruction2, readInstruction2);
    }

    @Test
    void testOutOfBoundsWrite() {
        byte[] instruction = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        // Write instructions to fill the memory
        int totalInstructions = 4 * (1024 / 7); // maxPages * maxInstructionCountOnPage
        for (int i = 0; i < totalInstructions; i++) {
            instructionMemory.writeInstruction(instruction);
        }

        // Try to write one more instruction, which should fail
        assertThrows(CustomException.class, () -> {
            instructionMemory.writeInstruction(instruction);
        });
    }

    @Test
    void testOutOfBoundsRead() {
        // Attempt to read an instruction when none have been written
        assertThrows(CustomException.class, () -> {
            instructionMemory.readInstruction(0);
        });

        byte[] instruction = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};

        // Write a single instruction
        instructionMemory.writeInstruction(instruction);

        // Attempt to read an out-of-bounds instruction
        assertThrows(IndexOutOfBoundsException.class, () -> {
            instructionMemory.readInstruction(1);
        });
    }
}
