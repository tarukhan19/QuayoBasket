package com.project.quayobasket.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DistanceCalcMethods {
Context context;
SessionManager sessionManager;
RequestQueue queue;
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist=dist/ 0.62137;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    public void getDistance(double lat1, double lng1, double lat, double lng, Context context)
    {
        this.context=context;

        queue = Volley.newRequestQueue(context);
        sessionManager = new SessionManager(context.getApplicationContext());

        String uri = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="+lat+","+lng+"&destinations=" + lat1+","+lng1 + "&mode=DRIVING&key=AIzaSyAdoq9IbV9WMAc_wAyEpNZslrPUmJ_RCLg";

        Log.e("uri", uri);

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);


                        try
                        {
                            //Log.e("response", response);
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            JSONArray rowArray=jsonObject.getJSONArray("rows");
                            JSONObject rowJsonObj=rowArray.getJSONObject(0);
                            JSONArray elementsArray=rowJsonObj.getJSONArray("elements");
                            JSONObject elementJsonObj=elementsArray.getJSONObject(0);
                            JSONObject distanceJsonObj=elementJsonObj.getJSONObject("distance");
                            double value=distanceJsonObj.getDouble("value");
                            value=value*0.001;
                            Log.e("value",value+"");
                            sessionManager.setLatLngDistance(String.valueOf(value));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }
}
