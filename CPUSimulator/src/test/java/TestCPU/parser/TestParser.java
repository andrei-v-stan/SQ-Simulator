package TestCPU.parser;

import CPU.parser.Code;
import CPU.parser.Convertor;
import CPU.parser.Statement;
import CPU.Info.AddressingModes;
import CPU.Info.InstructionSet;
import CPU.Info.NamedByte;
import UTILS.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TestParser {

    @BeforeEach
    void setUp() {
    }

    @Test
    void testToBytes_InvalidInstruction_ThrowsException() {
        try (MockedStatic<InstructionSet> mockedInstructionSet = mockStatic(InstructionSet.class)) {
            Statement statement = new Statement("INVALID_OP", "AX", null);
            mockedInstructionSet.when(() -> InstructionSet.searchByName("INVALID_OP")).thenReturn(null);

            Code code = new Code();
            CustomException exception = assertThrows(CustomException.class, () -> {
                code.toBytes(statement);
            });

            assertEquals("Instruction does not exist: INVALID_OP", exception.getMessage());
        }
    }

    @Test
    void testWriteToBytes_InvalidOperand_ThrowsException() {
        byte[] result = new byte[7];
        String invalidOperand = "INVALID";

        Code code = new Code();
        CustomException exception = assertThrows(CustomException.class, () -> {
            code.writeToBytes(result, invalidOperand, false);
        });

        assertEquals("Operand type not recognized: INVALID", exception.getMessage());
    }

    @Test
    void testWriteToBytes_ShortConst_Success() throws CustomException {
        byte[] result = new byte[7];
        String shortConst = "123";
        try (MockedStatic<Convertor> mockedConvertor = mockStatic(Convertor.class);
             MockedStatic<AddressingModes> mockedAddressingModes = mockStatic(AddressingModes.class)) {

            mockedConvertor.when(() -> Convertor.isShortConst(shortConst)).thenReturn(true);
            mockedConvertor.when(() -> Convertor.getBytesFromShort(shortConst)).thenReturn(new byte[]{0x00, 0x7B});
            mockedAddressingModes.when(() -> AddressingModes.getCode("IMMEDIATE")).thenReturn((byte) 0x01);

            Code code = new Code();
            code.writeToBytes(result, shortConst, false);

            assertEquals(0x01, result[1]);
            assertEquals(0x00, result[2]);
            assertEquals(0x7B, result[3]);
        }
    }

    @Test
    void testWriteToBytes_Register_Success() throws CustomException {
        byte[] result = new byte[7];
        String register = "AX";
        try (MockedStatic<Convertor> mockedConvertor = mockStatic(Convertor.class);
             MockedStatic<AddressingModes> mockedAddressingModes = mockStatic(AddressingModes.class)) {

            mockedConvertor.when(() -> Convertor.isRegister(register)).thenReturn(true);
            mockedConvertor.when(() -> Convertor.getBytesFromRegister(register)).thenReturn(new byte[]{0x00, 0x01});
            mockedAddressingModes.when(() -> AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

            Code code = new Code();
            code.writeToBytes(result, register, false);

            assertEquals(0x02, result[1]);
            assertEquals(0x00, result[2]);
            assertEquals(0x01, result[3]);
        }
    }

    @Test
    void testToBytes() throws CustomException {
        Statement statement = new Statement("ADD", "AX", "BX");
        NamedByte addNamedByte = new NamedByte("ADD", (byte) 0x10);
        try (MockedStatic<InstructionSet> mockedInstructionSet = mockStatic(InstructionSet.class);
             MockedStatic<Convertor> mockedConvertor = mockStatic(Convertor.class);
             MockedStatic<AddressingModes> mockedAddressingModes = mockStatic(AddressingModes.class)) {

            mockedInstructionSet.when(() -> InstructionSet.searchByName("ADD")).thenReturn(addNamedByte);
            mockedConvertor.when(() -> Convertor.isRegister("AX")).thenReturn(true);
            mockedConvertor.when(() -> Convertor.getBytesFromRegister("AX")).thenReturn(new byte[]{0x00, 0x01});
            mockedAddressingModes.when(() -> AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

            mockedConvertor.when(() -> Convertor.isRegister("BX")).thenReturn(true);
            mockedConvertor.when(() -> Convertor.getBytesFromRegister("BX")).thenReturn(new byte[]{0x00, 0x02});
            mockedAddressingModes.when(() -> AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

            Code code = new Code();
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
    }

    @Test
    void testWriteToBytes() throws CustomException {
        byte[] result = new byte[7];
        try (MockedStatic<Convertor> mockedConvertor = mockStatic(Convertor.class);
             MockedStatic<AddressingModes> mockedAddressingModes = mockStatic(AddressingModes.class)) {

            mockedConvertor.when(() -> Convertor.isRegister("AX")).thenReturn(true);
            mockedConvertor.when(() -> Convertor.getBytesFromRegister("AX")).thenReturn(new byte[]{0x00, 0x01});
            mockedAddressingModes.when(() -> AddressingModes.getCode("REGISTER")).thenReturn((byte) 0x02);

            Code code = new Code();
            code.writeToBytes(result, "AX", false);

            assertNotNull(result);
            assertEquals(0x02, result[1]);
            assertEquals(0x00, result[2]);
            assertEquals(0x01, result[3]);
        }
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
