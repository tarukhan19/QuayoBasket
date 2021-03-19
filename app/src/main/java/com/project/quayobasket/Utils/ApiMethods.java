package com.project.quayobasket.Utils;

import android.app.ProgressDialog;
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
import com.project.quayobasket.DialogFragment.AddressLHistoryFragment;
import com.project.quayobasket.Fragments.HomeFragment;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.session.SessionManager;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiMethods {
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    MapMethods mapMethods;
    public void loadAddress(Context mctx,String from)
    {
        progressDialog = new ProgressDialog(mctx);
        queue = Volley.newRequestQueue(mctx);
        session = new SessionManager(mctx.getApplicationContext());

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.GET_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("GET_ADDRESSresponse", response);


                        try {
                            if (from.equalsIgnoreCase("addresshistory"))
                            {
                                AddressLHistoryFragment.getInstance().runThread(response);

                            }
                            else if (from.equalsIgnoreCase("home"))
                            {

                                JSONObject jsonObject = new JSONObject(response);
                                int code = jsonObject.getInt("Code");
                                String status = jsonObject.getString("Status");

                                if (code == 200 && status.equalsIgnoreCase("Success"))
                                {
                                    JSONArray jsonArray=jsonObject.getJSONArray("Data");

                                    if (jsonArray.length()==0)
                                    {
                                        mapMethods=new MapMethods(mctx,"apimethod");
                                    }
                                    else
                                    {
                                        String address=jsonArray.getJSONObject(0).getString("Add_type")+":  "+jsonArray.getJSONObject(0).getString("flat_street")+
                                                ", "+jsonArray.getJSONObject(0).getString("Address1")+", "+jsonArray.getJSONObject(0).getString("address2")+
                                                ", "+jsonArray.getJSONObject(0).getString("landmark")+", "+jsonArray.getJSONObject(0).getString("PinNo");
                                        session.setDeliveryAddress(jsonArray.getJSONObject(0).getString("AddrId"),address,jsonArray.getJSONObject(0).getString("Address1")
                                                ,jsonArray.getJSONObject(0).getString("address2"),jsonArray.getJSONObject(0).getString("PinNo"));

                                        HomeFragment.getInstance().runThread();
                                        OrderFragment.getInstance().runThread("setorderaddress");
                                    }

                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Guest_Id", session.getLoginData().get(SessionManager.KEY_ID));
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

}
