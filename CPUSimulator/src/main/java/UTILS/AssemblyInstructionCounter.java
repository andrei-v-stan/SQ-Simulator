package UTILS;

public class AssemblyInstructionCounter {
    public static int countInstructions(String source) {
        assert source != null && !source.isBlank(): "Source must not be null or empty";

        String[] lines = source.split("\n");
        int count = 0;
        for (String line : lines) {
            String instruction = line.trim();
            if (!instruction.isEmpty()) {
                count++;
            }
        }

        assert count > 0 : "Number of instructions must be greater than 0";

        return count;
    }
}
