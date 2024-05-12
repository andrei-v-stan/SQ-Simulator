package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import PERIPHS.Screen;
import UTILS.CustomException;

public class Main {

    public static void main(String[] args) throws CustomException {
        Configurator configurator = new Configurator("CPUSimulator/src/main/resources/config.properties");
        PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();

        var keyboardThread= new Thread(Configurator.keyboard);
        var screenThread= new Thread(Configurator.screen);
        keyboardThread.start();
        screenThread.start();

        var source = """
                mov ax, 1
                mov bx, 5
                shl ax, 1
                sub bx, 1
                cmp bx, 0
                jne 3
                """;

        Configurator.cpu.loadInstructionMemory(source);
        Configurator.cpu.execute(0, 6);
        Configurator.cpu.registers.entrySet().forEach(x->{System.out.println(x.getKey() + " " + x.getValue().toString());});
        /*System.out.println(
        Configurator.memory.read(Configurator.configFR.getScreenPage(),0,
                Configurator.configFR.getScreenLength()* Configurator.configFR.getScreenWidth())[1]
        );*/
    }
}