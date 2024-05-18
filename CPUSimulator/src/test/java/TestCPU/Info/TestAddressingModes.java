package TestCPU.Info;

import CPU.Info.AddressingModes;
import CPU.Info.NamedByte;
import UTILS.CustomException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestAddressingModes {

    @Test
    void testSearchByCodeExists() {
        NamedByte mode = AddressingModes.searchByCode((byte) 0b00000010);
        assertNotNull(mode);
        assertEquals("DIRECT", mode.name);
    }

    @Test
    void testSearchByCodeNotExists() {
        NamedByte mode = AddressingModes.searchByCode((byte) 0b11111111);
        assertNull(mode);
    }

    @Test
    void testGetCodeExists() {
        assertDoesNotThrow(() -> {
            byte code = AddressingModes.getCode("REGISTER");
            assertEquals((byte) 0b00000011, code);
        });
    }

    @Test
    void testGetCodeNotExists() {
        assertThrows(CustomException.class, () -> {
            AddressingModes.getCode("INVALID_MODE");
        });
    }

    @Test
    void testGetCodeNull() {
        assertThrows(CustomException.class, () -> {
            AddressingModes.getCode(null);
        });
    }

    @Test
    void testGetCodeEmpty() {
        assertThrows(CustomException.class, () -> {
            AddressingModes.getCode("");
        });
    }
}
