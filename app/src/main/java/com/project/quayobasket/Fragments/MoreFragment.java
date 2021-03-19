package com.project.quayobasket.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.databinding.FragmentMoreBinding;
import com.project.quayobasket.databinding.ItemDataBinding;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MoreFragment extends Fragment implements View.OnClickListener {

    FragmentMoreBinding binding;
    ItemDataBinding itemDataBinding;
    String url;
    TextView dataTV;
    RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false);
        View view = binding.getRoot();
        initialize();
        return view;
    }

    private void initialize() {
        queue = Volley.newRequestQueue(getActivity());

        binding.aboutusCV.setOnClickListener(this);
        binding.privacypolicyCV.setOnClickListener(this);
        binding.tcCV.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacypolicyCV:
                openDialog("Privacy Policy");
                break;

            case R.id.tcCV:
                openDialog("Terms & Conditions");
                break;

            case R.id.aboutusCV:
                openDialog("About Us");
                break;
        }
    }

    private void openDialog(String title) {
        final Dialog dialog = new Dialog(getActivity()); // Context, this, etc.
        itemDataBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.item_data, null, false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogFragmentAnimation;
        dialog.setContentView(itemDataBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Toolbar toolbar = dialog.findViewById(R.id.toolbar);
         dataTV = dialog.findViewById(R.id.dataTV);

        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        ImageView backIV = toolbar.findViewById(R.id.plusimage);
        ImageView logout = toolbar.findViewById(R.id.logoutImage);
        logout.setVisibility(View.GONE);
        mTitle.setText(title);
        itemDataBinding.progressbar.setVisibility(View.VISIBLE);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (title.equalsIgnoreCase("Privacy Policy")) {

            url = EndPoints.LOAD_PRIVACYPOLICY;
        } else if (title.equalsIgnoreCase("Terms & Conditions")) {
            url = EndPoints.LOAD_TANDC;

        } else {

            url =EndPoints.LOAD_ABOUTUS;


        }


            loadData();

        dialog.show();


    }

    private void loadData()
    {
        itemDataBinding.dataTV.setVisibility(View.GONE);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("pppp>>>>",response);

                        itemDataBinding.progressbar.setVisibility(View.GONE);
                        itemDataBinding.dataTV.setVisibility(View.VISIBLE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject dataObj=jsonArray.getJSONObject(0);
                            String data=dataObj.getString("content");
                            dataTV.setText(data);




                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        itemDataBinding.progressbar.setVisibility(View.GONE);
                    }
                }
        ) ;
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


}