package MEMO;

import java.util.List;

public class MemoryManager {

    public static class MemoryLocation {
        public String usedBy;
        public int pageNumber;
        public int offset;
        public int length;
    }

    public static List<MemoryLocation> memoryLocations;

    public static boolean isLocationUsed(short location, int length) {
        assert location >= 0 : "Location must not be negative";
        assert length > 0 : "Length must be greater than 0";

        return isLocationUsed(location / 1024, location % 1024, length);
    }

    public static boolean isLocationUsed(int pageNumber, int offset, int length) {
        assert pageNumber >= 0 : "Page number must not be negative";
        assert length > 0 : "Length must be greater than 0";

        for (var memoryLocation : memoryLocations) {
            if (memoryLocation.pageNumber == pageNumber
                    && !(memoryLocation.offset > offset + length || memoryLocation.offset + memoryLocation.length < offset))
                return true;
        }
        return false;
    }

    public static MemoryLocation findMemoryLocationByName(String name) {
        for (var memoryLocation : memoryLocations) {
            if (memoryLocation.usedBy.equals(name))
                return memoryLocation;
        }
        return null;
    }
}
