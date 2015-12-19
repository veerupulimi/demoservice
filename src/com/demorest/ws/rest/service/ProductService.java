package com.demorest.ws.rest.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.appedo.manager.LogManager;
import com.demorest.ws.rest.manager.ResourceManager;

@Path("/Products")
public class ProductService {
	
	
	@GET
	@Path("{userid}/{productid}/{quantity}/addtoCart")
	@Produces(MediaType.APPLICATION_JSON)
	public String addToCart(@PathParam("userid") long lUserId, @PathParam("productid") long lproductid,@PathParam("quantity") int nQuantity) {
		ResourceManager resourceManager = null;
		JSONObject joRtn = null;
		try {
			resourceManager = ResourceManager.getCollectorManager();			
			joRtn = resourceManager.addProductToCart(lUserId, lproductid, nQuantity);		
			
		} catch (Exception e) {
			LogManager.errorLog(e);
		}
		return joRtn.toString();
	}
	
	
	
	@GET
	@Path("/getproducts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getProducts() {
		ResourceManager resourceManager = null;
		JSONObject joProducts  = null;
		JSONArray jaProducts = null;
		try {
			resourceManager = ResourceManager.getCollectorManager();
			
			jaProducts = resourceManager.getProducts();
			
			joProducts = new JSONObject();
			
			joProducts.put("productlist", jaProducts);
			
		} catch (Exception e) {
			LogManager.errorLog(e);
		}
		return joProducts.toString();
	}


	

}