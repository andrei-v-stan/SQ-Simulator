package CPU;

public class Register {
    private short value;
    private byte registerCode;
    public Register(short value, byte registerCode) {
        this.value = value;
        this.registerCode= registerCode;
    }

    public Register(byte registerCode) {
        this.registerCode = registerCode;
    }

    public short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public byte getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(byte registerCode) {
        this.registerCode = registerCode;
    }

    @Override
    public String toString() {
        return "Register{" +
                "value=" + value +
                ", registerCode=" + registerCode +
                '}';
    }
}
