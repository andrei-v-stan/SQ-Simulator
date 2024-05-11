package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import CPU.CPU;
import MEMO.Memory;
import PERIPHS.Keyboard;
import PERIPHS.Screen;

public class Configurator {

    public static CPU cpu;
    public static Memory memory;
    public static InstructionMemory instructionMemory;

    public static Keyboard keyboard;
    public static Screen screen;

    public Configurator(String filePath) {

        ConfigFileReader config = new ConfigFileReader(filePath);
        System.out.println("pageSize: " + config.getPageSize());
        System.out.println("nbPagesMemo: " + config.getNbPagesMemo());
        System.out.println("nbPagesInstrMemo: " + config.getNbPagesInstrMemo());

        System.out.println("stackPageNumber: " + config.getStackPageNumber());
        System.out.println("stackBufferPageOffset: " + config.getStackBufferPageOffset());
        System.out.println("stackLength: " + config.getStackLength());

        System.out.println("keyboardBufferPage: " + config.getKeyboardBufferPage());
        System.out.println("keyboardBufferPageOffset: " + config.getKeyboardBufferPageOffset());
        System.out.println("keyboardBufferLength: " + config.getKeyboardBufferLength());

        System.out.println("screenPage: " + config.getScreenPage());
        System.out.println("screenPageOffset: " + config.getScreenPageOffset());
        System.out.println("screenLength: " + config.getScreenLength());
        System.out.println("screenWidth: " + config.getScreenWidth());

        instructionMemory = new InstructionMemory(config.getNbPagesInstrMemo(), config.getPageSize());
        memory = new Memory(config.getNbPagesMemo(), config.getPageSize());
        var parser = new ParserImpl();
        cpu = new CPU(instructionMemory, memory, parser);

        keyboard = new Keyboard(memory, config.getKeyboardBufferPage() * 4096 + config.getKeyboardBufferPageOffset());
        screen = new Screen(memory, config.getScreenPage() * config.getPageSize() + config.getScreenPageOffset(), config.getScreenLength(), config.getScreenWidth());
    }
}
