package com.demorest.ws.rest.service;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This class does the initialization operation for this application
 * 
 * @author Ramkumar R
 *
 */
public class InitServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public static String version = null;
	public static String realPath = null;

	/**
	 * Do the initialization operation of this application like:
	 * - Get the configuration data from configuration files.
	 * - Schedule & Start all the counter class timers.
	 * 
	 */
	public void init() {
		//super();
   	 	System.out.println("Initializing Appedo Collector Web App...");
		
		// declare servlet context
		ServletContext context = getServletContext();
		
		realPath = context.getRealPath("//");
		version = (String) context.getInitParameter("version");
		
		try{
			
			
		} catch(Throwable e) {
			
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
