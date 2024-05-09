package MEMO;

public class MemoryPage {
    private  byte[] data;

    public MemoryPage(int pageSize) {
        data= new byte[pageSize];
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
