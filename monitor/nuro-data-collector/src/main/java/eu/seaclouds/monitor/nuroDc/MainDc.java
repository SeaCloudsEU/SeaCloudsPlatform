package eu.seaclouds.monitor.nuroDc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainDc {
    private static final Logger logger = LoggerFactory.getLogger(MainDc.class);

    public String managerIp = null;

    public String managerPort = null;

    public String configFilePath = null;

    private static final String DEFAULT_MANAGER_IP = "localhost";
    private static final String DEFAULT_MANAGER_PORT = "8170";
    private static final String DEFAULT_CONFIG_FILE_PATH = "config.properties";
    private static final String ENV_VAR_MANAGER_IP = "MANAGER_IP";
    private static final String ENV_VAR_MANAGER_PORT = "MANAGER_PORT";
    private static final String ENV_VAR_URL_CONFIG_FILE = "NURODC_CONFIG_FILE";

    public static void main(String[] args) throws Exception {

        MainDc mainInstance = new MainDc();

        Properties nuroDCProp = new Properties();

        // load default values
        mainInstance.loadDefaultValues();

        // try to load from environment variables
        mainInstance.loadFromEnrivonmentVariables();

        // load properties from the config file
        try {
            nuroDCProp.load(new FileInputStream(mainInstance.configFilePath));
        } catch (FileNotFoundException ex) {
            logger.error("Properties file not found");
            throw new RuntimeException("Properties file not found");
        } catch (IOException ex) {
            logger.error("Error while parsing properties file");
            throw new RuntimeException("Error while parsing properties file");
        }

        int port;

        // convert manager port
        try {
            port = Integer.parseInt(mainInstance.managerPort);
        } catch (NumberFormatException ex) {
            logger.error("Error while converting manager port - must be an integer");
            throw new RuntimeException("Error while parsing properties file");
        }

        Registry.initialize(mainInstance.managerIp, port, nuroDCProp);
        Registry.startMonitoring();
    }

    private void loadDefaultValues() {
        managerIp = DEFAULT_MANAGER_IP;
        managerPort = DEFAULT_MANAGER_PORT;
        configFilePath = DEFAULT_CONFIG_FILE_PATH;
    }

    private void loadFromEnrivonmentVariables() {

        if (System.getenv(ENV_VAR_MANAGER_IP) != null)
            managerIp = System.getenv(ENV_VAR_MANAGER_IP);

        if (System.getenv(ENV_VAR_MANAGER_PORT) != null)
            managerPort = System.getenv(ENV_VAR_MANAGER_PORT);

        if (System.getenv(ENV_VAR_URL_CONFIG_FILE) != null)
            configFilePath = System.getenv(ENV_VAR_URL_CONFIG_FILE);

    }

}
