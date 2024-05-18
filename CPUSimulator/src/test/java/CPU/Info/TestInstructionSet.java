package CPU.Info;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class TestInstructionSet {


    @BeforeEach
    void setUp(){
    }
    @Test
    void testSearchByCodeExists() {
        NamedByte instr = InstructionSet.searchByCode((byte) 0b00000010);
        assertNotNull(instr);
        assertEquals("ADD", instr.name);
    }

    @Test
    void testSearchByCodeNotExists() {
        NamedByte instr = InstructionSet.searchByCode((byte) 0b11111111);
        assertNull(instr);
    }

    @Test
    void testSearchByNameExists() {
        NamedByte instr = InstructionSet.searchByName("ADD");
        assertNotNull(instr);
        assertEquals((byte) 0b00000010, instr.opcode);
    }

    @Test
    void testSearchByNameNotExists() {
        NamedByte instr = InstructionSet.searchByName("NON_EXISTENT");
        assertNull(instr);
    }

    @Test
    void testIsUnaryOpTrue() {
        NamedByte instr = InstructionSet.searchByName("JMP");
        assertNotNull(instr);
        assertTrue(InstructionSet.isUnaryOp(instr));

        instr = InstructionSet.searchByName("NOT");
        assertNotNull(instr);
        assertTrue(InstructionSet.isUnaryOp(instr));

        instr = InstructionSet.searchByName("READ");
        assertNotNull(instr);
        assertTrue(InstructionSet.isUnaryOp(instr));

        instr = InstructionSet.searchByName("PUSH");
        assertNotNull(instr);
        assertTrue(InstructionSet.isUnaryOp(instr));
    }

    @Test
    void testIsUnaryOpFalse() {
        NamedByte instr = InstructionSet.searchByName("ADD");
        assertNotNull(instr);
        assertFalse(InstructionSet.isUnaryOp(instr));

        instr = InstructionSet.searchByName("AND");
        assertNotNull(instr);
        assertFalse(InstructionSet.isUnaryOp(instr));
    }

    @Test
    void testIsImmediateOpTrue() {
        NamedByte instr = InstructionSet.searchByName("JMP");
        assertNotNull(instr);
        assertTrue(InstructionSet.isImmediateOp(instr));

        instr = InstructionSet.searchByName("CALL");
        assertNotNull(instr);
        assertTrue(InstructionSet.isImmediateOp(instr));

        instr = InstructionSet.searchByName("READ");
        assertNotNull(instr);
        assertTrue(InstructionSet.isImmediateOp(instr));
    }

    @Test
    void testIsImmediateOpFalse() {
        NamedByte instr = InstructionSet.searchByName("ADD");
        assertNotNull(instr);
        assertFalse(InstructionSet.isImmediateOp(instr));

        instr = InstructionSet.searchByName("AND");
        assertNotNull(instr);
        assertFalse(InstructionSet.isImmediateOp(instr));
    }

    // Negative tests

    @Test
    void testSearchByCodeInvalidCode() {
        NamedByte instr = InstructionSet.searchByCode((byte) 0xFF);
        assertNull(instr);
    }

    @Test
    void testSearchByNameNull() {
        NamedByte instr = InstructionSet.searchByName(null);
        assertNull(instr);
    }

    @Test
    void testSearchByNameEmpty() {
        NamedByte instr = InstructionSet.searchByName("");
        assertNull(instr);
    }

    @Test
    void testIsUnaryOpNull() {
        assertThrows(NullPointerException.class, () -> {
            InstructionSet.isUnaryOp(null);
        });
    }

    @Test
    void testIsImmediateOpNull() {
        assertThrows(NullPointerException.class, () -> {
            InstructionSet.isImmediateOp(null);
        });
    }
}
