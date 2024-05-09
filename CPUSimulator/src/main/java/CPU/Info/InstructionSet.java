package CPU.Info;

import java.util.ArrayList;
import java.util.List;

public class InstructionSet {

    public static List<NamedByte> instructions;

    static {
        instructions = new ArrayList<>();
        instructions.add(new NamedByte("MOV", (byte) 0b00000001));
        instructions.add(new NamedByte("ADD", (byte) 0b00000010));
        instructions.add(new NamedByte("SUB", (byte) 0b00000011));
        instructions.add(new NamedByte("MUL", (byte) 0b00000100));
        instructions.add(new NamedByte("DIV", (byte) 0b00000101));
        instructions.add(new NamedByte("NOT", (byte) 0b00000110));
        instructions.add(new NamedByte("AND", (byte) 0b00000111));
        instructions.add(new NamedByte("OR", (byte) 0b00001000));
        instructions.add(new NamedByte("XOR", (byte) 0b00001001));
        instructions.add(new NamedByte("SHL", (byte) 0b00001010));
        instructions.add(new NamedByte("SHR", (byte) 0b00001011));
        instructions.add(new NamedByte("CMP", (byte) 0b00001100));
        instructions.add(new NamedByte("JMP", (byte) 0b00001101));
        instructions.add(new NamedByte("JE", (byte) 0b00001110));
        instructions.add(new NamedByte("JNE", (byte) 0b00001111));
        instructions.add(new NamedByte("JL", (byte) 0b00010000));
        instructions.add(new NamedByte("JG", (byte) 0b00010001));
        instructions.add(new NamedByte("JLE", (byte) 0b00010010));
        instructions.add(new NamedByte("JGE", (byte) 0b00010011));
        instructions.add(new NamedByte("PUSH", (byte) 0b00010100));
        instructions.add(new NamedByte("POP", (byte) 0b00010101));
        instructions.add(new NamedByte("READ", (byte) 0b00010110));
        instructions.add(new NamedByte("WRITE", (byte) 0b00010111));
        instructions.add(new NamedByte("CALL", (byte) 0b00011000));
    }


}
