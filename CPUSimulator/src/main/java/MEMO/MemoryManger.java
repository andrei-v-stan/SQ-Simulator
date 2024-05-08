package MEMO;

import java.util.List;

public class MemoryManger {

    public static class MemoryLocation{
        public String UsedBy;
        public int pageNumber;
        public int offset;
        public int length;
    }
    public static List<MemoryLocation> memoryLocations;

    public static boolean isLocationUsed(int pageNumber, int offset, int length){

        for(var memoryLocation: memoryLocations){
            if (memoryLocation.pageNumber == pageNumber
                    && !(memoryLocation.offset > offset + length || memoryLocation.offset+ memoryLocation.length < offset))
                return true;
        }
        return false;
    }


}
