package MEMO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TestMemoryManager {

    @BeforeEach
    void setUp() {
        MemoryManager.memoryLocations = new ArrayList<>();

        var loc1 = new MemoryManager.MemoryLocation();
        loc1.usedBy = "Process1";
        loc1.pageNumber = 0;
        loc1.offset = 100;
        loc1.length = 50;

        var loc2 = new MemoryManager.MemoryLocation();
        loc2.usedBy = "Process2";
        loc2.pageNumber = 1;
        loc2.offset = 200;
        loc2.length = 50;

        MemoryManager.memoryLocations.add(loc1);
        MemoryManager.memoryLocations.add(loc2);
    }

    @Test
    void testIsLocationUsedWithinBounds() {
        assertTrue(MemoryManager.isLocationUsed((short)100, 50));
        assertTrue(MemoryManager.isLocationUsed(0, 100, 50));
    }

    @Test
    void testIsLocationUsedOutsideBounds() {
        assertFalse(MemoryManager.isLocationUsed((short)150, 50));
        assertFalse(MemoryManager.isLocationUsed(1, 250, 50));
    }

    @Test
    void testIsLocationUsedPartialOverlap() {
        assertTrue(MemoryManager.isLocationUsed((short)120, 40)); // Partial overlap with loc1
        assertTrue(MemoryManager.isLocationUsed(1, 180, 40)); // Partial overlap with loc2
    }

    @Test
    void testFindMemoryLocationByNameExists() {
        MemoryManager.MemoryLocation loc = MemoryManager.findMemoryLocationByName("Process1");
        assertNotNull(loc);
        assertEquals("Process1", loc.usedBy);
        assertEquals(0, loc.pageNumber);
        assertEquals(100, loc.offset);
        assertEquals(50, loc.length);
    }

    @Test
    void testFindMemoryLocationByNameNotExists() {
        MemoryManager.MemoryLocation loc = MemoryManager.findMemoryLocationByName("NonExistentProcess");
        assertNull(loc);
    }

    // Negative tests

    @Test
    void testIsLocationUsedWithInvalidPageNumber() {
        assertFalse(MemoryManager.isLocationUsed(-1, 100, 50)); // Negative page number
        assertFalse(MemoryManager.isLocationUsed(100, 100, 50)); // Page number beyond any reasonable limit
    }

    @Test
    void testIsLocationUsedWithInvalidOffset() {
        assertFalse(MemoryManager.isLocationUsed(0, -100, 50)); // Negative offset
        assertFalse(MemoryManager.isLocationUsed(0, 2000, 50)); // Offset beyond page size
    }

    @Test
    void testIsLocationUsedWithZeroLength() {
        assertFalse(MemoryManager.isLocationUsed(0, 100, 0)); // Zero length
    }

    @Test
    void testIsLocationUsedWithNegativeLength() {
        assertFalse(MemoryManager.isLocationUsed(0, 100, -50)); // Negative length
    }

    @Test
    void testFindMemoryLocationByNameNull() {
        assertThrows(NullPointerException.class, () -> {
            MemoryManager.findMemoryLocationByName(null);
        });
    }

    @Test
    void testFindMemoryLocationByNameEmpty() {
        MemoryManager.MemoryLocation loc = MemoryManager.findMemoryLocationByName("");
        assertNull(loc);
    }
}
