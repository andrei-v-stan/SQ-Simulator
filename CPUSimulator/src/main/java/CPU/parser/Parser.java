package CPU.parser;

import MEMO.InstructionMemory;
import UTILS.CustomException;

public interface Parser {
    void loadInstructionMemory(InstructionMemory instructionMemory, String sourceCode) throws CustomException;
}
