package com.spring.wiremock.util;

import java.util.Arrays;
import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class CommonUtil {
	private CommonUtil(){
	}
	public static String urlBuilder(String baseUrl,String endpoint){
		StringBuilder urlBuilder = new StringBuilder()
				.append(baseUrl)
				.append(endpoint);
		return urlBuilder.toString();
	}
	
	public static HttpHeaders createCommonHttpHeader(){
		HttpHeaders header = new HttpHeaders();
		header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return header;
	}
	
	public static HttpHeaders createAccessTokenHeader(String encodedAuthorization){
		HttpHeaders header = new HttpHeaders();
		header.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		StringBuilder token = new StringBuilder();
			token.append("Basic ");
			token.append(encodedAuthorization);
		header.set("Authorization", token.toString());
		return header;
	}
	
	public static String createEncodedAuthorizationCode(String userName, String password){
		StringBuilder authorizationCode = new StringBuilder();
			authorizationCode.append(userName);
			authorizationCode.append(":");
			authorizationCode.append(password);
		return Base64.getEncoder().encodeToString(authorizationCode.toString().getBytes());				
	}
	
	public static HttpHeaders createMultiHttpHeader(){
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		return header;
	}
	
	public static HttpHeaders createJsonHttpHeader(){
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		return header;
	}
}