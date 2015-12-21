package com.demorest.ws.rest.servlet;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demorest.ws.rest.connect.DataBaseManager;
import com.demorest.ws.rest.manager.LogManager;
import com.demorest.ws.rest.util.Constants;

/**
 * This class does the initialization operation for this application
 * 
 * @author veeru
 *
 */
public class InitServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public static String version = null;
	public static String realPath = null;

	/**
	 * Do the initialization operation of this application like:
	 * Get the configuration data from configuration files.
	 * 
	 */
	public void init() {
		//super();
   	 	System.out.println("Initializing Sample Application...");
		
		// declare servlet context
		ServletContext context = getServletContext();
		
		realPath = context.getRealPath("//");
		
		try{
			String strConstantsFilePath = context.getInitParameter("CONFIG_PROPERTIES_FILE_PATH");
			String strLog4jFilePath = context.getInitParameter("LOG4J_PROPERTIES_FILE_PATH");
			
			Constants.CONSTANTS_FILE_PATH = InitServlet.realPath+strConstantsFilePath;
			Constants.LOG4J_PROPERTIES_FILE = InitServlet.realPath+strLog4jFilePath;
			
			// Loads log4j configuration properties
			LogManager.initializePropertyConfigurator( Constants.LOG4J_PROPERTIES_FILE );
			// Loads Constant properties 
			Constants.loadConstantsProperties(Constants.CONSTANTS_FILE_PATH);
			
			// Loads db config
			DataBaseManager.doConnectionSetupIfRequired(Constants.APP_CONFIG_FILE_PATH);
			
			
		} catch(Throwable e) {
			LogManager.errorLog(e);
		}
		

	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
