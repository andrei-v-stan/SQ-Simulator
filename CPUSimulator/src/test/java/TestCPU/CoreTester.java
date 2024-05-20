package TestCPU;

import CORE.ConfigFileReader;
import CORE.Configurator;
import CORE.Main;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CoreTester {

    @Test
    void testConfigurator() {
        Configurator configurator = new Configurator("C:/Users/andre/Documents/GitHub/SQ-Simulator/CPUSimulator/src/main/resources/config.properties");
        assertNotNull(configurator.cpu);
        assertNotNull(configurator.memory);
        assertNotNull(configurator.instructionMemory);
    }
}

class ConfigFileReaderTest {

    @Test
    void testGetPageSize() {
        ConfigFileReader configFileReader = new ConfigFileReader("C:/Users/andre/Documents/GitHub/SQ-Simulator/CPUSimulator/src/main/resources/config.properties");
        int pageSize = configFileReader.getPageSize();
        assertEquals(1024, pageSize);
    }
}

class MainTest {

    @Test
    void testMain() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}