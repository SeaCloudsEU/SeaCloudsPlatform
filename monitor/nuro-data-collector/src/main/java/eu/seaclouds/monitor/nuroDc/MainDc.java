package eu.seaclouds.monitor.nuroDc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainDc {
    private static final Logger logger = LoggerFactory.getLogger(MainDc.class);

    public String configFilePath = null;

    private static final String CONFIG_FILE_PATH = "resources/config.properties";

    public static void main(String[] args) throws Exception {

        MainDc mainInstance = new MainDc();

        if (args.length > 0) {
            mainInstance.configFilePath = args[0];
        } else {
            mainInstance.configFilePath = CONFIG_FILE_PATH;
        }

        Properties nuroDCProp = new Properties();

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

        Registry.initialize(nuroDCProp);
        Registry.startMonitoring();
    }

}
