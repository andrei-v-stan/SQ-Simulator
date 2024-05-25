package PERIPHS;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        assert textArea != null : "Test area must not be null";

        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        assert b >= Character.MIN_VALUE && b <= Character.MAX_VALUE : "Invalid character code";

        textArea.append(String.valueOf((char)b));
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}