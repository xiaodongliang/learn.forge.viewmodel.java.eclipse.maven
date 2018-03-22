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
import com.autodesk.client.api.DerivativesApi;
import com.autodesk.client.api.ObjectsApi;

import com.autodesk.client.model.*;
import io.swagger.annotations.Api;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.*;
import java.util.Arrays;
import java.util.regex.*;
import java.util.List;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;

import javax.ws.rs.core.UriBuilder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet({"/ossuploads"})
public class ossuploads  extends HttpServlet {

    public ossuploads() {
    }

    public void init() throws ServletException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private String filename(String contentTxt) throws UnsupportedEncodingException {
        Pattern pattern = Pattern.compile("filename=\"(.*)\"");
        Matcher matcher = pattern.matcher(contentTxt);
        matcher.find();
        return matcher.group(1);
    }

    private byte[] bodyContent(HttpServletRequest request) throws IOException {
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            InputStream in = request.getInputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            return out.toByteArray();
        }
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException,FileNotFoundException  {

        //from https://stackoverflow.com/questions/3831680/httpservletrequest-get-json-post-data/3831791
        try
        {

            //from https://stackoverflow.com/questions/13048939/file-upload-with-servletfileuploads-parserequest
            if (!ServletFileUpload.isMultipartContent(req)) {

                //not multiparts/formdata
                res.setStatus(500);
            }
            else
            {
                 String bucketKey = "";
                String filename="";
                String filepath = "/fileuploads";

                List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
                Iterator iter = items.iterator();

                File fileToUpload = null;

                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();

                    if (!item.isFormField()) {
                        filename = item.getName();

                        String root = getServletContext().getRealPath("/");
                        File path = new File(root + filepath);
                        if (!path.exists()) {
                            boolean status = path.mkdirs();
                        }

                        filepath = path + "/" + filename;
                        fileToUpload = new File(filepath);
                        item.write(fileToUpload);
                     }
                    else
                    {
                        if(item.getFieldName().equals("bucketKey")){
                            bucketKey = item.getString();
                        }
                     }
                }

                ObjectsApi objectsApi = new ObjectsApi();

                //this call will cause the issue:
                //com.sun.jersey.api.client.ClientHandlerException: com.sun.jersey.api.client.ClientHandlerException: A message body writer for Java type, class [B, and MIME media type, application/octet-stream, was not found
                //working on the problem 
                ApiResponse<ObjectDetails> response =
                        objectsApi.uploadObject(bucketKey, filename,
                                (int)fileToUpload.length(),
                                fileToUpload, null, null,
                                oauth.OAuthClient(null),oauth.getCredentials());


                res.setStatus(response.getStatusCode());
            }

        }
        catch(ApiException adskexp){

        }catch(FileNotFoundException fileexp){
            System.out.print("get buckets & objects exception: "+ fileexp.toString());

        }

        catch(Exception exp){
            System.out.print("get buckets & objects exception: "+ exp.toString());

        }
    }
    public void destroy() {
        super.destroy();
    }
}
