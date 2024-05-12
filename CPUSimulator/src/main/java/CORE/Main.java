package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import PERIPHS.Screen;
import UTILS.CustomException;

public class Main {

    public static void main(String[] args) throws CustomException {
        Configurator configurator = new Configurator("D:/facultateM/CSS/SQ-Simulator/CPUSimulator/src/main/resources/config.properties");
       /* PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();

        var keyboardThread= new Thread(Configurator.keyboard);
        var screenThread= new Thread(Configurator.screen);
        keyboardThread.start();
        screenThread.start();*/

        var source = """
                mov ax, 10
                add ax, 240
                mov bx, 'A'
                sub bx, ax
                cmp bx, ax
                je 3
                """;

        Configurator.cpu.loadInstructionMemory(source);
        Configurator.cpu.execute(0, 6);
        Configurator.cpu.registers.values().forEach(x->{System.out.println(x.toString());});
    }
}