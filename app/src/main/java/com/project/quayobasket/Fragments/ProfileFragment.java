package com.project.quayobasket.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.project.quayobasket.DialogFragment.AddressLHistoryFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.AppCurrentVersions;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.databinding.FragmentProfileBinding;
import com.project.quayobasket.databinding.ItemDataBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    FragmentProfileBinding binding;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private SessionManager session;
    String url, appversion;
    TextView dataTV;
    ItemDataBinding itemDataBinding;
    AppCurrentVersions appCurrentVersions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        initialize();
        binding.adresshistoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddressLHistoryFragment().show(getChildFragmentManager(), "search_dialog");

            }
        });

        binding.orderhistoryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OrderHistoryFragment().show(getChildFragmentManager(), "search_dialog");

            }
        });
        return view;
    }

    private void initialize() {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        appCurrentVersions = new AppCurrentVersions();
        progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        binding.aboutusTV.setOnClickListener(this);
        binding.privacypolicyTV.setOnClickListener(this);
        binding.tcTV.setOnClickListener(this);
        appversion = appCurrentVersions.getCurrentVersion(getActivity());
        if (session.isLoggedIn()) {
            loadProfile(session.getLoginData().get(SessionManager.KEY_ID));

        }

        binding.fnameET.setText(session.getLoginData().get(SessionManager.KEY_F_NAME));
        binding.mobilenoET.setText(session.getLoginData().get(SessionManager.KEY_MOBILENO));
        binding.emailIDET.setText(session.getLoginData().get(SessionManager.KEY_EMAIL));
        binding.versioncodeTV.setText(appversion);


    }


    private void loadProfile(String userid) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.GET_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);

                        progressDialog.dismiss();
                        try {

                            JSONObject jobj = new JSONObject(response);
                            int code = jobj.getInt("Code");
                            String status = jobj.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                JSONArray jsonArray = jobj.getJSONArray("Data");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String Guest_ID = jsonObject.getString("Id");
                                String Name = jsonObject.getString("Name");
                                String Mobile_No = jsonObject.getString("MobileNo");
                                String email_id = jsonObject.getString("Email");

                                session.setLoginData(Guest_ID, Name, email_id, Mobile_No);

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
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Cust_id", userid);
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.privacypolicyTV:
                openDialog("Privacy Policy");
                break;

            case R.id.tcTV:
                openDialog("Terms & Conditions");
                break;

            case R.id.aboutusTV:
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

            url = EndPoints.LOAD_ABOUTUS;


        }


        loadData();

        dialog.show();


    }

    private void loadData() {
        itemDataBinding.dataTV.setVisibility(View.GONE);

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("pppp>>>>", response);

                        itemDataBinding.progressbar.setVisibility(View.GONE);
                        itemDataBinding.dataTV.setVisibility(View.VISIBLE);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject dataObj = jsonArray.getJSONObject(0);
                            String data = dataObj.getString("content");
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
        );
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }
}