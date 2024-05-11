package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import PERIPHS.Screen;
import UTILS.CustomException;

public class Main {

    public static void main(String[] args) throws CustomException {
        Configurator configurator = new Configurator("CPUSimulator/src/main/resources/config.properties");
        //PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();

        var source = """
                mov ax, 10
                add ax, 240
                mov bx, 'A'
                sub bx, ax
                cmp bx, ax
                je 3
                """;
        Configurator.cpu.loadInstructionMemory(source);
        Configurator.cpu.execute(0, 0);
    }
}