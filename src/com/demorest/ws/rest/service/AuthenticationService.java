package com.demorest.ws.rest.service;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import com.demorest.ws.rest.manager.LogManager;

public class AuthenticationService {
	
	/***
	 * To authenticate 
	 * @param authInput
	 * @return
	 */
	public boolean authenticate(String authInput) {
		boolean authenticationStatus = false;
		
		try {
			
			if (null == authInput)
				return false;
			// header value format will be "Demo encodedstring" for Basic
			// authentication. Example "Demo YWRtaW46YWRtaW4="
			final String encodedInput = authInput.replaceFirst("Demo"
					+ " ", "");
			String decodedOutput = null;
			try {
				byte[] decodedBytes = DatatypeConverter.parseBase64Binary(encodedInput);
				decodedOutput = new String(decodedBytes, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			// I have fixed the decodedOutput as $$testencypt#$
			
			authenticationStatus = "$$testencypt#$".equals(decodedOutput);
					
		}catch (Exception e) {
			LogManager.errorLog(e);
		}
		
		return authenticationStatus;
		
	}
}
