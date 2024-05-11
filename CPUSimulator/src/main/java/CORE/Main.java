package CORE;

import PERIPHS.Screen;

public class Main {

    public static void main(String[] args) {
        Configurator configurator = new Configurator("CPUSimulator/src/main/resources/config.properties");
        PERIPHS.SimulatorFrame simulatorFrame = new PERIPHS.SimulatorFrame();
    }
}