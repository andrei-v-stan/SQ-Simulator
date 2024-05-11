package CPU.Info;

import java.util.ArrayList;
import java.util.List;

public class InstructionSet {

    public static List<NamedByte> instructions;
    public static List<String> arithmeticInstr;
    public static List<String> logicalInstr;
    public static List<String> shiftInstr;
    public static List<String> jumpInstr;
    public static List<String> stackInstr;
    public static List<String> ioInstr;
    public static List<String> fctInstr;



    static {
        instructions = new ArrayList<>();
        instructions.add(new NamedByte("MOV", "ARITHMETIC", (byte) 0b00000001));
        instructions.add(new NamedByte("ADD", "ARITHMETIC", (byte) 0b00000010));
        instructions.add(new NamedByte("SUB","ARITHMETIC", (byte) 0b00000011));
        instructions.add(new NamedByte("MUL","ARITHMETIC", (byte) 0b00000100));
        instructions.add(new NamedByte("DIV", "ARITHMETIC", (byte) 0b00000101));
        instructions.add(new NamedByte("NOT", "LOGICAL", (byte) 0b00000110));
        instructions.add(new NamedByte("AND", "LOGICAL",  (byte) 0b00000111));
        instructions.add(new NamedByte("OR", "LOGICAL",  (byte) 0b00001000));
        instructions.add(new NamedByte("XOR", "LOGICAL",  (byte) 0b00001001));
        instructions.add(new NamedByte("SHL", "SHIFT",  (byte) 0b00001010));
        instructions.add(new NamedByte("SHR", "SHIFT", (byte) 0b00001011));
        instructions.add(new NamedByte("CMP", "CMP", (byte) 0b00001100));
        instructions.add(new NamedByte("JMP", "JUMP", (byte) 0b00001101));
        instructions.add(new NamedByte("JE", "JUMP",  (byte) 0b00001110));
        instructions.add(new NamedByte("JNE", "JUMP",  (byte) 0b00001111));
        instructions.add(new NamedByte("JL", "JUMP",  (byte) 0b00010000));
        instructions.add(new NamedByte("JG", "JUMP",  (byte) 0b00010001));
        instructions.add(new NamedByte("JLE", "JUMP",  (byte) 0b00010010));
        instructions.add(new NamedByte("JGE", "JUMP",  (byte) 0b00010011));
        instructions.add(new NamedByte("PUSH", "STACK",  (byte) 0b00010100));
        instructions.add(new NamedByte("POP", "STACK", (byte) 0b00010101));
        instructions.add(new NamedByte("READ", "IO", (byte) 0b00010110));
        instructions.add(new NamedByte("WRITE", "IO",(byte) 0b00010111));
        instructions.add(new NamedByte("CALL", "FCT", (byte) 0b00011000));
        instructions.add(new NamedByte("RET", "FCT",(byte) 0b00011001));// does parser need it?
    }
    public static NamedByte searchByCode(byte lookup){
        for(var instr : instructions)
            if (instr.opcode == lookup)
                return instr;
        return null;
    }

    public static NamedByte searchByName(String name) {
        for(var instr : instructions)
            if (instr.name.equals(name))
                return instr;
        return null;
    }
}
