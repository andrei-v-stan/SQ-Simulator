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
    private  int pageSize;

    public InstructionMemory(int maxPages, int pageSize) {
        assert maxPages > 0 : "Maximum of pages must be greater than 0";
        assert pageSize > 0 : "Page size must be greater than 0";

        this.maxPages = maxPages;
        this.pageSize = pageSize;
        maxInstructionSize= 7;
        maxInstructionCountOnPage= pageSize/ maxInstructionSize;
        pages = new MemoryPage[maxPages];
        for (int i = 0; i < maxPages; i++) {
            pages[i] = new MemoryPage(pageSize);
        }

        assert pages.length == maxPages : "Incorrect number of pages initialized";
    }

    public void writeInstruction(byte[] instruction){
        assert instruction.length == 7 : "Invalid instruction length";

        final int oldInstructionCount = instructionCount;

        if (instructionCount >= maxPages * maxInstructionCountOnPage) //No more space to write
            return;

        int page= instructionCount / maxInstructionCountOnPage;
        int startCopy = instructionCount % maxInstructionCountOnPage * maxInstructionSize;
        System.arraycopy(instruction,0, pages[page].getData(), startCopy, maxInstructionSize);

        instructionCount+=1;

        assert instructionCount > oldInstructionCount : "Error at writing instruction";
    }
    public byte[] readInstruction(int instructionNb){

        byte[] instruction= null;
        if (instructionNb < instructionCount){
            int page= instructionNb / maxInstructionCountOnPage;
            instructionNb = instructionNb % maxInstructionCountOnPage;
            int startCopy = instructionNb * maxInstructionSize;
            instruction = Arrays.copyOfRange( pages[page].getData(), startCopy , startCopy + maxInstructionSize );
        }

        assert instruction != null : "Read instruction must not be null";
        assert instruction.length == 7 : "Invalid read instruction length";

        return instruction;
    }

    public MemoryPage[] getPages() {
        return pages;
    }

    public int getInstructionCount() {
        return instructionCount;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public int getMaxInstructionCountOnPage() {
        return maxInstructionCountOnPage;
    }

    public int getMaxInstructionSize() {
        return maxInstructionSize;
    }

    public int getPageSize() {
        return pageSize;
    }
}
