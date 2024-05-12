package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import PERIPHS.Screen;
import UTILS.CustomException;

import static UTILS.AssemblyFileReader.readAssemblyFromFile;
import static UTILS.AssemblyInstructionCounter.countInstructions;

public class Main {

    public static void main(String[] args) throws CustomException {
        var resourcesPath = "CPUSimulator/src/main/resources/";
        Configurator configurator = new Configurator(resourcesPath + "config.properties");
        PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();

        var keyboardThread= new Thread(Configurator.keyboard);
        var screenThread= new Thread(Configurator.screen);
        keyboardThread.start();
        screenThread.start();

        var source = readAssemblyFromFile(resourcesPath + "testcode5.txt");
        System.out.println(source);

        int count = countInstructions(source);
        System.out.println("Number of instructions: " + count);

        Configurator.cpu.loadInstructionMemory(source);
        Configurator.cpu.execute(7, count);
        Configurator.cpu.registers.entrySet().forEach(x->{System.out.println(x.getKey() + " " + x.getValue().toString());});
        /*System.out.println(
        Configurator.memory.read(Configurator.configFR.getScreenPage(),0,
                Configurator.configFR.getScreenLength()* Configurator.configFR.getScreenWidth())[1]
        );*/
    }
}