package com.project.quayobasket.DialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

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
import com.project.quayobasket.Fragments.HomeFragment;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.ApiMethods;
import com.project.quayobasket.Utils.ConnectivityReceiver;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SubmitAddressFragment extends BottomSheetDialogFragment implements
        ConnectivityReceiver.ConnectivityReceiverListener {
    //Dialog dialog;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    boolean isConnected;
    String adresslinee1 = "", addressline2 = "", flatno = "", landmark = "", pincode = "", addresstype = "home", location = "", lat = "", lng = "";

    ImageView backIV;
    private BottomSheetBehavior mBehavior;
    RelativeLayout relativelayout;
    TextView currentlocationTV;
    EditText floorET, addressline1ET, addressline2ET, landmarkET, pincodeET;
    RadioGroup radiogrp;
    RadioButton homeRB, officeRB, otherRB;
    Button submitBTN;
    View view;
    BottomSheetDialog dialog;
    static SubmitAddressFragment submitAddressFragment;
    ApiMethods apiMethods;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        view = View.inflate(getContext(), R.layout.fragment_submit_address, null);
        RelativeLayout linearLayout = view.findViewById(R.id.root);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.height = getScreenHeight();
        linearLayout.setLayoutParams(params);
        initialize();
        // Inflate the layout for this fragment

        currentlocationTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                runShowBottomsheetThread();
            }
        });
        radiogrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.homeRB:
                        // do operations specific to this selection
                        addresstype = "home";
                        break;
                    case R.id.officeRB:
                        // do operations specific to this selection
                        addresstype = "office";
                        break;
                    case R.id.otherRB:
                        // do operations specific to this selection
                        addresstype = "other";
                        break;
                }

            }
        });


        submitBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adresslinee1 = addressline1ET.getText().toString();
                addressline2 = addressline2ET.getText().toString();
                flatno = floorET.getText().toString();
                landmark = landmarkET.getText().toString();
                pincode = pincodeET.getText().toString();

                if (adresslinee1.isEmpty()) {
                    openDialog("Enter your address line 1", "warning");
                } else if (pincode.isEmpty()) {
                    openDialog("Enter your Pin Code", "warning");

                } else {
                    checkConnection();
                    if (isConnected)
                    {
                        HideKeyborad.hideKeyboard(getActivity());
                        submitAddress();
                    }
                    else
                    {
                        openDialog("No Internet Connecions", "warning");
                    }
                }

                //               }


            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;

    }

    public void runShowBottomsheetThread() {

        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            AddressDialogFragment addPhotoBottomDialogFragment =
                                    AddressDialogFragment.newInstance();
                            addPhotoBottomDialogFragment.show(getChildFragmentManager(),
                                    AddressDialogFragment.TAG);
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private void openDialog(String msg, String imagetype) {
        final Dialog errdialog = new Dialog(getActivity(), R.style.CustomDialog);
        errdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        errdialog.setContentView(R.layout.item_dialogbox);
        errdialog.setCanceledOnTouchOutside(false);
        errdialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        errdialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        errdialog.show();
        TextView descriptionTV = errdialog.findViewById(R.id.descriptionTV);
        TextView titleTV = errdialog.findViewById(R.id.titleTV);
        ImageView imageView = errdialog.findViewById(R.id.imageView);
        Button okBtn = errdialog.findViewById(R.id.okBtn);

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
                    errdialog.dismiss();
                } else {
                    errdialog.dismiss();

                }


            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }


    private void initialize() {
        submitAddressFragment = this;
        apiMethods = new ApiMethods();
        progressDialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        lat = session.getAddressData().get(SessionManager.KEY_LATITUDE);
        lng = session.getAddressData().get(SessionManager.KEY_LONGITUDE);
        relativelayout = view.findViewById(R.id.relativelayout);
        currentlocationTV = view.findViewById(R.id.currentlocationTV);
        floorET = view.findViewById(R.id.floorET);
        addressline1ET = view.findViewById(R.id.addressline1ET);
        addressline2ET = view.findViewById(R.id.addressline2ET);
        landmarkET = view.findViewById(R.id.landmarkET);
        pincodeET = view.findViewById(R.id.pincodeET);
        radiogrp = view.findViewById(R.id.radiogrp);
        homeRB = view.findViewById(R.id.homeRB);
        officeRB = view.findViewById(R.id.officeRB);
        otherRB = view.findViewById(R.id.otherRB);
        submitBTN = view.findViewById(R.id.submitBTN);


        Toolbar toolbar = view.findViewById(R.id.toolbarmain);
        TextView toolbar_title = view.findViewById(R.id.toolbar_title);
        backIV = toolbar.findViewById(R.id.plusimage);
        ImageView logoutImage = toolbar.findViewById(R.id.logoutImage);
        toolbar_title.setText("Address");
        logoutImage.setVisibility(View.GONE);

        runThread();

    }

    public static SubmitAddressFragment getInstance() {
        return submitAddressFragment;
    }

    public void runThread() {


        new Thread() {
            public void run() {
                try {

                    // here you check the value of getActivity() and break up if needed
                    if (getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {


                            location = session.getAddressData().get(SessionManager.KEY_CURRENTADDRESS);
                            currentlocationTV.setText(location);


                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();


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

    private void checkConnection() {
        isConnected = ConnectivityReceiver.isConnected();
    }


    private void submitAddress()
    {
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.SAVE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("SAVE_ADDRESS", response);

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                String address = flatno + ", " + adresslinee1 + ", " + addressline2 + ", " + landmark + ", \nPin code:" + pincode;
                                session.setDeliveryAddress(response, address, adresslinee1, addressline2, pincode);
                                session.setAddressData(location, lat, lng);
                                OrderFragment.getInstance().runThread("setorderaddress");
                                HomeFragment.getInstance().runThread();
                                apiMethods.loadAddress(getActivity(), "addresshistory");

                                Toast.makeText(getActivity(), "Address Added", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                                dialog.dismiss();

                            } else {
                                openDialog("Something went wrong.", "warning");

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

                params.put("Guest_id", session.getLoginData().get(SessionManager.KEY_ID));
                params.put("Address", adresslinee1);
                params.put("Address2", addressline2);
                params.put("PinNo", pincode);
                params.put("Location", location);
                params.put("Floor", flatno);
                params.put("Landmark", landmark);
                params.put("Add_Type", addresstype);
                params.put("Latitude", lat);
                params.put("Longitude", lng);
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