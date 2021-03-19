package com.project.quayobasket.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.ConnectivityReceiver;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.ActivityRegistrationBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    ActivityRegistrationBinding binding;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private SessionManager session;
    boolean isConnected;
    String emailId, password, fname, lname, mobileno, confirmpassword,response;
    SoapObject  request1;

    Object resultRequestSOAP = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registration);
        initialize();

        binding.loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             submit();

            }
        });
    }

    private void initialize() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setBackgroundDrawableResource(R.drawable.bg);

        session = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);

        if (session.getLoginData().get(SessionManager.KEY_MOBILENO).isEmpty())
        {
            binding.mobilenoET.setEnabled(true);

        }
       else
        {
            binding.mobilenoET.setText(session.getLoginData().get(SessionManager.KEY_MOBILENO));
            binding.mobilenoET.setEnabled(false);
        }
    }

    private void submit() {
        fname = binding.fnameET.getText().toString();
        lname = binding.lnameET.getText().toString();
        emailId = binding.emailIDET.getText().toString();
        mobileno = binding.mobilenoET.getText().toString();
        password = binding.passwordET.getText().toString();
        confirmpassword = binding.confpasswordET.getText().toString();
        HideKeyborad.hideKeyboard(this);


        if (fname.isEmpty())
        {
            openDialog("Enter valid First Name", "warning");
        } else if (lname.isEmpty()) {
            openDialog("Enter valid Last Name.", "warning");
        } else if (emailId.isEmpty()) {
            openDialog("Enter valid Email ID.", "warning");
        } else if (mobileno.isEmpty()) {
            openDialog("Enter valid Mobile No.", "warning");
        } else if (password.isEmpty()) {
            openDialog("Enter valid Password.", "warning");
        } else if (confirmpassword.isEmpty()) {
            openDialog("Please confirm your password.", "warning");
        } else if (!confirmpassword.equals(password)) {
            openDialog("Your password doesn't match.", "warning");
        } else {
            checkConnection();
            if (isConnected) {
                registration();
            } else {
                openDialog("No Internet Connecions", "warning");
            }
        }
    }


    private void openDialog(String msg, String imagetype) {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
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
                HideKeyborad.hideKeyboard(RegistrationActivity.this);

                if (imagetype.equalsIgnoreCase("warning") || imagetype.equalsIgnoreCase("failure")) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Intent in7 = new Intent(RegistrationActivity.this, HomeActivity.class);
                    in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(in7);
                    overridePendingTransition(R.anim.trans_left_in,
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

//    private void registration() {
//
//
//        try {
//             request1 = new SoapObject(Constant.NAMESPACE, Constant.REGISTRATION_METHOD);
//
//
//            request1.addProperty("First_Name", fname);
//            request1.addProperty("Last_Name",lname );
//            request1.addProperty("Email", emailId);
//            request1.addProperty("Mobile_No",mobileno );
//            request1.addProperty("Password",password );
//            request1.addProperty("DeviceId", session.getFirebaseRegId().get(SessionManager.KEY_FIREBASE_REGID));
//
//
//
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//                    SoapEnvelope.VER11);
//
//            envelope.dotNet = true;
//            envelope.setOutputSoapObject(request1);
//
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(Constant.REGISTRATION_URL);
//            androidHttpTransport.debug = true;
//            androidHttpTransport.call(Constant.SOAP_ACTION_REGISTRATION, envelope);
//
//
//            String requestDumpString = androidHttpTransport.requestDump;
//            Log.e("requestDump : " , requestDumpString);
//
//            resultRequestSOAP = envelope.getResponse(); // Output received
//            response = resultRequestSOAP.toString(); // Result string
//            Log.e("resultRequestSOAP : " , response);
//
//
//
//            //Static_Veriable.Customer_Id = Integer.parseInt(ss);
//
//
//        } catch (Exception ex) {
//
//          //  Log.e("EXCEPTION: " , ex.getMessage());
//            //   RaygunClient.send(ex);
//        }
//
//    }

//
//
//    class SaveTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog.setMessage("Loading...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            registration();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//
//            if (response.equalsIgnoreCase("User Allready Exits."))
//            {
//               openDialog(response,"warning");
//            }
//            else
//            {
//                loadProfile(response);
//            }
//
//
//        }
//    }


     private void registration()
    {
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.REGISTRATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("REGISTRATION_URL",response);
//{"Code":200,"Status":"Success","Message":"Congratulations ! Registration Successful.","Id":"32"}
                        //  {"Code":400,"Status":"Failed","Message":"Sorry ! Email Already Exist","Id":null}
                        progressDialog.dismiss();
                        try {
                        JSONObject jsonObject=new JSONObject(response);
                            int code=jsonObject.getInt("Code");
                            String status=jsonObject.getString("Status");

                            if (code==200 && status.equalsIgnoreCase("Success") )
                            {
                                String message=jsonObject.getString("Message");
                                if (message.equalsIgnoreCase("Congratulations ! Registration Successful."))
                                {
                                    String id=jsonObject.getString("Id");
                                    loadProfile(id);
                                }
                                else
                                {
                                    openDialog(message,"warning");
                                }

                            }
                            else
                            {
                                String message=jsonObject.getString("Message");
                                openDialog(message,"warning");

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
                params.put("First_Name",fname );
                params.put("Last_Name",lname );
                params.put("email",emailId );
                params.put("Mobile_No",mobileno );
                params.put("Password",password );
                params.put("DeviceId",session.getFirebaseRegId().get(SessionManager.KEY_FIREBASE_REGID) );

                return params;
            }
        };
        int socketTimeout = 30000;
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
                        Log.e("GET_USER_PROFILE",response);

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
                                    Intent intent=new Intent(RegistrationActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in,
                                            R.anim.trans_left_out);
                                    finish();

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
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }


}