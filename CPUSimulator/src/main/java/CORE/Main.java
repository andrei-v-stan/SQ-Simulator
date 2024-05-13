package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import PERIPHS.Screen;
import UTILS.CustomException;

import static UTILS.AssemblyFileReader.readAssemblyFromFile;
import static UTILS.AssemblyInstructionCounter.countInstructions;

public class Main {

    public static void main(String[] args) throws CustomException {
        //var resourcesPath = "D:/facultateM/CSS/SQ-Simulator/CPUSimulator/src/main/resources/";
        var resourcesPath = "CPUSimulator/src/main/resources/";
        Configurator configurator = new Configurator(resourcesPath + "config.properties");
        PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();

        var keyboardThread= new Thread(Configurator.keyboard);
        var screenThread= new Thread(Configurator.screen);
        keyboardThread.start();
        screenThread.start();

        var source = readAssemblyFromFile(resourcesPath + "testcode7.txt");
        System.out.println(source);

        int count = countInstructions(source);
        System.out.println("Number of instructions: " + count);

        Configurator.cpu.loadInstructionMemory(source);

        Configurator.cpu.execute(0, count);// offset 9 for testcode 5
        Configurator.cpu.registers.entrySet().forEach(x->{System.out.println(x.getKey() + " " + x.getValue().toString());});
    }
}