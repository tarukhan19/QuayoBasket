package com.project.quayobasket.Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.Adapter.BannerAdapter;
import com.project.quayobasket.Adapter.CategoryAdapter;
import com.project.quayobasket.Adapter.OfferImageAdapter;
import com.project.quayobasket.DialogFragment.SubCategoriesItemFragment;
import com.project.quayobasket.Model.BannerDTO;
import com.project.quayobasket.Model.CategoryDTO;
import com.project.quayobasket.Model.OfferImageDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.ConnectivityReceiver;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.GridSpacingItemDecoration;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.FragmentHomeBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener {
    FragmentHomeBinding binding;
    RequestQueue queue;
    SessionManager sessionManager;
    ArrayList<BannerDTO> bannerList;
    ArrayList<OfferImageDTO> offerImageDTOArrayList;
    ArrayList<CategoryDTO> categoryDTOArrayList;
    OfferImageAdapter adapter;
    CategoryAdapter categoryAdapter;
    boolean isConnected;
    static HomeFragment homeFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        View view = binding.getRoot();

        initialize();

        binding.currentAdressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.getInstance().runShowBottomsheetThread();
            }
        });

        binding.searchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubCategoriesItemFragment dialogFragment = new SubCategoriesItemFragment();
                FragmentTransaction ft = (getActivity()).getSupportFragmentManager().beginTransaction();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
                sessionManager.setCategoryId("0", "search");
                ft.addToBackStack(null);
                dialogFragment.show(ft, "dialog");
            }
        });

        binding.swipeToRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        checkConnection();
                        if (isConnected)
                            loadBanner();
                        else
                            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                        binding.swipeToRefresh.setRefreshing(false);
                    }
                }
        );
        return view;
    }

    private void initialize()
    {
        sessionManager = new SessionManager(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        bannerList = new ArrayList<>();
        offerImageDTOArrayList = new ArrayList<>();
        categoryDTOArrayList = new ArrayList<>();
        homeFragment = this;
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new OfferImageAdapter(getActivity(), offerImageDTOArrayList);
        binding.recyclerviewOffer.setLayoutManager(layoutManager);
        binding.recyclerviewOffer.setHasFixedSize(true);
        binding.recyclerviewOffer.setItemViewCacheSize(20);
        binding.recyclerviewOffer.setDrawingCacheEnabled(true);
        binding.recyclerviewOffer.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recyclerviewOffer.setAdapter(adapter);

        categoryAdapter = new CategoryAdapter(getActivity(), categoryDTOArrayList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        binding.recyclerviewCategories.setLayoutManager(mLayoutManager);
        binding.recyclerviewCategories.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(3, getActivity()), true));
        binding.recyclerviewCategories.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerviewCategories.setNestedScrollingEnabled(false);
        binding.recyclerviewCategories.setHasFixedSize(true);
        binding.recyclerviewCategories.setItemViewCacheSize(20);
        binding.recyclerviewCategories.setDrawingCacheEnabled(true);
        binding.recyclerviewCategories.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        binding.recyclerviewCategories.setAdapter(categoryAdapter);

        checkConnection();

        if (isConnected) {
            binding.swipeToRefresh.setRefreshing(true);
            loadBanner();



        } else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
    }

    public int dpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void bindView() {
        binding.autoPager.startAutoScroll();
        binding.autoPager.setInterval(3000);
        binding.autoPager.setCycle(true);
        binding.autoPager.setStopScrollWhenTouch(true);
        BannerAdapter adp = new BannerAdapter(getActivity(), bannerList);
        binding.autoPager.setAdapter(adp);
        binding.indicator.setViewPager(binding.autoPager, 0);
        adp.notifyDataSetChanged();
    }

    public static HomeFragment getInstance() {
        return homeFragment;
    }

    public void runThread() {


        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            if (sessionManager.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_DELIVERY_ID).isEmpty()) {
                                binding.currentAdressTV.setText(sessionManager.getAddressData().get(SessionManager.KEY_CURRENTADDRESS));
                            } else {
                                binding.currentAdressTV.setText(sessionManager.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_DELIVERY));
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

    private void loadBanner() {

        StringRequest postRequest = new StringRequest(Request.Method.GET, EndPoints
                .GET_BANNER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        bannerList.clear();
                        // pd.dismiss();
                        Log.e("GET_BANNER", response);


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                JSONArray imageList = jsonObject.getJSONArray("Data");

                                for (int i = 0; i < imageList.length(); i++) {
                                    JSONObject verticalObj = imageList.getJSONObject(i);

                                    String Id = verticalObj.getString("Id");
                                    String Name = verticalObj.getString("Name");
                                    String Offer_Type = verticalObj.getString("Offer_Type");
                                    String image = verticalObj.getString("Image");
//                                    String desc=verticalObj.getString("I_Name_Desc");

                                    BannerDTO bannerDTO = new BannerDTO();
                                    bannerDTO.setId(Id);
                                    bannerDTO.setImage(image);
                                    bannerDTO.setName(Name);
                                    bannerDTO.setOffer_Type(Offer_Type);

                                    bannerList.add(bannerDTO);

                                    loadOffers();
                                }
                                bindView();
                            } else {
                                binding.swipeToRefresh.setRefreshing(false);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {


        };
        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void loadOffers() {

        StringRequest postRequest = new StringRequest(Request.Method.GET, EndPoints.GET_OFFERS_IMAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    Log.e("GET_OFFERS_IMAGES",response);
                        // pd.dismiss();
                        offerImageDTOArrayList.clear();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                JSONArray imageList = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < imageList.length(); i++) {
                                    JSONObject verticalObj = imageList.getJSONObject(i);

                                    String Id = verticalObj.getString("Id");
                                    String Name = verticalObj.getString("Name");
                                    String Offer_Type = verticalObj.getString("Offer_Type");
                                    String image = verticalObj.getString("Image");

                                    OfferImageDTO offerDTO = new OfferImageDTO();
                                    offerDTO.setId(Id);
                                    offerDTO.setImage(image);
                                    offerDTO.setName(Name);
                                    offerDTO.setOffer_Type(Offer_Type);
                                    offerImageDTOArrayList.add(offerDTO);


                                }
                                adapter.notifyDataSetChanged();

                                loadCategory();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {


        };
        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
    }


    private void loadCategory() {

        StringRequest postRequest = new StringRequest(Request.Method.GET, EndPoints.GET_CATEGORIES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("GET_CATEGORIES",response);

                        // pd.dismiss();
                        categoryDTOArrayList.clear();
                        if (binding.swipeToRefresh.isRefreshing()) {
                            binding.swipeToRefresh.setRefreshing(false);
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success")) {
                                JSONArray imageList = jsonObject.getJSONArray("Data");

                                for (int i = 0; i < imageList.length(); i++) {
                                    JSONObject verticalObj = imageList.getJSONObject(i);

                                    String Id = verticalObj.getString("Id");
                                    String dept = verticalObj.getString("Department");
                                    String image = verticalObj.getString("Image");

                                    CategoryDTO categoryDTO = new CategoryDTO();
                                    categoryDTO.setId(Id);
                                    categoryDTO.setImage(image);
                                    categoryDTO.setDepartment(dept);
                                    categoryDTOArrayList.add(categoryDTO);
                                }
                                categoryAdapter.notifyDataSetChanged();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {


        };
        int socketTimeout = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
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


}