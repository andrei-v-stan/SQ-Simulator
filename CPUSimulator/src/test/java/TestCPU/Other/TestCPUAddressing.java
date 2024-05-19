package TestCPU.Other;

import CPU.CPU;
import CPU.Register;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import UTILS.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class TestCPUAddressing {
    @Mock
    private Register mockSPRegister;

    @Mock
    private Memory mockMemory;

    private CPU cpuUnderTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        cpuUnderTest = new CPU(new InstructionMemory(5, 1024), mockMemory, new ParserImpl());
        cpuUnderTest.getRegisters().put("SP", mockSPRegister);
    }

    @Test
    void writeToAddress_DirectMode_WriteToMemory() throws CustomException {
        short location = 100;
        short result = 42;

        cpuUnderTest.writeToAddress("DIRECT", location, result);

        verify(mockMemory).write(location, result);
    }

    @Test
    void writeToAddress_RegisterMode_WriteToRegister() {
        byte location = 1;
        short result = 42;

        try {
            cpuUnderTest.writeToAddress("REGISTER", location, result);
            verify(mockSPRegister, never()).setValue(result);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }


    }

    @Test
    void resolveAddressing_RegisterMode_ReadFromRegister() {
        byte operand = 1;
        short value = 42;

        when(mockSPRegister.getRegisterCode()).thenReturn((byte) 1);
        when(mockSPRegister.getValue()).thenReturn(value);

        Short result = null;
        try {
            result = cpuUnderTest.resolveAddressing("REGISTER", operand);
            assertEquals(value, result);
        } catch (CustomException e) {
            throw new RuntimeException(e);
        }


    }
}
