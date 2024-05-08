package main.java.CPU.parser;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {
    String instruction;
    String operand1;
    String operand2;
    List<ASTNode> children;

    public ASTNode(String instruction, String operand1, String operand2) {
        this.instruction = instruction;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringHelper(sb, 0);
        return sb.toString();
    }

    private void toStringHelper(StringBuilder sb, int depth) {
        sb.append("  ".repeat(Math.max(0, depth)));
        sb.append("Instruction: ").append(instruction)
                .append(", Operand1: ").append(operand1)
                .append(", Operand2: ").append(operand2)
                .append("\n");
        for (ASTNode child : children) {
            child.toStringHelper(sb, depth + 1);
        }
    }
}
