package PERIPHS;

import CORE.ConfigFileReader;
import CORE.Configurator;
import MEMO.Memory;
import java.util.LinkedList;
import java.util.Queue;

public class Keyboard implements Runnable

{
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
        /*
        *
        * */
        var bytes= new byte[1];
        bytes[0]= (byte)key;
        memory.write(Configurator.configFR.getKeyboardBufferPage(),
                Configurator.configFR.getScreenPageOffset(), bytes);

    }
    @Override
    public void run() {
    /*
    *
    * while (true)
    *   if(isRead)
    *       write(String[currChr]) <- this string is the input field from ui
    *       currChr ++
    *       isRead= !isRead
    *
    * */
    }
}
