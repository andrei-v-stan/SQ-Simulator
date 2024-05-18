package CPU.Other;

import CPU.CPU;
import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import MEMO.Memory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

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
            var mockRegisters= mock(Register.class);
    }
}
