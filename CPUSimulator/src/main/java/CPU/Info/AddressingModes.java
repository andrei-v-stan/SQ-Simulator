package CPU.Info;

import java.util.ArrayList;
import java.util.List;

public class AddressingModes {

    public static List<NamedByte> accessingModes;

    static {
        accessingModes= new ArrayList<>();
        // only second operand
        accessingModes.add(new NamedByte("IMMEDIATE", (byte) 0b00000001));
        accessingModes.add(new NamedByte("DIRECT", (byte) 0b00000010));
        accessingModes.add(new NamedByte("INDIRECT", (byte) 0b00000011));
    }

}
