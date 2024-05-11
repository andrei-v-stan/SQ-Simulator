package PERIPHS;

import MEMO.Memory;
import java.util.LinkedList;
import java.util.Queue;

public class Keyboard {
    private final Memory memory;
    private final int address;
    private Queue<Byte> buffer = new LinkedList<>();

    public Keyboard(Memory memory, int address) {
        this.memory = memory;
        this.address = address;
    }

    public short read() {
        if (!buffer.isEmpty()) {
            return (short) (buffer.poll() & 0xFF);
        }
        return 0;
    }

    public void write(char key) {
        buffer.add((byte) key);
    }
}
