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
import com.autodesk.client.api.ObjectsApi;
import com.autodesk.client.ApiResponse;

import com.autodesk.client.model.*;

import com.autodesk.client.api.BucketsApi;

import com.autodesk.client.model.BucketObjects;
import com.autodesk.client.model.Buckets;
import com.autodesk.client.model.BucketsItems;
import com.autodesk.client.model.ObjectDetails;
//import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import org.json.JSONException;

@WebServlet({"/oss"})
public class oss extends HttpServlet {

    public oss() {
    } 

    public void init() throws ServletException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id = req.getParameter("id");
        resp.setCharacterEncoding("utf8");
        resp.setContentType("application/json");
        try
        {
            String internalToken = oauth.getTokenInternal();


            if (id.equals("#")) {// root
                BucketsApi bucketsApi = new BucketsApi();

                //no idea how to set startAt. looks 'abc' can workaround
                ApiResponse<Buckets> buckets = bucketsApi.getBuckets("us",100,"abc",oauth.OAuthClient(null),oauth.getCredentials());

                JSONArray bucketsArray = new JSONArray();
                PrintWriter out = resp.getWriter();

                for(int i=0;i<buckets.getData().getItems().size();i++){


                    BucketsItems eachItem = buckets.getData().getItems().get(i);
                    JSONObject obj = new JSONObject();

                    obj.put("id", eachItem.getBucketKey());
                    obj.put("text", eachItem.getBucketKey());
                    obj.put("type", "bucket");
                    obj.put("children", true);

                    bucketsArray.put(obj);

                }

                out.print(bucketsArray);

            }
            else
            {

                // as we have the id (bucketKey), let's return all objects
                ObjectsApi objectsApi = new ObjectsApi();

                ApiResponse<BucketObjects> objects = objectsApi.getObjects(id,100,
                        null,null,
                        oauth.OAuthClient(null),oauth.getCredentials());


                JSONArray objectsArray = new JSONArray();
                PrintWriter out = resp.getWriter();

                for(int i=0;i<objects.getData().getItems().size();i++){


                    ObjectDetails eachItem = objects.getData().getItems().get(i);
                    String base64Urn = DatatypeConverter.printBase64Binary(eachItem.getObjectId().getBytes());


                    JSONObject obj = new JSONObject();

                    obj.put("id", base64Urn);
                    obj.put("text", eachItem.getObjectKey());
                    obj.put("type", "object");
                    obj.put("children", false);


                    objectsArray.put(obj);

                }

                out.print(objectsArray);


            }
        }catch (ApiException autodeskExp) {
            System.out.print("get buckets & objects exception: "+ autodeskExp.toString());
            resp.setStatus(500);

        }
         catch(Exception exp){
             System.out.print("get buckets & objects exception: "+ exp.toString());
             resp.setStatus(500);
         }

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            //from https://stackoverflow.com/questions/3831680/httpservletrequest-get-json-post-data/3831791

            StringBuffer jb = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = req.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) { /*report an error*/ }

            // Create a new bucket
            try
            {
                 JSONObject jsonObject = new JSONObject(jb.toString());

                String bucketKey = jsonObject.getString("bucketKey");
                String internalToken = oauth.getTokenInternal();
                BucketsApi bucketsApi = new BucketsApi();
                PostBucketsPayload postBuckets = new PostBucketsPayload();
                postBuckets.setBucketKey(bucketKey);
                postBuckets.setPolicyKey(PostBucketsPayload.PolicyKeyEnum.TRANSIENT);// expires in 24h

                ApiResponse<Bucket> newbucket = bucketsApi.createBucket(postBuckets, null,
                        oauth.OAuthClient(null),oauth.getCredentials());

                resp.setStatus(200);


            }
            catch (ApiException autodeskExp) {
                System.out.print("get buckets & objects exception: "+ autodeskExp.toString());
                resp.setStatus(500);

            }
            catch(Exception exp){
                System.out.print("get buckets & objects exception: "+ exp.toString());
                resp.setStatus(500);

            }

        } catch (JSONException e) {
            // crash and burn
            throw new IOException("Error parsing JSON request string");
        }

    }

    public void destroy() {
        super.destroy();
    }


}