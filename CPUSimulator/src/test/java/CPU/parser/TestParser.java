package CPU.parser;

import CPU.parser.Code;
import CPU.parser.Convertor;
import CPU.parser.Statement;
import CPU.Info.AddressingModes;
import CPU.Info.InstructionSet;
import CPU.Info.InstructionSet.NamedByte;
import UTILS.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestParser {
    @Mock
    private InstructionSet mockInstructionSet;

    @Mock
    private AddressingModes mockAddressingModes;

    private Code code;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        code = new Code();
    }

    @Test
    void testToBytes_InvalidInstruction_ThrowsException() {
        Statement statement = new Statement("INVALID_OP", "AX", null);
        when(InstructionSet.searchByName("INVALID_OP")).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            code.toBytes(statement);
        });

        assertEquals("Instruction does not exist: INVALID_OP", exception.getMessage());
    }

    @Test
    void testWriteToBytes_InvalidOperand_ThrowsException() {
        byte[] result = new byte[7];
        String invalidOperand = "INVALID";

        CustomException exception = assertThrows(CustomException.class, () -> {
            code.writeToBytes(result, invalidOperand, false);
        });

        assertEquals("Operand type not recognized: INVALID", exception.getMessage());
    }

    @Test
    void testWriteToBytes_ShortConst_Success() throws CustomException {
        byte[] result = new byte[7];
        String shortConst = "123";
        when(Convertor.isShortConst(shortConst)).thenReturn(true);
        when(Convertor.getBytesFromShort(shortConst)).thenReturn(new byte[]{0x00, 0x7B});
        when(AddressingModes.getCode("IMMEDIATE")).thenReturn((byte) 0x01);

        code.writeToBytes(result, shortConst, false);

        assertEquals(0x01, result[1]);
        assertEquals(0x00, result[2]);
        assertEquals(0x7B, result[3]);
    }

    @Test
    void testWriteToBytes_Register_Success() throws CustomException {
        byte[] result = new byte[7];
        String register = "AX";
        when(Convertor.isRegister(register)).thenReturn(true);
        when(Convertor.getBytesFromRegister(register)).thenReturn(new byte[]{0x00, 0x01});
        when(AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

        code.writeToBytes(result, register, false);

        assertEquals(0x02, result[1]);
        assertEquals(0x00, result[2]);
        assertEquals(0x01, result[3]);
    }

    @Test
    void testToBytes() throws CustomException {
        Statement statement = new Statement("ADD", "AX", "BX");
        when(InstructionSet.searchByName("ADD")).thenReturn(new InstructionSet.NamedByte((byte) 0x10));
        when(Convertor.isRegister("AX")).thenReturn(true);
        when(Convertor.getBytesFromRegister("AX")).thenReturn(new byte[]{0x00, 0x01});
        when(AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

        when(Convertor.isRegister("BX")).thenReturn(true);
        when(Convertor.getBytesFromRegister("BX")).thenReturn(new byte[]{0x00, 0x02});
        when(AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

        byte[] result = code.toBytes(statement);
        assertNotNull(result);
        assertEquals(0x10, result[0]);
        assertEquals(0x02, result[1]);
        assertEquals(0x00, result[2]);
        assertEquals(0x01, result[3]);
        assertEquals(0x02, result[4]);
        assertEquals(0x00, result[5]);
        assertEquals(0x02, result[6]);
    }

    @Test
    void testWriteToBytes() throws CustomException {
        byte[] result = new byte[7];
        when(Convertor.isRegister("AX")).thenReturn(true);
        when(Convertor.getBytesFromRegister("AX")).thenReturn(new byte[]{0x00, 0x01});
        when(AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

        code.writeToBytes(result, "AX", false);

        assertNotNull(result);
        assertEquals(0x02, result[1]);
        assertEquals(0x00, result[2]);
        assertEquals(0x01, result[3]);
    }
}

class ConvertorTest {
    @Test
    void testIsShortConst() {
        assertTrue(Convertor.isShortConst("123"));
        assertFalse(Convertor.isShortConst("abc"));
        assertFalse(Convertor.isShortConst(null));
    }

    @Test
    void testIsCharConst() {
        assertTrue(Convertor.isCharConst("'a'"));
        assertFalse(Convertor.isCharConst("abc"));
        assertFalse(Convertor.isCharConst(null));
    }

    @Test
    void testIsHexConst() {
        assertTrue(Convertor.isHexConst("123h"));
        assertFalse(Convertor.isHexConst("abc"));
        assertFalse(Convertor.isHexConst(null));
    }

    @Test
    void testIsRegister() {
        assertTrue(Convertor.isRegister("AX"));
        assertFalse(Convertor.isRegister("abc"));
        assertFalse(Convertor.isRegister(null));
    }

    @Test
    void testIsDirectMemory() throws CustomException {
        assertTrue(Convertor.isDirectMemory("[123]"));
        assertFalse(Convertor.isDirectMemory("abc"));
        assertFalse(Convertor.isDirectMemory(null));
        assertFalse(Convertor.isDirectMemory("[123h]"));
    }

    @Test
    void testIsIndirectMemory() throws CustomException {
        assertTrue(Convertor.isIndirectMemory("[AX]"));
        assertFalse(Convertor.isIndirectMemory("abc"));
        assertFalse(Convertor.isIndirectMemory(null));
    }

    @Test
    void testGetBytesFromShort() {
        byte[] result = Convertor.getBytesFromShort("123");
        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(123, result[1]);
    }

    @Test
    void testGetBytesFromChar() {
        byte[] result = Convertor.getBytesFromChar("'a'");
        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals('a', result[1]);
    }

    @Test
    void testGetBytesFromHex() {
        byte[] result = Convertor.getBytesFromHex("123h");
        assertNotNull(result);
        assertEquals(0x01, result[0]);
        assertEquals(0x23, result[1]);
    }

    @Test
    void testGetBytesFromRegister() {
        byte[] result = Convertor.getBytesFromRegister("AX");
        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(1, result[1]);
    }

    @Test
    void testGetBytesFromDirectMemory() throws CustomException {
        byte[] result = Convertor.getBytesFromDirectMemory("[123]");
        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(123, result[1]);
    }

    @Test
    void testGetBytesFromIndirectMemory() {
        byte[] result = Convertor.getBytesFromIndirectMemory("[AX]");
        assertNotNull(result);
        assertEquals(0, result[0]);
        assertEquals(1, result[1]);
    }
}
