package PERIPHS;

import CORE.Configurator;
import MEMO.Memory;

public class Screen implements Runnable{
    private final Memory memory;
    private final int videoMemoryAddress;
    private final int screenLength;
    private final int screenWidth;

    public Screen(Memory memory, int videoMemoryAddress, int screenLength, int ScreenWidth) {
        assert memory != null : "Memory is not initialized";
        assert videoMemoryAddress >= 0 : "Invalid video memory address";
        assert screenLength > 0 : "Screen length must be greater than 0";
        assert ScreenWidth > 0 : "Screen width must be greater than 0";

        this.memory = memory;
        this.videoMemoryAddress = videoMemoryAddress;
        this.screenLength = screenLength;
        this.screenWidth = ScreenWidth;
    }

    public int getLength() {
        return screenLength;
    }

    public int getWidth() {
        return screenWidth;
    }

    public void write(int position, byte value) {
        assert position >= 0 && position < screenLength * screenWidth : "Position out of bounds";

        memory.write((short) (videoMemoryAddress + position), (short) (value & 0xFF));
    }

    @Override
    public void run() {
        /* every x seconds read from

        var bytes= memory.read(Configurator.configFR.getScreenPage(), Configurator.configFR.getScreenPageOffset(),
                Configurator.configFR.getScreenLength() * Configurator.configFR.getScreenWidth());
         from short read least significant byte (maybe conversion to byte not sure)
         write to string
         maybe use stringBuilder
        new String(bytes);
         */
        while (true) {
            try {
                // Read from memory
                byte[] bytes = memory.read(Configurator.configFR.getScreenPage(), Configurator.configFR.getScreenPageOffset(),
                        Configurator.configFR.getScreenLength() * Configurator.configFR.getScreenWidth());

                // Convert bytes to string
                StringBuilder stringBuilder = new StringBuilder();
                int count= 0;
                for (byte aByte : bytes) {
                    // Convert short (16-bit) to byte (8-bit), considering only least significant byte
                    byte leastSignificantByte = (byte) (aByte & 0xFF);
                    // Append byte to StringBuilder
                    stringBuilder.append((char) leastSignificantByte);
                    if (count == screenWidth){
                        stringBuilder.append('\n');
                        count=0;
                    }
                    count+=1;
                }
                String screenString = stringBuilder.toString();
                SimulatorFrame.screenOutput.setText(screenString);

                // Process the screenString as needed (e.g., display on UI)

                // Sleep for x seconds
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}