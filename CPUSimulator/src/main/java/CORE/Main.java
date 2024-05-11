package CORE;

import CPU.parser.ParserImpl;
import MEMO.InstructionMemory;
import PERIPHS.Screen;
import UTILS.CustomException;

public class Main {

    public static void main(String[] args) throws CustomException {
        Configurator configurator = new Configurator("CPUSimulator/src/main/resources/config.properties");
        PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();
    }
}