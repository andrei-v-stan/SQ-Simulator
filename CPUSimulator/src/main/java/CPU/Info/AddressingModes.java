package CPU.Info;

import java.util.ArrayList;
import java.util.List;

public class AddressingModes {

    public static List<NamedByte> accessingModes;

    static {
        accessingModes= new ArrayList<>();
        // only second operand
        accessingModes.add(new NamedByte("IMMEDIATE", (byte) 0b00000001)); // const
        accessingModes.add(new NamedByte("DIRECT", (byte) 0b00000010)); // memory
        accessingModes.add(new NamedByte("REGISTER", (byte) 0b00000011)); // register
        accessingModes.add(new NamedByte("INDIRECT", (byte) 0b00000100));// special register
    }
    public static NamedByte searchByCode(byte lookup){
        for(var mode : accessingModes)
            if (mode.opcode == lookup)
                return mode;
        return null;
    }
}

