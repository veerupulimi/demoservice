package com.demorest.ws.rest.connect;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

import com.demorest.ws.rest.manager.LogManager;

/**
 * Manager for the DataBase connections with a connection-pooling.
 * 
 */
public class DataBaseManager {

	/**
	 * Connection detail parameters
	 * 
	 */
	private static String driver = "";
	private static String protocal = "";
	private static String port = "";
	private static String dbHostName = "";
	private static String dbName = "";
	private static String userName = "";
	private static String password = "";
	private static int maxActive = 10;
	private static int maxWait = 500;
	private static boolean autoCommit = false;
	private static Boolean connEstb = false;

	/**
	 * Objects for functionality
	 * 
	 */
	public static Connection con;
    private static GenericObjectPool connectionPool = null;
	private static PoolingDataSource dataSource = null;

	/**
	 * Get the connection details from a property file given.
	 * 
	 * @param configfilePath
	 * @throws Exception
	 */
	public static void getDetails(String configfilePath) throws Exception{
		Properties prop = new Properties();
		InputStream is = new FileInputStream( configfilePath );
		prop.load(is);
		driver		= prop.getProperty("DRIVER");
		protocal	= prop.getProperty("PROTOCAL");
		dbHostName	= prop.getProperty("DBHOST");
		port		= prop.getProperty("LOCALLISTERNPORT");
		dbName		= prop.getProperty("DBNAME");
		userName	= prop.getProperty("DBUSERNAME");
		password	= prop.getProperty("DBPASSWORD");
		maxActive	= Integer.parseInt(prop.getProperty("MAXACTIVE"));
		maxWait		= Integer.parseInt(prop.getProperty("MAXWAIT"));
		autoCommit	= Boolean.parseBoolean(prop.getProperty("AUTOCOMMIT"));
	}
	
	/**
	 * Create a connection if no connection has been established previously.
	 * 
	 * @param configfilePath
	 * @return
	 * @throws Exception
	 */
	public static boolean doConnectionSetupIfRequired(String configfilePath) throws Exception{
		boolean b = false;
		if( !connEstb ){
			b = connect(configfilePath);
		}
		return b;
	}
	
