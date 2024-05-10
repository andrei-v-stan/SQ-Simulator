package CORE;

import MEMO.InstructionMemory;
import CPU.CPU;
import MEMO.Memory;

public class Configurator {

    // responsible for initializing objects uses config file reader
    // CPU, Memory, InstructionMemory
    // MemoryManager -> stack, keyboard Buffer, screen

    public static CPU cpu;
    public static Memory memory;
    public static InstructionMemory instructionMemory;

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
        cpu = new CPU(instructionMemory, memory);
    }
}
