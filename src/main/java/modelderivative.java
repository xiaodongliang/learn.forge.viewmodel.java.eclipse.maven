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

import com.autodesk.client.ApiException;
import com.autodesk.client.ApiResponse;
//import com.autodesk.client.api.BucketsApi;
import com.autodesk.client.model.*;

import com.autodesk.client.api.DerivativesApi;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
//import java.io.PrintWriter;
import java.util.Arrays;

@WebServlet({"/modelderivative"})
public class modelderivative  extends HttpServlet {

    public modelderivative() {
    }

    public void init() throws ServletException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
 
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        //from https://stackoverflow.com/questions/3831680/httpservletrequest-get-json-post-data/3831791

        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        try
        {
            JSONObject jsonObject = new JSONObject(jb.toString());

            String objectName = jsonObject.getString("objectName");
            String internalToken = oauth.getTokenInternal();
            DerivativesApi derivativesApi = new DerivativesApi();

            JobPayload job = new JobPayload();

            JobPayloadInput input = new JobPayloadInput();
            input.setUrn(new String(objectName));
            JobPayloadOutput output = new JobPayloadOutput();
            JobPayloadItem formats = new JobPayloadItem();
            formats.setType(JobPayloadItem.TypeEnum.SVF);
            formats.setViews(Arrays.asList(JobPayloadItem.ViewsEnum._3D));
            output.setFormats(Arrays.asList(formats));

            job.setInput(input);
            job.setOutput(output);

            ApiResponse<Job> response = derivativesApi.translate(job,true,oauth.OAuthClient(null),oauth.getCredentials());

            res.setStatus(response.getStatusCode());

        }
        catch (ApiException autodeskExp) {
            System.out.print("get buckets & objects exception: "+ autodeskExp.toString());
            res.setStatus(500);

        }
        catch(Exception exp){
            System.out.print("get buckets & objects exception: "+ exp.toString());
            res.setStatus(500);
        }


    }
    public void destroy() {
        super.destroy();
    }
}
