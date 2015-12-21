package com.demorest.ws.rest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.demorest.ws.rest.manager.LogManager;

/**
 * This class holds the application level variables which required through the application.
 * 
 * @author Veeru
 *
 */
public class Constants {

	public static String RESOURCE_PATH = null;
	public static String CONSTANTS_FILE_PATH = null;
	public static String APP_CONFIG_FILE_PATH = null;
	public static String LOG4J_PROPERTIES_FILE;
	/**
	 * Loads constants properties 
	 * 
	 * @param srtConstantsPath
	 */
	public static void loadConstantsProperties(String srtConstantsPath) {
    	Properties prop = new Properties();
    	InputStream is = null;
    	
        try {
    		is = new FileInputStream(srtConstantsPath);
    		prop.load(is);
    		
     		//application's resource directory path
     		RESOURCE_PATH = prop.getProperty("RESOURCE_PATH");     		
     		APP_CONFIG_FILE_PATH = RESOURCE_PATH+prop.getProperty("APP_CONFIG_FILE_PATH");
     		
     		
        } catch(Exception e) {
        	LogManager.errorLog(e);
        } finally {
        	prop.clear();
        	try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				LogManager.errorLog(e);
			}
        }
	}

}
