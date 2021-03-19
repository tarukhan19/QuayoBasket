package com.project.quayobasket.DialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.ConnectivityReceiver;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.FragmentLoginBinding;
import com.project.quayobasket.session.SessionManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;
import java.util.Map;


public class LoginFragment extends DialogFragment implements ConnectivityReceiver.ConnectivityReceiverListener {

    FragmentLoginBinding binding;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private SessionManager session;
    boolean isConnected;
    String emailId, password;
    SoapObject request1;
    Object resultRequestSOAP = null;
    String userid="";
    Dialog dialog;
    ImageView backIV;
    boolean isPassShow=false;
    @Override

    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dialog = super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()), R.layout.fragment_login, null, false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogFragmentAnimation;
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        initialize();
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        binding.hideshowpassLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.passwordET.getText().toString().isEmpty())
                {
                    if (!isPassShow)
                    {
                        isPassShow=true;
                        binding.hideshowpassIV.setImageDrawable(getResources().getDrawable(R.drawable.showpassword));
                        binding.passwordET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    }
                    else
                    {
                        isPassShow=false;
                        binding.hideshowpassIV.setImageDrawable(getResources().getDrawable(R.drawable.hidepassword));
                        binding.passwordET.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }
                }



            }
        });
        binding.loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyborad.hideKeyboard(getActivity());

                submit();
            }
        });

        binding.loginwithotpTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                HideKeyborad.hideKeyboard(getActivity());

                session.setLoginData("","","","");

                Intent in7 = new Intent(getActivity(), HomeActivity.class);
                in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in7.putExtra("for","login");
                getActivity().startActivity(in7);
                getActivity().overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });
        return dialog;
    }

    private void initialize() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        }
        session = new SessionManager(getActivity().getApplicationContext());
        progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());
        Toolbar toolbar = dialog.findViewById(R.id.toolbarmain);
        TextView toolbar_title = dialog.findViewById(R.id.toolbar_title);
        backIV = toolbar.findViewById(R.id.plusimage);
        ImageView logoutImage = toolbar.findViewById(R.id.logoutImage);
        toolbar_title.setText("Login");
        logoutImage.setVisibility(View.GONE);

    }

    private void submit()
    {
        emailId = binding.emailET.getText().toString();
        password = binding.passwordET.getText().toString();
        if (emailId.isEmpty()) {
            openDialog("Enter valid Email Id", "warning");
        } else if (password.isEmpty()) {
            openDialog("Enter valid Password.", "warning");
        } else {
            checkConnection();
            if (isConnected) {
             login();

            } else {
                openDialog("No Internet Connecions", "warning");
            }
        }
    }


    private void openDialog(String msg, String imagetype) {
        final Dialog dialog = new Dialog(getActivity(), R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialogbox);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

    private void login() {

            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("LOGIN_URL",response);
// {"Code":200,"Message":"Success","Data":[{"Id":"2","First_Name":"Taru  khan",
// "Email_ID":"tarukhan19@gmail.com","Mobile_No":"9522335636"}]}


                            // {"Code":500,"Message":"Sorry ! Invalid Password.","Data":[]}
                            // {"Code":400,"Message":"Sorry ! Invalid Email.","Data":[]}
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                int code=jsonObject.getInt("Code");
                                String status=jsonObject.getString("Message");
                                //{"Data":null,"Code":400,"Status":"Failed"}
                                if (code==200 && status.equalsIgnoreCase("Success") )
                                {
                                    JSONArray jsonArray=jsonObject.getJSONArray("Data");
                                    JSONObject obj=jsonArray.getJSONObject(0);
                                    String id=obj.getString("Id");
                                     loadProfile(id);
                                }
                                else if (code==400)
                                {
                                    if (status.equalsIgnoreCase("Sorry ! Invalid Email."))
                                    {
                                        dialog.dismiss();

                                    }
                                    else
                                    {
                                        openDialog(status,"warning");
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

                    params.put("Email_Id",emailId );
                    params.put("Password",password );
                    params.put("Device_Id",session.getFirebaseRegId().get(SessionManager.KEY_FIREBASE_REGID) );


                    return params;
                }
            };
            int socketTimeout = 100000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            requestQueue.add(postRequest);



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

                            JSONObject jobj=new JSONObject(response);
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

}