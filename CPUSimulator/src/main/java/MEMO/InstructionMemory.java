package MEMO;

import java.lang.reflect.Array;
import java.util.Arrays;

public class InstructionMemory {

    // Instr-> | ...8bit operator.../.2bit operand spec./ 16 bit operand |
    // Instr->  | ...8bit operator.../.2bit operand spec./ 16 bit operand / .2bit operand spec./ 16 bit operand|

    private MemoryPage[] pages;
    private int instructionCount;

    private  int maxPages;
    private int maxInstructionCountOnPage; // 1024/7
    private  int maxInstructionSize; //44 bits -> 1 byte for spec so ist now 4 or 7 ~  byte
    private  int pageSize;// 1024 byte


    public void writeInstruction(byte[] instruction){

        if (instructionCount >= maxPages * maxInstructionCountOnPage) //No more space to write
            return;

        instructionCount+=1;

        int page= instructionCount / maxInstructionCountOnPage;
        int startCopy = instructionCount % maxInstructionCountOnPage * maxInstructionSize;
        System.arraycopy(instruction,0, pages[page].getData(), startCopy, maxInstructionSize);

    }
    public byte[] readInstruction(int instructionNb){

        byte[] instruction= null;
        if (instructionNb < instructionCount){
            int page= instructionNb / maxInstructionCountOnPage;
            instructionNb = instructionNb % maxInstructionCountOnPage;
            int startCopy = instructionNb * maxInstructionSize;
            instruction = Arrays.copyOfRange( pages[page].getData(), startCopy , startCopy + maxInstructionSize );
        }

        return instruction;
    }



}
