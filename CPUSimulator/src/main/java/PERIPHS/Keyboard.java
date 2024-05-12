package PERIPHS;

import CORE.ConfigFileReader;
import CORE.Configurator;
import MEMO.Memory;
import UTILS.SyncHelper;

import java.util.LinkedList;
import java.util.Queue;

public class Keyboard implements Runnable

{
    private final Memory memory;

    private String input;

    private int currenCharIndex;

    public Keyboard(Memory memory) {
        this.memory = memory;
        this.currenCharIndex= 0;
    }

    public void write(char key) {
        //buffer.add((byte) key);
        var bytes= new byte[1];
        bytes[0]= (byte)key;
        memory.write(Configurator.configFR.getKeyboardBufferPage(),
                Configurator.configFR.getScreenPageOffset(), 8, bytes);

    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public void run() {

     while (true){
         synchronized (SyncHelper.monitor) {
             // Wait for notification
             SyncHelper.waitForNotification();
             // Write character to memory
             write(input.charAt(currenCharIndex));
             currenCharIndex++;
         }
         if (currenCharIndex > input.length())
             break;
     }
    }
}
