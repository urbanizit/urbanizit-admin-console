package org.urbanizit.adminconsole.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanizit.adminconsole.core.ConfigurationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author nicolasger
 */
public class PropertiesConfigurationService implements ConfigurationService {

    private Properties props;
    private static final Logger logger = LoggerFactory.getLogger(PropertiesConfigurationService.class);

    public PropertiesConfigurationService() throws IOException {
        init();
    }

    public void init() throws IOException {
        props = new Properties();
        String configFile = System.getProperty("config.path") + File.separatorChar + "urbanizit.properties";
        logger.info("Loading configuration file : {}", configFile);
        try {
            props.load(new FileInputStream(configFile));
        } catch (IOException e) {
            logger.error("Error loading configuration file: ", e);
            throw e;
        }
    }

    @Override
    public String getDatabaseURI() {
        return props.getProperty("neo4j.db.url");
    }
}