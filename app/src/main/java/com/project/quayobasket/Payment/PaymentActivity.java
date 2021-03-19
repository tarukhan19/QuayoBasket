package com.project.quayobasket.Payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.Adapter.OrderSummaryAdapter;
import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.DatabaseClient;
import com.project.quayobasket.RoomPersistanceLibrary.QuayoBasketDTO;
import com.project.quayobasket.RoomPersistanceLibrary.SqliteDbMethod;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.ActivityPaymentBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    // ProgressBar pb;


    private SessionManager session;
    String transaction_id, transaction_msg = "";
    TextView mTitle;
    ImageView backIV, logout;
    ActivityPaymentBinding binding;
    private ProgressDialog progressDialog;
    double billamount;
    JSONObject responsejsonobject;
    private RequestQueue requestQueue;
    static PaymentActivity paymentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment);

        initialize();

        binding.retryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
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
                Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
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
        Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_left_in,
                R.anim.trans_left_out);
        //     }

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

        submitData();

    }

    public static PaymentActivity getInstance()
    {
        return paymentActivity;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PGConstants.REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK) {
//                try {
//                    String paymentResponse = data.getStringExtra(PGConstants.PAYMENT_RESPONSE);
//                    Log.e("Transaction !", paymentResponse);
//
//                    if (paymentResponse.equals("null")) {
//                        progressDialog.dismiss();
//                        binding.successLL.setVisibility(View.GONE);
//                        binding.failureLL.setVisibility(View.VISIBLE);
//
//                    } else {
//                        responsejsonobject = new JSONObject(paymentResponse);
//                        transaction_id = responsejsonobject.getString("transaction_id");
//                        transaction_msg = responsejsonobject.getString("response_message");
//                        if (transaction_msg.equalsIgnoreCase("Transaction successful")) {
//                            progressDialog.setMessage("Loading...");
//                            progressDialog.setCancelable(false);
//                            progressDialog.show();
//
//                            submitData();
//
//                        } else if (transaction_msg.equalsIgnoreCase("Transaction cancelled")) {
//                            progressDialog.dismiss();
//                            binding.successLL.setVisibility(View.GONE);
//                            binding.failureLL.setVisibility(View.VISIBLE);
//                        } else {
//                            progressDialog.dismiss();
//                            submitData();
//                        }
//
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                //Write your code if there's no result
//                Log.e("RESULT_CANCELED !", "RESULT_CANCELED");
//
//                progressDialog.dismiss();
//                binding.successLL.setVisibility(View.GONE);
//                binding.failureLL.setVisibility(View.VISIBLE);
//
//            } else {
//                Log.e("RESULT !", "RESULT_other");
//
//                progressDialog.dismiss();
//                binding.successLL.setVisibility(View.GONE);
//                binding.failureLL.setVisibility(View.VISIBLE);
//            }
//
//        }
//    }


    private void deleteTask() {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(PaymentActivity.this).getAppDatabase()
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
                                        new SqliteDbMethod().getTasks(PaymentActivity.this,"payment");


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
                    params.put("name", responsejsonobject.getString("name"));
                    params.put("email", responsejsonobject.getString("email"));
                    params.put("phone", responsejsonobject.getString("phone"));
                    params.put("address_line_1", responsejsonobject.getString("address_line_1"));
                    params.put("address_line_2", responsejsonobject.getString("address_line_2"));
                    params.put("city", responsejsonobject.getString("city"));
                    params.put("state", responsejsonobject.getString("state"));
                    params.put("country", responsejsonobject.getString("country"));
                    params.put("zip_code", responsejsonobject.getString("zip_code"));
                    params.put("udf1", responsejsonobject.getString("udf1"));
                    params.put("udf2", responsejsonobject.getString("udf2"));
                    params.put("udf3", responsejsonobject.getString("udf3"));
                    params.put("udf4", responsejsonobject.getString("udf4"));
                    params.put("udf5", responsejsonobject.getString("udf5"));
                    params.put("transaction_id", responsejsonobject.getString("transaction_id"));
                    params.put("payment_mode", responsejsonobject.getString("payment_mode"));
                    params.put("payment_channel", responsejsonobject.getString("payment_channel"));
                    params.put("payment_datetime", responsejsonobject.getString("payment_datetime"));
                    params.put("response_code", responsejsonobject.getString("response_code"));
                    params.put("response_message", responsejsonobject.getString("response_message"));
                    params.put("error_desc", responsejsonobject.getString("error_desc"));
                    params.put("cardmasked", responsejsonobject.getString("cardmasked"));
                    params.put("hash", responsejsonobject.getString("hash"));

                    Log.e("params",params.toString());

                } catch (JSONException e) {
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
                                OrderSummaryAdapter adapter = new OrderSummaryAdapter(PaymentActivity.this, quayoBasketDTOList);
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

}