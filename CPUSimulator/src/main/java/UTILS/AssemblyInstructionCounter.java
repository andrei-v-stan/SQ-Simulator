package UTILS;

public class AssemblyInstructionCounter {
    public static int countInstructions(String source) {
        String[] lines = source.split("\n");
        int count = 0;
        for (String line : lines) {
            String instruction = line.trim();
            if (!instruction.isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
