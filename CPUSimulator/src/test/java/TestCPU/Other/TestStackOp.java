package TestCPU.Other;

import CPU.CPU;
import CPU.Register;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class TestStackOp {

    private static CPU cpuUnderTest;
    private static CPU spyCPU;
    @BeforeAll
    static void SetUp(){
        cpuUnderTest= new CPU(new InstructionMemory(5, 1024),
                new Memory(5, 1024), new ParserImpl());
        spyCPU= Mockito.spy(cpuUnderTest);
    }

    @Test
    void writeToStack(){
        /*Register mockSPRegister = mock(Register.class);

        // Set up mock SP register value
        when(spyCPU.getRegisters().get("SP")).thenReturn(mockSPRegister);
        when(mockSPRegister.getValue()).thenReturn((short) 100);

        // Call writeToStack method
        spyCPU.writeToStack((short) 42);

        // Verify interactions
        verify(spyCPU.getMemory()).write((short) 100, (short) 42);
        verify(mockSPRegister).setValue((short) 102);*/
    }

}
