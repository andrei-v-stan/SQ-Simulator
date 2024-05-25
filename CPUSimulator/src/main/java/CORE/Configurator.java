package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import CPU.CPU;
import MEMO.Memory;
import PERIPHS.Keyboard;
import PERIPHS.Screen;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Configurator {

    public static CPU cpu;
    public static Memory memory;
    public static InstructionMemory instructionMemory;

    public static ConfigFileReader configFR;
    public static Keyboard keyboard;
    public static Screen screen;

    public Configurator(String filePath) {
        assert filePath != null : "Filepath must not be null";
        assert Files.exists(Paths.get(filePath));

        configFR = new ConfigFileReader(filePath);
        System.out.println("pageSize: " + configFR.getPageSize());
        System.out.println("nbPagesMemo: " + configFR.getNbPagesMemo());
        System.out.println("nbPagesInstrMemo: " + configFR.getNbPagesInstrMemo());

        System.out.println("stackPageNumber: " + configFR.getStackPageNumber());
        System.out.println("stackBufferPageOffset: " + configFR.getStackBufferPageOffset());
        System.out.println("stackLength: " + configFR.getStackLength());

        System.out.println("keyboardBufferPage: " + configFR.getKeyboardBufferPage());
        System.out.println("keyboardBufferPageOffset: " + configFR.getKeyboardBufferPageOffset());
        System.out.println("keyboardBufferLength: " + configFR.getKeyboardBufferLength());

        System.out.println("screenPage: " + configFR.getScreenPage());
        System.out.println("screenPageOffset: " + configFR.getScreenPageOffset());
        System.out.println("screenLength: " + configFR.getScreenLength());
        System.out.println("screenWidth: " + configFR.getScreenWidth());

        instructionMemory = new InstructionMemory(configFR.getNbPagesInstrMemo(), configFR.getPageSize());
        memory = new Memory(configFR.getNbPagesMemo(), configFR.getPageSize());
        var parser = new ParserImpl();
        cpu = new CPU(instructionMemory, memory, parser);

        keyboard = new Keyboard(memory);
        screen = new Screen(memory, configFR.getScreenPage() * configFR.getPageSize() + configFR.getScreenPageOffset(), configFR.getScreenLength(), configFR.getScreenWidth());
    }
}
