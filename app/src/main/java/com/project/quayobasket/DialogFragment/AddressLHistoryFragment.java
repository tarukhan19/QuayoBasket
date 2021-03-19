package com.project.quayobasket.DialogFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.quayobasket.Adapter.AddressAdapter;
import com.project.quayobasket.Model.AddressDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.ApiMethods;
import com.project.quayobasket.databinding.FragmentAddressLHistoryBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AddressLHistoryFragment extends BottomSheetDialogFragment {

    ArrayList<AddressDTO> addressDTOArrayList;
    AddressAdapter adapter;
    static AddressLHistoryFragment addressLHistoryFragment;
    private BottomSheetBehavior mBehavior;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    ApiMethods apiMethods;
    FragmentAddressLHistoryBinding binding;
    BottomSheetDialog bottomSheet;
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_address_l_history, null);
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

        binding.changeAddressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SubmitAddressFragment().show(getChildFragmentManager(), "search_dialog");

            }
        });
        return bottomSheet;
    }

    private void initialize() {
        progressDialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        addressDTOArrayList = new ArrayList<>();
        adapter = new AddressAdapter(getActivity(), addressDTOArrayList, bottomSheet);
        addressLHistoryFragment = this;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setAdapter(adapter);

        apiMethods = new ApiMethods();
        apiMethods.loadAddress(getActivity(), "addresshistory");
        binding.progressbar.setVisibility(View.VISIBLE);


    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public static AddressLHistoryFragment getInstance() {
        return addressLHistoryFragment;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void runThread(String response) {


        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            try {
                                loadAddress(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();


    }

    private void loadAddress(String response) throws Exception{

        addressDTOArrayList.clear();
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt("Code");
            String status = jsonObject.getString("Status");

            if (code == 200 && status.equalsIgnoreCase("Success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");
                if (jsonArray.length()!=0) {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.progressbar.setVisibility(View.GONE);
                    binding.noadressTV.setVisibility(View.GONE);


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);

                        AddressDTO addressDTO = new AddressDTO();
                        addressDTO.setAddress(object.getString("location"));
                        addressDTO.setAddress1(object.getString("Address1"));
                        addressDTO.setAddress2(object.getString("address2"));
                        addressDTO.setAddresstype((object.getString("Add_type")));
                        addressDTO.setAddrId(object.getString("AddrId"));
                        addressDTO.setFlatno(object.getString("flat_street"));
                        addressDTO.setLandmark(object.getString("landmark"));
                        addressDTO.setMobile_No(object.getString("Mobile_No"));
                        addressDTO.setName(object.getString("Name"));
                        addressDTO.setPinNo(object.getString("PinNo"));
                        addressDTO.setLat(object.getDouble("Latitude"));
                        addressDTO.setLng(object.getDouble("Longitude"));


                        addressDTOArrayList.add(addressDTO);
                        adapter.notifyDataSetChanged();

                    }
                    adapter.notifyDataSetChanged();
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.progressbar.setVisibility(View.GONE);
                    binding.noadressTV.setVisibility(View.VISIBLE);

                }
            }

            else
            { binding.recyclerView.setVisibility(View.GONE);
                binding.progressbar.setVisibility(View.GONE);
                binding.noadressTV.setVisibility(View.VISIBLE);

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}