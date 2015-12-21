package com.demorest.ws.rest.manager;

import java.sql.Connection;
import java.util.StringTokenizer;

import javax.xml.bind.DatatypeConverter;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.demorest.ws.rest.manager.LogManager;
import com.demorest.ws.rest.connect.DataBaseManager;
import com.demorest.ws.rest.dbi.CartDBI;


/**
 * Singleton class. So no objects can be created.
 * 
 * @author Veeru
 *
 */
public class ResourceManager{
	
	// Singleton object, used globally with static getCollectorManager().
	private static ResourceManager collectorManager = new ResourceManager();
	private Connection con = null;
	
	/**
	 * Avoid multiple object creation, by Singleton
	 */
	private ResourceManager() {
		
		try{
			//DataBaseManager.doConnectionSetupIfRequired("C:/Appedo/resource/appedo_config.properties");
			DataBaseManager.doConnectionSetupIfRequired("/mnt/demorest/resource/appedo_config.properties");
			con = DataBaseManager.giveConnection();	
			
		} catch(Exception ex) {
			LogManager.errorLog(ex);
		}
	}
	
	/**
	 * Access the only[singleton] CollectorManager object.
	 * 
	 * @return CollectorManager
	 */
	public static ResourceManager getCollectorManager(){
		return collectorManager;
	}
	
	/**
	 * To authenticate login user
	 * @param encodedUserPassword
	 * @return
	 */
	public JSONObject userAuthentication(String encodedUserPassword) {
		CartDBI cartDBI  = null;
		String usernameAndPassword = null;
		JSONObject joCredentials = null;
		try{
			cartDBI  = new CartDBI();
			byte[] decodedBytes = DatatypeConverter.parseBase64Binary(encodedUserPassword);
			usernameAndPassword = new String(decodedBytes, "UTF-8");
			
			final StringTokenizer tokenizer = new StringTokenizer(
					usernameAndPassword, ":");
			final String username = tokenizer.nextToken();
			final String password = tokenizer.nextToken();
			
			joCredentials  = cartDBI.userAuthentication(username, password, con);
			
		} catch(Exception ex) {
			LogManager.errorLog(ex);
		}
		return joCredentials;
		
	}
	/**
	 * To get the details about orders
	 * @param lUserId
	 * @return
	 */
	public JSONArray getOrderDetails(long lUserId) {
		JSONArray jaProducts = null;
		CartDBI cartDBI  = null;
		try{
			cartDBI  = new CartDBI();
			jaProducts  = cartDBI.getOrderDetails(lUserId, con);
			
		} catch(Exception ex) {
			LogManager.errorLog(ex);
		}
		return jaProducts;
	}
	/**
	 * To add products to cart
	 * @param lUserId
	 * @param lProductId
	 * @param nQuantity
	 * @return
	 * @throws Exception
	 */
	
	public JSONObject addProductToCart(long lUserId, long lProductId, int nQuantity) throws Exception {
		CartDBI cartDBI = null;
		JSONObject joRtn = null;
		try{
			cartDBI  = new CartDBI();
			joRtn = cartDBI.addProductToCart(con, lUserId, lProductId, nQuantity);
			
		} catch(Exception ex) {
			LogManager.errorLog(ex);
		}
		return joRtn;
	}
	
	/**
	 * To get the history of purchase orders
	 * @param lUserId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getPurchaseHistory(long lUserId) throws Exception {
		CartDBI cartDBI = null;
		JSONObject joPurchaseHistory = null;
		try{
			cartDBI  = new CartDBI();
			joPurchaseHistory = cartDBI.getPurchaseHistory(lUserId, con);
			
		} catch(Exception ex) {
			LogManager.errorLog(ex);
		}
		return joPurchaseHistory;
	}
	/**
	 * To purchase the orders
	 * @param lUserId
	 * @return
	 * @throws Exception
	 */
	public JSONObject productPurchase(long lUserId) throws Exception {
		CartDBI cartDBI = null;
		JSONObject joInvoice = null;
		try{
			cartDBI  = new CartDBI();
			joInvoice = cartDBI.productPurchase(con, lUserId);
			
		} catch(Exception ex) {
			LogManager.errorLog(ex);
		}
		return joInvoice;
	}
	@Override
	/**
	 * Before destroying clear the objects. To prevent from MemoryLeak.
	 */
	protected void finalize() throws Throwable {
		
		super.finalize();
	}
	
}
