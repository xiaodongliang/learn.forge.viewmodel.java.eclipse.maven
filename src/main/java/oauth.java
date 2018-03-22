
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
import com.autodesk.client.auth.Credentials;
import com.autodesk.client.auth.OAuth2TwoLegged;
import java.util.ArrayList;

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

    private static String OAuthRequest(ArrayList<String> scopes, String cache) throws Exception{

        //cache has not been used...will do


        String client_id = config.credentials.client_id;
        String client_secret = config.credentials.client_secret;

        OAuth2TwoLegged forgeOAuth = OAuthClient(scopes);

        twoLeggedCredentials = forgeOAuth.authenticate();
        String token = twoLeggedCredentials.getAccessToken();

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
