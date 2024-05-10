package CORE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ConfigFileReader {

    /*  pagesize = 1024
        nbPagesMemo = (pana in 2^6)
        nbPagesInstrMemo = (pana in 2^6)
        stackPageNumber=
        stackBufferPageOffset=
        stackLength=
        keyboardBufferPage=
        keyboardBufferPageOffset=
        keyboardBufferLength
        screenPage
        screenPageOffset
        screenLength
        screenWidth
        more may be added
*/
    // checks for some restrains
    // throw new CustomException("Config file bad idk");

    private int pageSize = 1024;
    private int nbPagesMemo;
    private int nbPagesInstrMemo;
    private int stackPageNumber;
    private int stackBufferPageOffset;
    private int stackLength;
    private int keyboardBufferPage;
    private int keyboardBufferPageOffset;
    private int keyboardBufferLength;
    private int screenPage;
    private int screenPageOffset;
    private int screenLength;
    private int screenWidth;

    public ConfigFileReader(String filePath) {
        loadConfig(filePath);
    }

    private void loadConfig(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.contains("=")) {
                    String[] parts = line.split("=");
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        switch (key) {
                            case "pagesize":
                                pageSize = Integer.parseInt(value);
                                break;
                            case "nbPagesMemo":
                                nbPagesMemo = Integer.parseInt(value);
                                break;
                            case "nbPagesInstrMemo":
                                nbPagesInstrMemo = Integer.parseInt(value);
                                break;
                            case "stackPageNumber":
                                stackPageNumber = Integer.parseInt(value);
                                break;
                            case "stackBufferPageOffset":
                                stackBufferPageOffset = Integer.parseInt(value);
                                break;
                            case "stackLength":
                                stackLength = Integer.parseInt(value);
                                break;
                            case "keyboardBufferPage":
                                keyboardBufferPage = Integer.parseInt(value);
                                break;
                            case "keyboardBufferPageOffset":
                                keyboardBufferPageOffset = Integer.parseInt(value);
                                break;
                            case "keyboardBufferLength":
                                keyboardBufferLength = Integer.parseInt(value);
                                break;
                            case "screenPage":
                                screenPage = Integer.parseInt(value);
                                break;
                            case "screenPageOffset":
                                screenPageOffset = Integer.parseInt(value);
                                break;
                            case "screenLength":
                                screenLength = Integer.parseInt(value);
                                break;
                            case "screenWidth":
                                screenWidth = Integer.parseInt(value);
                                break;
                            default:
                                System.err.println("Unknown key: " + key);
                                break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getNbPagesMemo() {
        return nbPagesMemo;
    }

    public int getNbPagesInstrMemo() {
        return nbPagesInstrMemo;
    }

    public int getStackPageNumber() {
        return stackPageNumber;
    }

    public int getStackBufferPageOffset() {
        return stackBufferPageOffset;
    }

    public int getStackLength() {
        return stackLength;
    }

    public int getKeyboardBufferPage() {
        return keyboardBufferPage;
    }

    public int getKeyboardBufferPageOffset() {
        return keyboardBufferPageOffset;
    }

    public int getKeyboardBufferLength() {
        return keyboardBufferLength;
    }

    public int getScreenPage() {
        return screenPage;
    }

    public int getScreenPageOffset() {
        return screenPageOffset;
    }

    public int getScreenLength() {
        return screenLength;
    }

    public int getScreenWidth() {
        return screenWidth;
    }
}
