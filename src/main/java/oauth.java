
/////////////////////////////////////////////////////////////////////
// Copyright (c) Autodesk, Inc. All rights reserved
// Written by Forge Partner Development
//
// Permission to use, copy, modify, and distribute this software in
// object code form for any purpose and without fee is hereby granted,
// provided that the above copyright notice appears in all copies and
// that both that copyright notice and the limited warranty and
// restricted rights notice below appear in all supporting
// documentation.
//
// AUTODESK PROVIDES THIS PROGRAM "AS IS" AND WITH ALL FAULTS.
// AUTODESK SPECIFICALLY DISCLAIMS ANY IMPLIED WARRANTY OF
// MERCHANTABILITY OR FITNESS FOR A PARTICULAR USE.  AUTODESK, INC.
// DOES NOT WARRANT THAT THE OPERATION OF THE PROGRAM WILL BE
// UNINTERRUPTED OR ERROR FREE.
/////////////////////////////////////////////////////////////////////
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;
import org.json.simple.JSONObject;

import com.autodesk.client.auth.Credentials;
import com.autodesk.client.auth.OAuth2TwoLegged;

public class oauth  { 

    private static Credentials twoLeggedCredentials = null;

    public static String getTokenPublic() throws Exception{

        return OAuthRequest(config.scopePublic, "public");

    }

    public  static String getTokenInternal() throws Exception{

        return OAuthRequest(config.scopeInternal, "internal");

    }

    public  static Credentials getCredentials() throws Exception{

        return twoLeggedCredentials;
    }

    //token cache map
    private static Map<String,JSONObject> _cached = new HashMap<String,JSONObject>();
    private static String OAuthRequest(ArrayList<String> scopes, String cache) throws Exception{

        
    	if(_cached.containsKey(cache)) {
            //check if the token expires or not
    		JSONObject cacheJsonObj =(JSONObject) _cached.get(cache);
    		Instant instant = Instant.now();
    		Long currentTime = instant.getMillis(); 
    		Long expire_at = (Long) cacheJsonObj.get("expire_at");
    		if(expire_at >currentTime)
                //use current token
    			return  String.valueOf(cacheJsonObj.get("access_token")); 
    	}
    	
        //get new token
        String client_id = config.credentials.client_id;
        String client_secret = config.credentials.client_secret;

        OAuth2TwoLegged forgeOAuth = OAuthClient(scopes);

        twoLeggedCredentials = forgeOAuth.authenticate();
        String token = twoLeggedCredentials.getAccessToken();
        long expire_at = twoLeggedCredentials.getExpiresAt();
        
        //store the token to cache
        JSONObject obj = new JSONObject(); 
        obj.put("access_token", token);
    	Instant instant = Instant.now();
    	Long currentTime = instant.getMillis(); 
        obj.put("expire_at", currentTime + expire_at); 
        _cached.put(cache, obj); 
        
         
        return  token;
    }

    public static OAuth2TwoLegged OAuthClient(ArrayList<String> scopes) throws Exception{

        String client_id = config.credentials.client_id;
        String client_secret = config.credentials.client_secret;
        if (scopes == null)
            scopes = config.scopeInternal;

        return new OAuth2TwoLegged(client_id, client_secret, scopes,Boolean.valueOf(true));

    }
}
