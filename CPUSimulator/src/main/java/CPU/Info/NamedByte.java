package CPU.Info;

public class NamedByte {

    public String name;
    public String category;
    public byte opcode;

    public NamedByte(String name, byte opcode) {
        this.name = name;
        this.opcode = opcode;
    }

    public NamedByte(String name, String category, byte opcode) {
        this.name = name;
        this.category = category;
        this.opcode = opcode;
    }
}
