package com.demorest.ws.rest.service;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.json.JSONObject;
import com.demorest.ws.rest.manager.LogManager;
import com.demorest.ws.rest.manager.ResourceManager;

@Path("/user")
public class UserService {

	@GET
	@Path("{encodedUserPassword}/userAuthentication")
	@Produces(MediaType.APPLICATION_JSON)
	public String userAuthentication(@PathParam("encodedUserPassword") String encodedUserPassword) {
		
		ResourceManager resourceManager = null;
		JSONObject joCredentials  = null;
		
		try {
			resourceManager = ResourceManager.getCollectorManager();
			joCredentials = resourceManager.userAuthentication(encodedUserPassword);			
		} catch (Exception e) {
			LogManager.errorLog(e);			
		}
		return joCredentials.toString();
		
	}
}