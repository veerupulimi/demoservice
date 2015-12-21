package com.demorest.ws.rest.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.demorest.ws.rest.manager.LogManager;
import com.demorest.ws.rest.manager.ResourceManager;

@Path("/orders")
public class OrderService {

	@GET
	@Path("{userid}/orderDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrderDetails(@PathParam("userid") long id) {
		ResourceManager resourceManager = null;
		
		JSONArray jaOrderDetails = null;
		try {
			resourceManager = ResourceManager.getCollectorManager();
			
			jaOrderDetails = resourceManager.getOrderDetails(id);
			
		} catch (Exception e) {
			LogManager.errorLog(e);
		}
		return jaOrderDetails.toString();
	}
	
	@GET
	@Path("{userid}/productPurchase")
	@Produces(MediaType.APPLICATION_JSON)
	public String purchase(@PathParam("userid") long lUserId) {
		ResourceManager resourceManager = null;
		JSONObject joInvoice  = null;
		
		try {
			resourceManager = ResourceManager.getCollectorManager();
			
			joInvoice = resourceManager.productPurchase(lUserId);
			
		} catch (Exception e) {
			LogManager.errorLog(e);
		}
		return joInvoice.toString();
	}
	
//	@GET
//	@Path("{userid}/purchaseHistory")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getPurchaseHistory(@PathParam("userid") long lUserId) {
//		ResourceManager resourceManager = null;
//		JSONObject joPurchaseHistory  = null;
//		
//		try {
//			resourceManager = ResourceManager.getCollectorManager();
//			
//			joPurchaseHistory = resourceManager.getPurchaseHistory(lUserId);
//			
//		} catch (Exception e) {
//			LogManager.errorLog(e);
//		}
//		return joPurchaseHistory.toString();
//	}
	
	@POST
	@Path("/purchaseHistory")
	@Produces(MediaType.APPLICATION_JSON)	
	public String getPurchaseHistory(@HeaderParam("userid") long lUserId) {
		ResourceManager resourceManager = null;
		JSONObject joPurchaseHistory  = null;
		
		try {
			resourceManager = ResourceManager.getCollectorManager();
			
			joPurchaseHistory = resourceManager.getPurchaseHistory(lUserId);
			
		} catch (Exception e) {
			LogManager.errorLog(e);
		}
		return joPurchaseHistory.toString();
	}
	
	
	
	
	
}