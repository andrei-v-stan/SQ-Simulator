package TestCPU.Other;

import CPU.CPU;
import CPU.Register;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TestStackOp {

    @Mock
    private Register mockSPRegister;

    @Mock
    private Memory mockMemory;

    private CPU cpuUnderTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Create CPU object and inject the mocked Memory object
        cpuUnderTest = new CPU(new InstructionMemory(5, 1024), mockMemory, new ParserImpl());
    }

    @Test
    void writeToStack() {
        // Set up mock SP register value
        when(mockSPRegister.getValue()).thenReturn((short) 100);

        // Set SP register in CPU to the mock SP register
        cpuUnderTest.getRegisters().put("SP", mockSPRegister);

        // Call writeToStack method
        cpuUnderTest.writeToStack((short) 42);

        // Verify that SP value is updated
        verify(mockSPRegister).setValue((short) 102);

        // Verify that writeToStack writes correct data to memory
        verify(mockMemory).write((short) 100, (short) 42);
    }
    @Test
    void readFromStack() {
        // Set up mock SP register value
        when(mockSPRegister.getValue()).thenReturn((short) 100).thenReturn((short) 98);

        // Set SP register in CPU to the mock SP register
        cpuUnderTest.getRegisters().put("SP", mockSPRegister);

        // Set up mock buffer data
        short bufferData = 42;
        when(mockMemory.read((short) 98, 2)).thenReturn(bufferData);

        // Call readFromStack method
        short result = cpuUnderTest.readFromStack();

        // Verify that SP value is updated
        verify(mockSPRegister).setValue((short) 98);

        // Verify that readFromStack reads correct data from memory
        verify(mockMemory).read((short) 98, 2);

        // Verify the returned result
        assertEquals(bufferData, result);
    }
}
