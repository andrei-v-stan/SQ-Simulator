package MEMO;

public class MemoryPage {
    private  byte[] data;

    public MemoryPage() {
        data= new byte[1024];
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
