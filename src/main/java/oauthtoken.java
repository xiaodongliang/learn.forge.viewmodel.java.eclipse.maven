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
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet({"/oauthtoken"})
public class oauthtoken extends HttpServlet {

    public oauthtoken() {
    }

    public void init() throws ServletException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("utf8");

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject obj = new JSONObject();

        String token = "";
        try{
            token = oauth.getTokenPublic();
            obj.put("access_token", token);
            // We Need a getDate Time function here and add the exp time in Seconds so every time we have a new value. 
            obj.put("expires_in", 3500);
            out.print(obj);
        }
        catch (Exception var2) {
            System.out.print("get token exception: "+ var2.toString());
            resp.setStatus(500);
        }

    }

    public void destroy() {
        super.destroy();
    }
}
