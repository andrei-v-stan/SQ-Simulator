package MEMO;

import java.util.Arrays;

public class Memory {
    private MemoryPage[] pages; // Array to hold pages
    private int pageSize; // Size of each page

    public Memory(int pageCount, int pageSize) {
        assert pageCount > 0 : "Page count must be greater than 0";
        assert pageSize > 0 : "Page size must be greater than 0";

        this.pageSize = pageSize;
        pages = new MemoryPage[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pages[i] = new MemoryPage(pageSize);
        }

        assert pages.length == pageCount : "Error at initializing memory pages";
    }

    public Short read(short location, int length) {
        assert length > 0 : "Length must be greater than 0";

        var lookup = read((int) (location) / pageSize, (int) (location) % pageSize, length);
        if (lookup != null)
            return (short) (((lookup[0] & 0xFF) << 8) | (lookup[1] & 0xFF));
        return null;
    }

    // Method to read data from memory
    public byte[] read(int pageNumber, int offset, int length) {
        assert pageNumber > 0 : "Page number must be greater than 0";
        assert length > 0 : "Length must be greater than 0";

        var result = Arrays.copyOfRange(pages[pageNumber].getData(), offset, offset + length);

        assert result.length > 0 : "Read memory must not be empty";

        return result;
    }

    public void write(short location, short data) {
        assert location >= 0 : "Location must not be negative";

        var byteArray = new byte[2];

        byteArray[0] = (byte) ((data >> 8) & 0xFF);
        byteArray[1] = (byte) (data & 0xFF);

        write((int) (location) / pageSize, (int) (location) % pageSize, 2, byteArray);
    }

    public void write(short location, short data, int length) {
        assert location >= 0 : "Location must not be negative";

        var byteArray = new byte[1];

        byteArray[0] = (byte) (data & 0xFF);

        write((int) (location) / pageSize, (int) (location) % pageSize, length, byteArray);
    }

    // Method to write data to memory
    public void write(int pageNumber, int offset, int length, byte[] data) {
        assert pageNumber >= 0 : "Page number must not be negative";
        assert offset <= pageSize : "Offset must not exceed page size";
        assert length > 0 : "Length must be greater than 0";

        System.arraycopy(data, 0, pages[pageNumber].getData(), offset, length);
    }
}