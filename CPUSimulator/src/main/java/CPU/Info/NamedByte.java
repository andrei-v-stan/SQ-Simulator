package CPU.Info;

public class NamedByte {

    public String name;
    public String category;
    public byte opcode;

    public NamedByte(String name, byte opcode) {
        assert name != null : "Byte name must not be null";

        this.name = name;
        this.opcode = opcode;
    }

    public NamedByte(String name, String category, byte opcode) {
        assert name != null : "Byte name must not be null";
        assert category != null : "Byte category must not be null";

        this.name = name;
        this.category = category;
        this.opcode = opcode;
    }
}
