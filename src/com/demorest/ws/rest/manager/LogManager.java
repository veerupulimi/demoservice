package com.demorest.ws.rest.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogManager {
	/** Logger to handle the Errors */
	static Logger errorLog = null;
	
	/** Logger to handle the Informations/Debugs */
	static Logger infoLog = null;
	
	/** Flag to log the Method's starting & ending into InfoLogger */
	static boolean bEnableMethodCallLog = false;
	
	static String LOG4J_PROPERTIES_FILE = "";
	
	static Thread thLogRefresh = null;
	
	/**
	 * Initialize the log4j properties. 
	 * And create the info & error logs.
	 */
	public static void initializePropertyConfigurator(String strLog4jPropertiesFile) {
		LOG4J_PROPERTIES_FILE = strLog4jPropertiesFile;
		
		PropertyConfigurator.configure( LOG4J_PROPERTIES_FILE );
		errorLog = Logger.getLogger("errorLogger");
		infoLog = Logger.getLogger("infoLogger");
		
		reloadConfig();
	}
	
	/**
	 * Reload few properties of the log4j config file, in the interval of given time limit.
	 */
	private static void reloadConfig() {
		thLogRefresh = new Thread(){
			@Override
			public void run() {
				Properties prop = new Properties();
				InputStream is = null;
				boolean bTemp = bEnableMethodCallLog;
				String strLog = null;
				
				while( true ) {
					try {
						File fileLog = new File(LOG4J_PROPERTIES_FILE);
						
						if( fileLog.exists() ) {
							is = new FileInputStream(LOG4J_PROPERTIES_FILE);
							prop.load(is);
							
							strLog = prop.getProperty("ENABLE_METHOD_CALL_LOG");
							strLog = (strLog==null)?"false":strLog;
							
							bTemp = Boolean.parseBoolean(strLog);
							
							if( bTemp != bEnableMethodCallLog ) {
								bEnableMethodCallLog = bTemp;
								System.out.println((new Date())+" Info ENABLE_METHOD_CALL_LOG is "+(bEnableMethodCallLog?"Enabled":"Disabled."));
								infoLog("Info ENABLE_METHOD_CALL_LOG is "+(bEnableMethodCallLog?"Enabled":"Disabled."));
							}
					 		
							// wait for 10 seconds
							Thread.sleep(10000);
						} else {
							System.out.println("Log file not found: "+fileLog.getCanonicalPath());
						}
					} catch(Exception e) {
						System.out.println("Exception in reloadConfig: "+e.getMessage());
						e.printStackTrace();
					} finally {
						prop.clear();
						try {
							is.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};
		thLogRefresh.start();
	}
	
	/**
	 * 
	 * @return
	 */
	public static void stopLogRefresh() {
		if( thLogRefresh != null ) {
			thLogRefresh.interrupt();
		}
	}
	
	/**
	 * Write the error in error.log
	 * 
	 * @param th
	 */
	public static void errorLog(Throwable th) {
		
		StringWriter stack = new StringWriter();
		PrintWriter pw = new PrintWriter(stack);
		
		try {
			th.printStackTrace(pw);
			StringBuilder sb = new StringBuilder("Thread-Id: ").append(Thread.currentThread().getId())
										.append(" <> Exception: ").append(stack.toString());
			errorLog.error(sb);
		} finally {
			pw.close();
			try {
				stack.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			pw = null;
			stack = null;
		}
		
	}
	
	/**
	 * Write the given exception string in error.log
	 * 
	 * @param strException
	 */
	public static void errorLog(String strException) {
		
		StringBuilder sb = new StringBuilder("Thread-Id: ").append(Thread.currentThread().getId())
									.append(" <> Exception: ").append(strException);
		errorLog.error(sb);
	}
	
	/**
	 * Write the errors in error.log, with the given SQL query
	 * 
	 * @param th
	 * @param sbQuery
	 */
	public static void errorLog(Throwable th, StringBuilder sbQuery) {
		
		StringWriter stack = new StringWriter();
		PrintWriter pw = new PrintWriter(stack);
		
		try {
			StringBuilder sb = new StringBuilder("Thread-Id: ").append(Thread.currentThread().getId())
										.append(" <> Exception in Query : ").append(sbQuery);
			errorLog.error(sb);
			
			th.printStackTrace(pw);
			sb.setLength(0);
			sb	.append("Thread-Id: ").append(Thread.currentThread().getId())
				.append(" <> Exception: ").append(stack.toString());
			errorLog.error(sb);
		} finally {
			pw = null;
			stack = null;
		}
		
	}
	
	/**
	 * Write the info in info.log
	 * 
	 * @param strInfo
	 */
	public static void infoLog(String strInfo) {
		StringBuilder sb = new StringBuilder("Thread-Id: ").append(Thread.currentThread().getId())
									.append(" <> Info : ").append(strInfo);
		infoLog.info(sb);
		
	}
	
	/**
	 * Log the called function's start time
	 * 
	 * @return
	 */
	public static Date logMethodStart() {
		String strFunctionName = null;
		Date now = null;
		
		if( bEnableMethodCallLog ){
			now = new Date();
			
			StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
			strFunctionName = ste.getClassName()+"."+ste.getMethodName();
			
			infoLog.info("Starting of "+strFunctionName+" <> Thread-Id: "+Thread.currentThread().getId()+" <> "+now+" <> "+now.getTime());
			
		}
		
		return now;
	}
	
	/**
	 * Log the called function's end time
	 * 
	 * @param dateStart
	 */
	public static void logMethodEnd(Date dateStart) {
		String strFunctionName = null;
		Date now = null;
		
		if( bEnableMethodCallLog && dateStart != null ){
			now = new Date();
			
			StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
			strFunctionName = ste.getClassName()+"."+ste.getMethodName();
			
			infoLog.info("Ending of "+strFunctionName+" <> Thread-Id: "+Thread.currentThread().getId()+" <> "+now+" <> "+now.getTime()+" <> Elapsed: "+(now.getTime()-dateStart.getTime())+" ms.");
			
		}
	}
}
