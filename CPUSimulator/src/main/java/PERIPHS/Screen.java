package PERIPHS;

import MEMO.Memory;

public class Screen {
    private final Memory memory;
    private final int videoMemoryAddress;
    private final int screenLength;
    private final int screenWidth;

    public Screen(Memory memory, int videoMemoryAddress, int screenLength, int ScreenWidth) {
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
        if (position < 0 || position >= screenLength * screenWidth) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        memory.write((short) (videoMemoryAddress + position), (short) (value & 0xFF));
    }
}