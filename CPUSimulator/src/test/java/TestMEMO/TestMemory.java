package TestMEMO;

import MEMO.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestMemory {

    private Memory memory;

    @BeforeEach
    void setUp() {
        memory = new Memory(4, 256); // 4 pages, each 256 bytes
    }

    @Test
    void testInitialization() {
        assertNotNull(memory);
        // Further assertions can check the state of memory pages if necessary
    }
    @Test
    void testWriteAndRead() {
        short location = 10;
        short data = 0x1234;

        memory.write(location, data);
        Short readData = memory.read(location, 2);

        assertNotNull(readData);
        assertEquals(data, readData);
    }

    @Test
    void testWriteAndReadAcrossPageBoundaries() {
        short location = 254; // Adjusting to write across page boundary
        short data = 0x5678;

        memory.write(location, data);
        Short readData = memory.read(location, 2);

        assertNotNull(readData);
        assertEquals(data, readData);
    }

    @Test
    void testOutOfBoundsRead() {
        short location = 1024; // This is out of bounds for 4 pages of 256 bytes each
        assertThrows(IndexOutOfBoundsException.class, () ->{
            Short readData = memory.read(location, 2);
        });
    }

    @Test
    void testOutOfBoundsWrite() {
        short location = 1024; // This is out of bounds for 4 pages of 256 bytes each
        short data = (short) 0x9ABC;

        // Writing out of bounds should be handled gracefully, possibly with a no-op or an exception
        // In this example, we don't expect an exception to be thrown, but the data should not be written
        assertThrows(IndexOutOfBoundsException.class, () -> {
                    memory.write(location, data);
                });
    }

    @Test
    void testPartialWriteAndRead() {
        short location = 20;
        short data = 0x00FF;

        memory.write(location, data, 1); // Write only one byte
        byte[] readData = memory.read((int)location / 256, (int)location % 256, 1); // Read one byte

        assertNotNull(readData);
        assertEquals((byte) 0x00FF, readData[0]);
    }

}
