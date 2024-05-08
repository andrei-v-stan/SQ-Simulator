package CPU;
import CPU.Info.InstructionSet;
import MEMO.InstructionMemory;
import MEMO.MemoryManger;

import  java.util.*;
public class CPU {
    public HashMap<String, Register> registers;
    private ALU alu;
    private InstructionMemory instructionMemory;

    private void Initialize(){
        registers= new HashMap<String, Register>();
        registers.put("AX", new Register((byte) 0b00000001));
        registers.put("BX", new Register((byte) 0b00000010));
        registers.put("CX", new Register((byte) 0b00000011));
        registers.put("DX", new Register((byte) 0b00000100));
        registers.put("SP", new Register((byte) 0b00000101));
        registers.put("PC", new Register((byte) 0b00000110));
    }

    private void execute(int programOffset, int instructionCount){


        for (int currentInstruction=0; currentInstruction<= instructionCount; currentInstruction++){

            var rawInstruction = instructionMemory.readInstruction(currentInstruction + programOffset);

        }
        //while there are instruction
        // InstructionMemory.readInstruction()
        // DECODE Instruction
        // execute Instruction
            // -> Arithm, Logical, read write Memo, funct call, push pop, IO, jumps

    }
    // in the future execute with breakpoints

    private void prepareInstruction(byte[] rawInstruction){
        byte operator = rawInstruction[0];

        // Extract operand specification (2 bits)
        byte firstOperandSpec = rawInstruction[1];

        // Extract operand (16 bits)
        short firstOperand = (short) (((rawInstruction[2] & 0xFF) << 8) | (rawInstruction[3] & 0xFF));

        byte secondOperandSpec= rawInstruction[4];
        short secondOperand = (secondOperandSpec == 0) ? (short) (((rawInstruction[5] & 0xFF) << 8) | (rawInstruction[6] & 0xFF)) : 0;

        // Determine the instruction based on the operator
        String instructionName = null;
        for (var instruction : InstructionSet.instructions) {
            if (instruction.opcode == operator) {
                instructionName = instruction.name;
                break;
            }
        }

        if (instructionName != null) {
            // check if first operand addressing mode is not IMMEDIATE (error)
            // check type of op and you have operands for op
            // check based on op and addressing mode do op
            // cases for memory and registers


        } else {

            System.out.println("Unknown instruction");
        }
    }
}

