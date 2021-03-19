package com.project.quayobasket.DialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.project.quayobasket.Adapter.OffersAdapter;
import com.project.quayobasket.Model.OffersDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.databinding.FragmentOffersBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class OffersFragment extends BottomSheetDialogFragment {

    ArrayList<OffersDTO> offersDTOArrayList;
    OffersAdapter adapter;
    static OffersFragment offersFragment;
    private BottomSheetBehavior mBehavior;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    FragmentOffersBinding binding;
    BottomSheetDialog bottomSheet;
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_offers, null);
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
        progressDialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        offersDTOArrayList = new ArrayList<>();
        adapter = new OffersAdapter(getActivity(), offersDTOArrayList, bottomSheet);
        offersFragment = this;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setAdapter(adapter);


        binding.progressbar.setVisibility(View.VISIBLE);

        try {
            loadOffers();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public static OffersFragment getInstance() {
        return offersFragment;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }



    private void loadOffers() throws Exception{

        StringRequest postRequest = new StringRequest(Request.Method.POST, EndPoints.GET_OFFERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        offersDTOArrayList.clear();

                        Log.e("GET_ORDER_HISTORY", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                                if (jsonArray.length()!=0) {
                                    binding.recyclerView.setVisibility(View.VISIBLE);
                                    binding.progressbar.setVisibility(View.GONE);
                                    binding.nooffersTV.setVisibility(View.GONE);


                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject object = jsonArray.getJSONObject(i);

                                        OffersDTO offersDTO = new OffersDTO();
                                        offersDTO.setOfferid(object.getString("Offer_Id"));
                                        offersDTO.setOffertype(object.getString("Offer_type"));
                                        offersDTO.setOutletid(object.getString("Outlet_Id"));
                                        offersDTO.setDiscoutType(object.getString("Discount_type"));
                                        offersDTO.setDiscountValue(object.getString("Discount_Value"));
                                        offersDTO.setMinOrder(object.getString("Min_Order"));
                                        offersDTO.setDiscapvalue(object.getString("dis_cap_value"));
                                        offersDTO.setStartdate(object.getString("StartDate"));
                                        offersDTO.setEnddate(object.getString("EndDate"));
                                        offersDTO.setPromocode(object.getString("PromoCode"));


                                        offersDTOArrayList.add(offersDTO);
                                        adapter.notifyDataSetChanged();

                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    binding.recyclerView.setVisibility(View.GONE);
                                    binding.progressbar.setVisibility(View.GONE);
                                }
                            }

                            else
                            {
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.progressbar.setVisibility(View.GONE);
                                binding.nooffersTV.setVisibility(View.VISIBLE);

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
              params.put("MinOrder", session.getTotalRateForOffers().get(SessionManager.KEY_TOTALRATE_FOROFFERS));
              // params.put("MinOrder", "0");

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