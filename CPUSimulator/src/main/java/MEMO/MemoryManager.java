package MEMO;

import java.util.List;

public class MemoryManager {

    public static class MemoryLocation{
        public String usedBy;
        public int pageNumber;
        public int offset;
        public int length;
    }
    public static List<MemoryLocation> memoryLocations;

    public static boolean isLocationUsed(short location, int length){
        return isLocationUsed(location/ 1024, location% 1024, length);
    }
    public static boolean isLocationUsed(int pageNumber, int offset, int length){

        for(var memoryLocation: memoryLocations){
            if (memoryLocation.pageNumber == pageNumber
                    && !(memoryLocation.offset > offset + length || memoryLocation.offset+ memoryLocation.length < offset))
                return true;
        }
        return false;
    }
    public static MemoryLocation findMemoryLocationByName(String name){
        for(var memoryLocation: memoryLocations){
            if (memoryLocation.usedBy.equals(name))
                return memoryLocation;
        }
        return null;
    }


}
