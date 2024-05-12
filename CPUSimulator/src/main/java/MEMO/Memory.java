package MEMO;

import java.util.Arrays;

public class Memory {
    private MemoryPage[] pages; // Array to hold pages
    private int pageSize; // Size of each page
    public Memory(int pageCount, int pageSize) {
        this.pageSize = pageSize;
        pages = new MemoryPage[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pages[i] = new MemoryPage(pageSize);
        }
    }
    private void Initialize(){


    }
    public Short read(short location, int length){
        var lookup= read((int)(location)/pageSize, (int)(location)%pageSize, length);
        if (lookup != null)
            return (short) (((lookup[0] & 0xFF) << 8) | (lookup[1] & 0xFF));
        return null;
    }

    // Method to read data from memory
    public byte[] read(int pageNumber, int offset, int length) {
        return Arrays.copyOfRange(pages[pageNumber].getData(), offset, length);
    }
    public void write(short location, short data){
        var byteArray = new byte[2];

        byteArray[0] = (byte) ((data >> 8) & 0xFF);
        byteArray[1] = (byte) (data & 0xFF);

        write((int)(location)/pageSize, (int)(location)%pageSize,2, byteArray);
    }
    public void write(short location, short data, int length){
        var byteArray = new byte[1];

        byteArray[0] = (byte) (data & 0xFF);

        write((int)(location)/pageSize, (int)(location)%pageSize, length, byteArray);
    }
    // Method to write data to memory
    public void write(int pageNumber, int offset,int length, byte[] data) {
        System.arraycopy(data,0, pages[pageNumber].getData(), offset, length);
    }



}