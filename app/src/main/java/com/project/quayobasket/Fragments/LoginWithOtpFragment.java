package com.project.quayobasket.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.Activity.RegistrationActivity;
import com.project.quayobasket.DialogFragment.LoginFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.ConnectivityReceiver;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.FragmentLoginWithOtpBinding;
import com.project.quayobasket.session.SessionManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginWithOtpFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    FragmentLoginWithOtpBinding binding;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private SessionManager session;
    boolean isConnected;

    String mobileno,  otp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_with_otp, container, false);
        View view = binding.getRoot();
        initialize();



        binding.resendotpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyborad.hideKeyboard(getActivity());

                binding.otpET.setVisibility(View.GONE);
                binding.loginBTN.setText("Send OTP for sign in/signup");
                binding.mobilenoET.setEnabled(true);
                binding.otpET.setText("");
            }
        });

        binding.loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyborad.hideKeyboard(getActivity());

             //   Toast.makeText(getActivity(), binding.loginBTN.getText().toString(), Toast.LENGTH_SHORT).show();
                if (binding.loginBTN.getText().toString().equalsIgnoreCase("Send OTP for sign in/signup")) {
                    submit();
                } else {
                    submitdata();
                }

            }
        });

        binding.loginwithemailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyborad.hideKeyboard(getActivity());

                binding.resendotpBTN.setVisibility(View.GONE);
                binding.otpET.setVisibility(View.GONE);
                binding.loginBTN.setText("Send OTP for sign in/signup");
                binding.mobilenoET.setEnabled(true);
                binding.otpET.setText("");
                binding.mobilenoET.setText("");


                LoginFragment dialogFragment = new LoginFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
                ft.addToBackStack(null);
                dialogFragment.show(ft, "dialog");
            }
        });
        return  view;
    }


    private void submitdata() {
        mobileno = binding.mobilenoET.getText().toString();
        otp = binding.otpET.getText().toString();

        if (mobileno.isEmpty()) {
            openDialog("Enter valid Mobile No", "warning");
        } else if (otp.isEmpty()) {
            openDialog("Enter valid OTP", "warning");

        } else {
            checkConnection();
            if (isConnected) {
                try {
                    verifyOtp();
                }
                catch (Exception e)
                {
                    Log.e("exception",e.getLocalizedMessage());
                }
            } else {
                openDialog("No Internet Connecions", "warning");
            }
        }
    }

    private void submit()
    {
        mobileno = binding.mobilenoET.getText().toString();
        if (mobileno.isEmpty()) {
            openDialog("Enter valid Mobile No", "warning");
        } else {
            checkConnection();
            if (isConnected) {
                try {
                  sendOtp();
                }
                catch (Exception e)
                {

                    Log.e("exception",e.getLocalizedMessage());

                }
            } else {
                openDialog("No Internet Connecions", "warning");
            }
        }
    }


    private void initialize()
    {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        }
        session = new SessionManager(getActivity().getApplicationContext());
        progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
    }

    private void checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
    }


    @Override
    public void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        this.isConnected = isConnected;
    }
    private void loadProfile(String userid)
    {
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.GET_USER_PROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);

                        progressDialog.dismiss();



                        try {
                            JSONObject jobj= null;
                            jobj = new JSONObject(response);
                            int code=jobj.getInt("Code");
                            String status=jobj.getString("Status");

                            if (code==200 && status.equalsIgnoreCase("Success") )
                            {
                                JSONArray jsonArray=jobj.getJSONArray("Data");
                                JSONObject jsonObject=jsonArray.getJSONObject(0);
                                String Guest_ID=jsonObject.getString("Id");
                                String Name=jsonObject.getString("Name");
                                String Mobile_No=jsonObject.getString("MobileNo");
                                String email_id=jsonObject.getString("Email");

                                session.setLoginData(Guest_ID,Name,email_id,Mobile_No);
                                session.setLogin();
                                try {

                                    progressDialog.dismiss();
                                    Intent intent=new Intent(getActivity(), HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);


                                } catch (Exception e) {
                                }
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
                Log.e("Cust_id",userid);
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }



    private void sendOtp() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.SEND_OTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("SEND_OTP",response);

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            int code=jsonObject.getInt("Code");
                            String status=jsonObject.getString("Status");

                            if (code==200 && status.equalsIgnoreCase("Success") )
                            {


                                    binding.otpET.setVisibility(View.VISIBLE);
                                    binding.loginBTN.setText("Verify");
                                    binding.resendotpBTN.setVisibility(View.VISIBLE);
                                    binding.mobilenoET.setEnabled(false);


                            }
                            else
                            {
                                openDialog(response,"failure");

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

                params.put("Mobile_No",mobileno );
                params.put("Device_Id",session.getFirebaseRegId().get(SessionManager.KEY_FIREBASE_REGID) );

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);



    }

    private void openDialog(String msg, String imagetype)
    {
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialogbox);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        TextView descriptionTV = dialog.findViewById(R.id.descriptionTV);
        TextView titleTV = dialog.findViewById(R.id.titleTV);
        ImageView imageView = dialog.findViewById(R.id.imageView);
        Button okBtn = dialog.findViewById(R.id.okBtn);

        descriptionTV.setText(msg);
        if (imagetype.equalsIgnoreCase("warning")) {
            //imageView.setImageResource(R.drawable.warning);
            titleTV.setText("Warning!");
        } else if (imagetype.equalsIgnoreCase("success")) {
            // imageView.setImageResource(R.drawable.success);
            titleTV.setText("Success!");
        } else if (imagetype.equalsIgnoreCase("failure")) {
            // imageView.setImageResource(R.drawable.sorry);
            titleTV.setText("Failure!");
        }
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagetype.equalsIgnoreCase("warning") || imagetype.equalsIgnoreCase("failure")) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Intent in7 = new Intent(getActivity(), HomeActivity.class);
                    in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in7);
                    getActivity().overridePendingTransition(R.anim.trans_left_in,
                            R.anim.trans_left_out);
                }


            }
        });
    }


    private void verifyOtp()
    {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.VERIFY_OTP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("VERIFY_OTP",response);

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            int code=jsonObject.getInt("Code");
                            String status=jsonObject.getString("Status");
//{"Code":200,"Status":"Success","Message":"2"}
                            //OTP Is Incorrect
                            //{"Code":400,"Status":"Failed","Message":"Sorry ! Invalid OTP !","Customer_Code":null}
                            // {"Code":200,"Status":"Success","Message":"Sorry ! User Not Registered.","Customer_Code":null}
                            //{"Code":200,"Status":"Success","Message":"Congratulations ! OTP Verified Successfully.","Customer_Code":"2"}
                            if (code==200 && status.equalsIgnoreCase("Success") )
                            {
                                String Message=jsonObject.getString("Message");

                                    if (Message.equalsIgnoreCase("Sorry ! User Not Registered."))
                                    {

                                        binding.resendotpBTN.setVisibility(View.GONE);
                                        binding.otpET.setVisibility(View.GONE);
                                        binding.loginBTN.setText("Send OTP for sign in/signup");
                                        binding.mobilenoET.setEnabled(true);
                                        binding.otpET.setText("");
                                        binding.mobilenoET.setText("");
                                        session.setLoginData("","","",mobileno);

                                        Intent in7 = new Intent(getActivity(), RegistrationActivity.class);
                                        in7.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(in7);
                                        getActivity().overridePendingTransition(R.anim.trans_left_in,
                                                R.anim.trans_left_out);


                                    }
                                    else if (Message.equalsIgnoreCase("Congratulations ! OTP Verified Successfully."))
                                    {
                                        String custcode=jsonObject.getString("Customer_Code");
                                        loadProfile(custcode);
                                    }
                                    else
                                    {

                                        openDialog(Message,"warning");

                                    }


                            }
                            else
                            {
                                String Message=jsonObject.getString("Message");
                                openDialog(Message,"warning");
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

                params.put("Mobile_No",mobileno );
                params.put("OTP_No",otp );

                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }

}