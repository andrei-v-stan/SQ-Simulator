package CPU.parser;

import UTILS.CustomException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Convertor {
    public static Map<String, Byte> registerCodes;

    static {
        registerCodes = new HashMap<>() {{
            put("AX", (byte) 0b00000001);
            put("BX", (byte) 0b00000010);
            put("CX", (byte) 0b00000011);
            put("DX", (byte) 0b00000100);
            put("EX", (byte) 0b00000101);
            put("FX", (byte) 0b00000110);
            put("GX", (byte) 0b00000111);

            put("HX", (byte) 0b00001000);
            put("SP", (byte) 0b00001001);
            put("PC", (byte) 0b00001010);
        }};
    }

    public static boolean isShortConst(String operand) {
        if (operand == null) {
            return false;
        }

        try {
            Short.parseShort(operand);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCharConst(String operand) {
        if (operand == null) {
            return false;
        }

        var ch = operand.replaceAll("'", "");
        return ch.length() == 1;
    }

    public static boolean isHexConst(String operand) {
        if (operand == null || !operand.endsWith("h")) {
            return false;
        }

        try {
            Short.parseShort(operand.substring(0, operand.length() - 1), 16);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isRegister(String operand) {
        if (operand == null) {
            return false;
        }

        return registerCodes.containsKey(operand.toUpperCase());
    }

    public static boolean isDirectMemory(String operand) throws CustomException {
        if (operand == null) {
            return false;
        }

        if (operand.startsWith("[") && operand.endsWith("]")) {
            var addr = operand.substring(1, operand.length() - 1);
            if (isShortConst(addr) || isHexConst(addr)) {
                if (addr.startsWith("-")) {
                    throw new CustomException("Address cannot have negative value");
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isIndirectMemory(String operand) throws CustomException {
        if (operand == null) {
            return false;
        }

        if (operand.startsWith("[") && operand.endsWith("]")) {
            var addr = operand.substring(1, operand.length() - 1);
            return isRegister(addr);
        }

        return false;
    }

    public static byte[] getBytesFromShort(String operand) {
        var result = new byte[2];
        var value = Short.parseShort(operand);
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);

        return result;
    }

    public static byte[] getBytesFromChar(String operand) {
        var result = new byte[2];
        var value = operand.charAt(1);
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);

        return result;
    }

    public static byte[] getBytesFromHex(String operand) {
        var result = new byte[2];
        var value = Short.parseShort(operand, 16);
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);

        return result;
    }

    public static byte[] getBytesFromRegister(String operand) {
        var result = new byte[2];
        var value = registerCodes.get(operand.toUpperCase());
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);

        return result;
    }

    public static byte[] getBytesFromDirectMemory(String operand) {
        var result = new byte[2];
        var addr = operand.substring(1, operand.length() - 1);
        if (isShortConst(operand)) {
            var value = Short.parseShort(addr);
            result[0] = (byte) (value & 0xFF);
            result[1] = (byte) ((value >> 8) & 0xFF);
        }
        else {
            var value = Short.parseShort(addr, 16);
            result[0] = (byte) (value & 0xFF);
            result[1] = (byte) ((value >> 8) & 0xFF);
        }

        return result;
    }

    public static byte[] getBytesFromIndirectMemory(String operand) {
        var result = new byte[2];
        var register = operand.substring(1, operand.length() - 1);
        var value = registerCodes.get(register);
        result[0] = (byte) (value & 0xFF);
        result[1] = (byte) ((value >> 8) & 0xFF);

        return result;
    }
}