	/**
	 * Connect the database with the properties file details.
	 * And create the Connection Pool.
	 * 
	 * This will execute only if connection is already not created.
	 * 
	 * @param configFilePath
	 * @return Connection creation status
	 * @throws Exception
	 */
    private static boolean connect(String configFilePath) throws Exception{
        try {
        	synchronized (connEstb) {
        		if( ! connEstb ) {
        			// get the db & pooling configurations from the config file.
        			getDetails(configFilePath);
					
        			// create connection pool
					connectionPool = new GenericObjectPool(null);
					connectionPool.setMaxActive(maxActive);
					connectionPool.setMaxWait(maxWait);
					
		        	Class.forName(driver).newInstance();
		        	String dbURL = "jdbc:"+protocal+"://"+dbHostName+":"+port+"/"+dbName+"?autoReconnect=true";
					String connectURI = dbURL+"&user="+userName+"&password="+password;
					//System.out.println("connectURI: "+connectURI);
					// create Connection factory
					ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);
					
					// create the PoolableConnectionFactory
					PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
							connectionFactory, connectionPool, null, null, false, true);
					
					// set connection factory
					connectionPool.setFactory(poolableConnectionFactory);
					
					// create pool data source
					dataSource = new PoolingDataSource(connectionPool);
					dataSource.setAccessToUnderlyingConnectionAllowed(true);
					
					
					connEstb = true;
        		}
        	}
        } catch (Exception e) {
        	LogManager.errorLog(e);
            throw e;
        }
        return true;
    }

	
	/**
	 * Returns a connection from the Pool.
	 * 
	 * @return
	 */
    public static Connection giveConnection() {
		Connection con = null;
		try{
			con = dataSource.getConnection();
			con.setAutoCommit(autoCommit);
			
			// gmt_differance is a connection variable now
			// it will have the MySQL server to GMT difference
			// Eg.: if MySQL server is EDT then gmt_differance will get -4:00:00
			//con.createStatement().execute("set @gmt_differance = timediff(now(),convert_tz(now(),@@session.time_zone,'+00:00'))");
		} catch (Exception e) {
			LogManager.errorLog(e);
		}
		return con;
    }
    
    /**
     * Prints the status of the Connection-Pool.
     *
     */
	public static void printStatus(){
		LogManager.infoLog("returning static dataSource: "+connectionPool.getNumIdle()+"<>"+connectionPool.getNumActive());
	}
	
	/**
	 * Check whether connection is alive or not.
	 * 
	 * @param con
	 * @return
	 * @throws SQLException
	 */
	public static boolean isConnectionExists(Connection con) throws SQLException {
		Statement stmt = null;
		
		try{
			stmt = con.createStatement();
			stmt.execute("SELECT 1");
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			DataBaseManager.close(stmt);
			stmt = null;
		} 
	}
	
	/**
	 * Re-Establish the connections, if they are not active.
	 * 
	 * @throws SQLException 
	 */
	public static Connection reEstablishConnection(Connection con) throws SQLException {
		Connection connNew = con;
		
		while( connNew == null || ! isConnectionExists(connNew) ) {
			try{
				Thread.sleep(3000);
				LogManager.infoLog("Trying to establish DB connection.");
				connNew = giveConnection();
			} catch(Exception exConReEstablish) {
				LogManager.errorLog(exConReEstablish);
				LogManager.infoLog("unlable to ReEstablish DB connection: "+exConReEstablish.getMessage());
			}
		}
		
		LogManager.infoLog("Connection re-established");
		return connNew;
	}
	
	/**
	 * Close a ResultSet if it has an object in its reference
	 * 
	 * @param rst
	 * @return
	 */
	public static boolean close(ResultSet rst){
		try{
			if( rst != null )
				rst.close();
		}catch(Exception e){
			LogManager.errorLog(e);
			return false;
		}
		return true;
	}

	/**
	 * Close a Statement if it has an object in its reference
	 * 
	 * @param sta
	 * @return
	 */
	public static boolean close(Statement sta){
		try{
			if( sta != null )
				sta.close();
		}catch(Exception e){
			LogManager.errorLog(e);
			return false;
		}
		return true;
	}

	/**
	 * Close a Connection if it has an object in its reference
	 * 
	 * @param conn
	 * @return
	 */
	public static boolean close(Connection conn){
		try{
			if( conn != null )
				conn.close();
		}catch(Exception e){
			LogManager.errorLog(e);
			return false;
		}
		return true;
	}
	
	public static void rollback(Connection con) {
		try {
			con.rollback();
		} catch (SQLException e) {
			LogManager.errorLog(e);
		}
	}
	


    /**
     * Insert the data with given Query. Then extract the auto-generated key(primary key) from Statement and return it.
     * 
     * @param con
     * @param strQuery
     * @return
     */
    public static long insertAndReturnKey(PreparedStatement psAgentResponse, String strAutoIncreamentColumnName) throws Exception {
    	ResultSet rstPrimaryKey = null;
    	long lGeneratedKey = -1l;
    	
    	try{
    		psAgentResponse.executeUpdate();
			
			//condition to check resultset containing values or not
			rstPrimaryKey = psAgentResponse.getGeneratedKeys();
			
			while( rstPrimaryKey.next() ){
				lGeneratedKey = rstPrimaryKey.getLong(strAutoIncreamentColumnName);
			}
    	} catch(Exception e){
    		throw e;
    	} finally {
    		close(rstPrimaryKey);
    		rstPrimaryKey = null;
    	}
    	
    	return lGeneratedKey;
    }
    
    /**
     * Do bulk insert by Reader.
     * This is possible only in the Postgres.
     * This is faster than the Statement.batchInsert()
     * 
     * @param con
     * @param lUID
     * @param strMethodsCSV
     * @return
     */
	public static long doBulkInsert(Connection con, String strTableName, String strCSVText) {
		long lInserts = 0;
		CopyManager cpManager = null;
		PushbackReader reader = null;
		
		Date dateLog = LogManager.logMethodStart();
		
		try{
            cpManager = ((PGConnection) (((org.apache.commons.dbcp.DelegatingConnection) con).getInnermostDelegate()) ).getCopyAPI();
			reader = new PushbackReader( new StringReader(""), strCSVText.length() );
			reader.unread( strCSVText.toCharArray() );
			
			lInserts = cpManager.copyIn("COPY "+strTableName+" FROM STDIN (DELIMITER '|')", reader);
			
//			CopyManager copyManager = new CopyManager((BaseConnection) ((org.apache.commons.dbcp.DelegatingConnection) con) );
//			
//			FileReader fileReader = new FileReader(strCSVText);
//			
//			lInserts = cpManager.copyIn("COPY "+strTableName+" FROM STDIN WITH CSV", fileReader);
		} catch(Throwable th) {
			LogManager.errorLog(th);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LogManager.errorLog(e);
			}
		}
		
		LogManager.logMethodEnd(dateLog);
		return lInserts;
	}
	
	/**
     * Returns last inserted key 
     * 
     * @param pstmt
     * @return
     * @throws Exception
     */
    public static long returnKey(PreparedStatement pstmt) throws Exception {
    	long lGeneratedKey = -1l;
    	ResultSet rstPrimaryKey = null;
    	
    	try{
			//condition to check resultset containing values or not
			rstPrimaryKey = pstmt.getGeneratedKeys();
			while( rstPrimaryKey.next() ){
				lGeneratedKey = rstPrimaryKey.getLong(1);
			}
    	} catch(Exception e){
    		throw e;
    	} finally {
    		close(rstPrimaryKey);
    		rstPrimaryKey = null;
    	}
    	
    	return lGeneratedKey;
    }
}