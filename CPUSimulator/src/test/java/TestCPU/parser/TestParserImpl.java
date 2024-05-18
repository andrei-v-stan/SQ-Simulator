package TestCPU.parser;

import CPU.parser.Code;
import CPU.parser.ParserImpl;
import CPU.parser.Statement;
import MEMO.InstructionMemory;
import UTILS.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestParserImpl {
    private ParserImpl parser;
    private InstructionMemory instructionMemory;
    private Code codeMock;

    @BeforeEach
    void setUp() {
        parser = new ParserImpl();
        instructionMemory = mock(InstructionMemory.class);
        codeMock = mock(Code.class);
    }

    @Test
    void testLoadInstructionMemory_ValidSourceCode() throws CustomException {
        String sourceCode = "ADD AX, BX\nSUB CX, DX";

        Statement statement1 = new Statement("ADD", "AX", "BX");
        Statement statement2 = new Statement("SUB", "CX", "DX");

        List<Statement> statements = List.of(statement1, statement2);
        when(codeMock.statements).thenReturn(statements);
        when(codeMock.toBytes(statement1)).thenReturn(new byte[]{0x01});
        when(codeMock.toBytes(statement2)).thenReturn(new byte[]{0x02});

        ParserImpl parserSpy = spy(parser);
        doReturn(codeMock).when(parserSpy).parse(anyString());

        parserSpy.loadInstructionMemory(instructionMemory, sourceCode);

        verify(instructionMemory).writeInstruction(new byte[]{0x01});
        verify(instructionMemory).writeInstruction(new byte[]{0x02});
    }

    @Test
    void testLoadInstructionMemory_InvalidSourceCode() {
        String sourceCode = "INVALID AX, BX";
        assertThrows(CustomException.class, () -> parser.loadInstructionMemory(instructionMemory, sourceCode));
    }

    @Test
    void testParse_ValidSourceCode() throws CustomException {
        String sourceCode = "ADD AX, BX\nSUB CX, DX";
        var code = parser.parse(sourceCode);

        assertNotNull(code);
        assertEquals(2, code.statements.size());
        assertEquals("ADD", code.statements.get(0).operation);
        assertEquals("SUB", code.statements.get(1).operation);
    }

    @Test
    void testParse_InvalidSourceCode() {
        String sourceCode = "INVALID\nADD AX, BX\nMOV CX,";
        CustomException exception = assertThrows(CustomException.class, () -> parser.parse(sourceCode));
        assertEquals("Invalid instruction syntax, line: 0", exception.getMessage());
    }

    @Test
    void testGetRawInstruction_ValidInstruction() throws CustomException {
        String line = "ADD AX, BX";
        Statement statement = parser.getRawInstruction(line, 1);

        assertNotNull(statement);
        assertEquals("ADD", statement.operation);
        assertEquals("AX", statement.operand1);
        assertEquals("BX", statement.operand2);
    }

    @Test
    void testGetRawInstruction_InvalidInstruction() {
        String line = "ADD AX BX";
        CustomException exception = assertThrows(CustomException.class, () -> parser.getRawInstruction(line, 1));
        assertEquals("Invalid instruction syntax, line: 1", exception.getMessage());
    }

    @Test
    void testGetRawInstruction_MissingOperands() {
        String line = "ADD";
        CustomException exception = assertThrows(CustomException.class, () -> parser.getRawInstruction(line, 1));
        assertEquals("Invalid instruction syntax, line: 1", exception.getMessage());
    }
}
