package CPU.parser;

import java.util.ArrayList;
import java.util.List;

public class ParserImpl implements Parser {
    private static int currentIndex = 0;
    private static List<String> tokens;

    @Override
    public ASTNode parse(String sourceCode) {
        tokens = tokenize(sourceCode);
        currentIndex = 0;
        return parseProgram();
    }

    private static List<String> tokenize(String sourceCode) {
        return getTokens(sourceCode);
    }

    static List<String> getTokens(String sourceCode) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (char c : sourceCode.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
            } else if (c == ',' || c == '\n' || c == ':') {
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                currentToken.append(c);
            }
        }

        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }

        return tokens;
    }

    private static String getNextToken() {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex++);
        }
        return null;
    }

    private static String peekNextToken() {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex);
        }
        return null;
    }

    private static ASTNode parseProgram() {
        ASTNode programNode = new ASTNode("Program", null, null);

        while (peekNextToken() != null) {
            programNode.addChild(parseLine());
        }

        return programNode;
    }

    private static ASTNode parseLine() {
        String instruction = getNextToken();
        String operand1 = null;
        String operand2 = null;

        if (peekNextToken() != null && peekNextToken().equals(":")) {
            getNextToken();
        } else {
            operand1 = getNextToken();
            if (peekNextToken() != null && peekNextToken().equals(",")) {
                getNextToken();
                operand2 = getNextToken();
            }
        }

        return new ASTNode(instruction, operand1, operand2);
    }

    public static void main(String[] args) {
        String sourceCode = "MOV AX, 10\n" +
                "ADD AX, BX\n" +
                "SUB CX, 5\n" +
                "JMP _start\n" +
                "start:\n" +
                "CALL _start\n";

        var parser = new ParserImpl();
        ASTNode ast = parser.parse(sourceCode);
        System.out.println(ast);
    }
}
