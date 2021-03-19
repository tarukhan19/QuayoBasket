package com.project.quayobasket.Payment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.Adapter.OrderSummaryAdapter;
import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.DatabaseClient;
import com.project.quayobasket.RoomPersistanceLibrary.QuayoBasketDTO;
import com.project.quayobasket.RoomPersistanceLibrary.SqliteDbMethod;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.ActivityPaymentBinding;
import com.project.quayobasket.databinding.ActivityRazorPayBinding;
import com.project.quayobasket.session.SessionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RazorPayActivity extends  Activity implements PaymentResultListener {
    private static final String TAG = RazorPayActivity.class.getSimpleName();

    private SessionManager session;
    String transaction_id, transaction_msg = "";
    TextView mTitle;
    ImageView backIV, logout;
    ActivityRazorPayBinding binding;
    private ProgressDialog progressDialog;
    double billamount;
    private RequestQueue requestQueue;
    static RazorPayActivity paymentActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_razor_pay);

        initialize();

        binding.retryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RazorPayActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (transaction_msg.equalsIgnoreCase("Transaction successful")) {
//
//                    try {
//                        deleteTask();
//                    } catch (Exception e) {
//                    }
//
//                } else {
                Intent intent = new Intent(RazorPayActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
                //             }

            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (transaction_msg.equalsIgnoreCase("Transaction successful")) {
//            try {
//                deleteTask();
//            } catch (Exception e) {
//            }
//        } else {
        Intent intent = new Intent(RazorPayActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
        //     }

    }

    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", getResources().getString(R.string.app_name));
            options.put("description", "Charges");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", "100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            submitData();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }


    private void initialize() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        backIV = toolbar.findViewById(R.id.plusimage);
        logout = toolbar.findViewById(R.id.logoutImage);
        logout.setVisibility(View.GONE);

        paymentActivity=this;

        mTitle.setText("Order Summary");
        progressDialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);

        session = new SessionManager(getApplicationContext());

        try {
            String amnt = session.getPaymentAmount().get(SessionManager.KEY_PAYMENTAMOUNT);
            billamount = Double.parseDouble(amnt); // Make use of autoboxing.  It's also easier to read.


        } catch (NumberFormatException e) {
            // p did not contain a valid double
        }
//        PaymentParams pgPaymentParams = new PaymentParams();
//        pgPaymentParams.setAPiKey(PaymentConstants.PG_API_KEY);
//        pgPaymentParams.setAmount(String.valueOf(billamount));
//        //pgPaymentParams.setAmount(String.valueOf(2));
//
//        pgPaymentParams.setEmail(session.getLoginData().get(SessionManager.KEY_EMAIL));
//        pgPaymentParams.setName(session.getLoginData().get(SessionManager.KEY_F_NAME));
//        pgPaymentParams.setPhone(session.getLoginData().get(SessionManager.KEY_MOBILENO));
//        pgPaymentParams.setOrderId(System.currentTimeMillis() + session.getOrderId().get(SessionManager.KEY_ORDERID) + session.getLoginData().get(SessionManager.KEY_ID));
//        pgPaymentParams.setCurrency(PaymentConstants.PG_CURRENCY);
//        pgPaymentParams.setDescription(PaymentConstants.PG_DESCRIPTION);
//        pgPaymentParams.setCity("Dhanbad");
//        pgPaymentParams.setState("Jharkhand");
//        pgPaymentParams.setAddressLine1(session.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_LINE1));
//        pgPaymentParams.setAddressLine2(session.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_LINE2));
//        pgPaymentParams.setZipCode(session.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_PINCODE));
//        pgPaymentParams.setCountry("91");
//        pgPaymentParams.setReturnUrl(PaymentConstants.PG_RETURN_URL);
//        pgPaymentParams.setMode(PaymentConstants.PG_MODE);
//        pgPaymentParams.setUdf1(PaymentConstants.PG_UDF1);
//        pgPaymentParams.setUdf2(PaymentConstants.PG_UDF2);
//        pgPaymentParams.setUdf3(PaymentConstants.PG_UDF3);
//        pgPaymentParams.setUdf4(PaymentConstants.PG_UDF4);
//        pgPaymentParams.setUdf5(PaymentConstants.PG_UDF5);
//        pgPaymentParams.setEnableAutoRefund("n");
//        pgPaymentParams.setOfferCode("");
//
//        Log.e("pgparams", pgPaymentParams.toString());
//        //pgPaymentParams.setSplitInfo("{\"vendors\":[{\"vendor_code\":\"24VEN985\",\"split_amount_percentage\":\"20\"}]}");
//
//        PaymentGatewayPaymentInitializer pgPaymentInitialzer = new PaymentGatewayPaymentInitializer(pgPaymentParams, this);
//        pgPaymentInitialzer.initiatePaymentProcess();

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Checkout.preload(getApplicationContext());

        startPayment();


    }

    public static RazorPayActivity getInstance()
    {
        return paymentActivity;
    }


    private void submitData() {


        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.SAVE_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("SAVE_PAYMENT", response);

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");
                            //{"Code":200,"Result":"true","Status":"Success"}
                            String Result = jsonObject.getString("Result");
// {"Code":200,"Result":"Congratulations ! Payment Successful","Status":"Success"}
                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                if (Result.equalsIgnoreCase("Congratulations ! Payment Successful")) {

                                    if (transaction_msg.equalsIgnoreCase("Transaction failed")) {
                                        binding.failureLL.setVisibility(View.VISIBLE);
                                        binding.successLL.setVisibility(View.GONE);
                                    } else {
                                        binding.failureLL.setVisibility(View.GONE);
                                        binding.successLL.setVisibility(View.VISIBLE);
                                        new SqliteDbMethod().getTasks(RazorPayActivity.this,"payment");


                                    }


                                } else {
                                    binding.failureLL.setVisibility(View.VISIBLE);
                                    binding.successLL.setVisibility(View.GONE);
                                }


                            } else {
                                binding.failureLL.setVisibility(View.VISIBLE);
                                binding.successLL.setVisibility(View.GONE);
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

                try {
                    params.put("order_id", session.getOrderId().get(SessionManager.KEY_ORDERID));
                    params.put("amount", String.valueOf(billamount));
                    params.put("currency", "INR");
                    params.put("desc_Detail", "");
                    params.put("name", "taru");
                    params.put("email", "tarukhan19");
                    params.put("phone", "9522335636");
                    params.put("address_line_1", "dwarka");
                    params.put("address_line_2", "sec 7");
                    params.put("city", "desli");
                    params.put("state", "delhi");
                    params.put("country","india");
                    params.put("zip_code", "485001");
                    params.put("udf1", "");
                    params.put("udf2", "");
                    params.put("udf3", "");
                    params.put("udf4", "");
                    params.put("udf5", "");
                    params.put("transaction_id","jvjkdhkjs");
                    params.put("payment_mode", "online");
                    params.put("payment_channel", "nvjds");
                    params.put("payment_datetime", "njnk");
                    params.put("response_code","200");
                    params.put("response_message", "success");
                    params.put("error_desc", "bhdb");
                    params.put("cardmasked", "true");
                    params.put("hash", "njcnfjiofj");

                    Log.e("params",params.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);

    }




    public void runThread(List<QuayoBasketDTO> quayoBasketDTOList)
    {
        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            if (transaction_msg.equalsIgnoreCase("Transaction successful")) {

                                setData();
                                OrderSummaryAdapter adapter = new OrderSummaryAdapter(RazorPayActivity.this, quayoBasketDTOList);
                                binding.recyclerView.setAdapter(adapter);
                            }                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }



    private void setData() {
        binding.granttotalTV.setText("Rs." + String.valueOf(billamount));
        binding.ordernoTV.setText(session.getOrderId().get(SessionManager.KEY_ORDERID));
        binding.currentDateTV.setText(currentDate());
        binding.mobilenoTV.setText(session.getLoginData().get(SessionManager.KEY_MOBILENO));
        binding.addressTV.setText(session.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_DELIVERY) );
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.scheduleLayoutAnimation();
        binding.recyclerView.setNestedScrollingEnabled(false);

        deleteTask();

    }

    private String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        return date;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }


    private void deleteTask() {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(RazorPayActivity.this).getAppDatabase()
                        .taskDao()
                        .deleteAll();
                session.setCartSize("0");
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


            }
        }
        DeleteTask dt = new DeleteTask();
        dt.execute();

    }
}
