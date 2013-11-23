package com.liu.servlets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Conf {
	private static Logger logger = Logger.getLogger(Conf.class);
    private Properties properties;
    private static Conf INSTANCE;
    
    private final static String DEFAULT_CONF_PATH
            = System.getProperty("user.dir") + "/conf.properties";
    private final static String SECRET_KEY = "LIU_SHHKLDGZHZ";
    
    public static Conf getInstance() {
    	if(INSTANCE == null)
    		return new Conf();
    	return INSTANCE;
    }
    
    private Conf() {}

	private InputStream getDefaultConfInputStream() {
        try {
            return new FileInputStream(DEFAULT_CONF_PATH);
        } catch (FileNotFoundException e) {
            logger.error("No configuration file found!");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
	
	private void loadConf(InputStream in) {
        properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public void reload() {
        loadConf(getDefaultConfInputStream());
    }
	
	public String getSecretKey() {
		return SECRET_KEY;
	}
	
	public String getDefaultAppOs() {
		return properties.getProperty("default_os");
	}
}
