package com.project.quayobasket.Fragments;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.quayobasket.Adapter.OrderHistoryAdapter;
import com.project.quayobasket.Model.OrderHistoryDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.databinding.FragmentOrderHistoryBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OrderHistoryFragment extends BottomSheetDialogFragment {

    FragmentOrderHistoryBinding binding;
    RequestQueue queue;
    SessionManager session;
    private BottomSheetBehavior mBehavior;
    ArrayList<OrderHistoryDTO> orderHistoryDTOArrayList;
    OrderHistoryAdapter adapter;
   OrderHistoryFragment orderHistoryFragment;
    JSONArray orderHistoryList;
    ArrayList<String> itemnameList;
    String itemname;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_order_history, null);
        binding = DataBindingUtil.bind(view);

        LinearLayout linearLayout = view.findViewById(R.id.root);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        params.height = getScreenHeight();
        linearLayout.setLayoutParams(params);

        bottomSheet.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) (view.getParent()));

        initialize();

        binding.backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return bottomSheet;
    }

    private void initialize() {
        queue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        itemnameList = new ArrayList<>();

        orderHistoryDTOArrayList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(getActivity(), orderHistoryDTOArrayList);
        orderHistoryFragment = this;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setAdapter(adapter);


        binding.progressbar.setVisibility(View.VISIBLE);
        loadHistory();

    }


    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onStart() {
        super.onStart();

        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void loadHistory() {

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.GET_ORDER_HISTORY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        orderHistoryDTOArrayList.clear();

                        Log.e("GET_ORDER_HISTORY", response);

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                orderHistoryList = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < orderHistoryList.length(); i++) {
                                    JSONObject orderHistoryObj = orderHistoryList.getJSONObject(i);
                                    String Order_No = orderHistoryObj.getString("Order_No");
                                    loadOrderHistoryDetails(Order_No);

                                }

                            }
                            else {
                                binding.progressbar.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.noorderTV.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        binding.progressbar.setVisibility(View.GONE);
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", session.getLoginData().get(SessionManager.KEY_ID));
                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }

    private void loadOrderHistoryDetails(String order_No) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.GET_ORDER_HISTORY_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("ORDER_HISTORY_DETAILS", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                binding.progressbar.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.VISIBLE);
                                binding.noorderTV.setVisibility(View.GONE);
                                JSONArray orderItemList = jsonObject.getJSONArray("Data");

                                for (int i = 0; i < orderItemList.length(); i++) {
                                    JSONObject orderItems = orderItemList.getJSONObject(i);
                                    String I_Name = orderItems.getString("I_Name");
                                    String Qty = orderItems.getString("Qty");

                                    itemnameList.add(Qty + " x " + I_Name + " ");

                                }
                                StringBuilder sbString = new StringBuilder();
                                for (String services : itemnameList) {
                                    sbString.append(services).append(",");
                                }
                                itemname = sbString.toString().trim();
                                if (itemname.length() > 0) {
                                    itemname = itemname.substring(0, itemname.length() - 1);
                                }


                                for (int j = 0; j < orderHistoryList.length(); j++) {
                                    JSONObject orderHistoryObj = orderHistoryList.getJSONObject(j);
                                    String Order_No = orderHistoryObj.getString("Order_No");
                                    String Srno = orderHistoryObj.getString("Srno");
                                    String Id = orderHistoryObj.getString("Id");
                                    String Order_Date = orderHistoryObj.getString("Order_Date");
                                    String Order_Aount = orderHistoryObj.getString("Order_Aount");
                                    String Total_Tax = orderHistoryObj.getString("Total_Tax");
                                    String Status = orderHistoryObj.getString("Status");
                                    Log.e("itemname", orderHistoryList.length() + "");
                                    if (order_No.equalsIgnoreCase(Order_No)) {

                                        OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
                                        orderHistoryDTO.setId(Id);
                                        orderHistoryDTO.setSrno(Srno);
                                        orderHistoryDTO.setOrder_No(Order_No);
                                        orderHistoryDTO.setOrder_Date(Order_Date);
                                        orderHistoryDTO.setOrder_Aount(Order_Aount);
                                        orderHistoryDTO.setTotal_Tax(Total_Tax);
                                        orderHistoryDTO.setStatus(Status);
                                        orderHistoryDTO.setOrderItem(itemname);

                                        orderHistoryDTOArrayList.add(orderHistoryDTO);
                                        itemnameList.clear();
                                    }


                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                binding.progressbar.setVisibility(View.GONE);
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.noorderTV.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        binding.progressbar.setVisibility(View.GONE);
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Bill_No", order_No);
                params.put("Outlet_Id", "1");

                Log.e("params", params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


}