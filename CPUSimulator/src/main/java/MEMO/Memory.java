package MEMO;

public class Memory {
    private MemoryPage[] pages; // Array to hold pages
    private int pageSize; // Size of each page
    public Memory(int pageCount) {
        pageSize = 1024;
        pages = new MemoryPage[pageCount];
        for (int i = 0; i < pageCount; i++) {
            pages[i] = new MemoryPage();
        }
    }
    private void Initialize(){
        

    }

    // Method to read data from memory
    public byte[] read(int pageNumber, int offset, int length) {
        // Check if the page is in memory, handle page faults if needed
        // Read data from the page
        // Return the data
        return null;
    }

    // Method to write data to memory
    public void write(int pageNumber, int offset, byte[] data) {
        // Check if the page is in memory, handle page faults if needed
        // Write data to the page
    }

    // STACK reserve

}
