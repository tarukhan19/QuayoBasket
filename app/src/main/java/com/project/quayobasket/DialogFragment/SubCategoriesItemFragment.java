package com.project.quayobasket.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
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
import com.project.quayobasket.Adapter.SubCategoryAdapter;
import com.project.quayobasket.Interface.SubcategoryAdpaterListener;
import com.project.quayobasket.Model.SubCategoryDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.QuayoBasketDTO;
import com.project.quayobasket.RoomPersistanceLibrary.SqliteDbMethod;
import com.project.quayobasket.Utils.ConnectivityReceiver;
import com.project.quayobasket.Utils.EndPoints;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.FragmentSubCategoriesItemBinding;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class SubCategoriesItemFragment extends DialogFragment implements ConnectivityReceiver.ConnectivityReceiverListener,
        SubcategoryAdpaterListener {
    FragmentSubCategoriesItemBinding binding;
    ProgressDialog progressDialog;
    RequestQueue queue;
    SessionManager session;
    SubCategoryAdapter adapter;
    List<SubCategoryDTO> subCategoryDTOList,vegsubCategoryDTOList,nvsubCategoryDTOList;
    String categoryId,from;
    boolean isConnected;
    Dialog dialog;
    ImageView backIV;
    LinearLayout micLL;
    List<QuayoBasketDTO> quayoBasketDTOList;
    SubcategoryAdpaterListener listener;
    EditText titleET;
    RelativeLayout cartLL;
    TextView countTV, amountTV;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String itemName="",URL="";
    static SubCategoriesItemFragment subCategoriesItemFragment;

    private BottomSheetBehavior addtocartBottomSheet;

    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        dialog = super.onCreateDialog(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()),
                R.layout.fragment_sub_categories_item,null, false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogFragmentAnimation;
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();
        initialize();

        micLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        binding.reloadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();

                if (isConnected) {


                    if (!from.equalsIgnoreCase("search"))
                    {
                        new SqliteDbMethod().getTasks(getActivity(),"subcategoryitem");
                        binding.dataLL.setVisibility(View.VISIBLE);
                        binding.norecrLL.setVisibility(View.GONE);
                       // loadDetails();
                    }



                } else {
                    binding.norecrLL.setVisibility(View.VISIBLE);
                    binding.textview.setText("No Internet Connection!");
                }


            }
        });

        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!from.equalsIgnoreCase("search"))
                {
                    listener.filterProduct(String.valueOf(charSequence));

                }
                else
                {
                    itemName=String.valueOf(charSequence);
                    if (!itemName.isEmpty())
                    {
                        //loadDetails();
                        new SqliteDbMethod().getTasks(getActivity(),"subcategoryitem");
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addtocartBottomSheet.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {

                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


//        binding.vegSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//
//                    adapter = new SubCategoryAdapter(getActivity(), vegsubCategoryDTOList, listener, cartLL, amountTV, countTV, addtocartBottomSheet);
//                    binding.recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                } else {
//
//                    adapter = new SubCategoryAdapter(getActivity(), subCategoryDTOList, listener, cartLL, amountTV, countTV, addtocartBottomSheet);
//                    binding.recyclerView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//        });

        return dialog;

    }



    private void promptSpeechInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a)
        {
            Toast.makeText(getActivity().getApplicationContext(),getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (!from.equalsIgnoreCase("search"))
                    {
                        listener.filterProduct(String.valueOf(result.get(0)));

                    }
                    else
                    {
                        itemName=String.valueOf(result.get(0));
                        if (!itemName.isEmpty())
                        {
                           // loadDetails();
                            new SqliteDbMethod().getTasks(getActivity(),"subcategoryitem");

                        }

                    }
                }
                break;
            }

        }
    }

    public void setListener(SubcategoryAdpaterListener listener) {
        this.listener = listener;
    }

    private void initialize()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

        subCategoriesItemFragment = this;

        Toolbar toolbar = dialog.findViewById(R.id.toolbar);
        backIV = toolbar.findViewById(R.id.plusimage);
        titleET = toolbar.findViewById(R.id.searchET);
        micLL = toolbar.findViewById(R.id.micLL);
        cartLL = dialog.findViewById(R.id.cartLL);
        countTV = cartLL.findViewById(R.id.countTV);
        amountTV = cartLL.findViewById(R.id.amountTV);


        subCategoryDTOList = new ArrayList<>();
        vegsubCategoryDTOList= new ArrayList<>();
        nvsubCategoryDTOList=new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity());
        queue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        categoryId = session.getCategoryId().get(SessionManager.KEY_CATEGORY_ID);

        checkConnection();


        addtocartBottomSheet = BottomSheetBehavior.from(cartLL);

        setListener(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setNestedScrollingEnabled(false);
        adapter = new SubCategoryAdapter(getActivity(), subCategoryDTOList, this, cartLL, amountTV, countTV, addtocartBottomSheet);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.scheduleLayoutAnimation();

        from=session.getCategoryId().get(SessionManager.KEY_CATEGORY_DEPARTMENT);

        if (isConnected) {

            if (!from.equalsIgnoreCase("search"))
            {
                new SqliteDbMethod().getTasks(getActivity(),"subcategoryitem");

                binding.dataLL.setVisibility(View.VISIBLE);
                binding.norecrLL.setVisibility(View.GONE);

               // loadDetails();
            }

        } else {
            binding.norecrLL.setVisibility(View.VISIBLE);
            binding.textview.setText("No Internet Connection!");
        }
    }

    public static SubCategoriesItemFragment getInstance()
    {
        return subCategoriesItemFragment;
    }

    private void loadDetails() {
        if (!from.equalsIgnoreCase("search"))
        {
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        if (from.equalsIgnoreCase("category") )
        {
            URL= EndPoints.LOAD_CATEGORY_SUBCATEGORY;
        }
        else  if (from.equalsIgnoreCase("banner"))
        {
            URL=EndPoints.LOAD_BANNER_SUBCATEGORY;
        }

        else  if (from.equalsIgnoreCase("offer"))
        {
            URL=EndPoints.LOAD_OFFER_SUBCATEGORY;
        }
        else
        {
            URL=EndPoints.SEARCH;
        }


        StringRequest postRequest = new StringRequest(Request.Method.POST,URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("LOAD_SUBCATEGORY",response);
                        if (progressDialog.isShowing())
                        {
                            progressDialog.dismiss();

                        }
                        subCategoryDTOList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int code = jsonObject.getInt("Code");
                            String status = jsonObject.getString("Status");

                            if (code == 200 && status.equalsIgnoreCase("Success"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                                if (jsonArray.length() > 0) {
                                    binding.dataLL.setVisibility(View.VISIBLE);
                                    binding.norecrLL.setVisibility(View.GONE);

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
                                        //  subCategoryDTO.setDept(object.getString("dept"));
                                        //   subCategoryDTO.setDiscount(object.getString("Discount"));

                                        double tax=object.optDouble("Tax");
                                        double amount=object.optDouble("Rate");
                                        double strikeamount=object.optDouble("StrikePrice");
                                        double taxamount=((tax/100)*amount);
                                        double prodamountplustax=amount+taxamount;


                                        subCategoryDTO.setI_name(object.optString("i_name"));
                                        subCategoryDTO.setOutlet_id(object.optString("outlet_id"));
                                        subCategoryDTO.setRate(amount);
                                        subCategoryDTO.setItem_id(object.getString("I_Code"));
                                        subCategoryDTO.setTaxpercent(tax);
                                        subCategoryDTO.setTaxamount(taxamount);
                                        subCategoryDTO.setItem_Image(object.optString("Item_Image"));
                                        subCategoryDTO.setItem_Descp(object.optString("Item_Descp"));
                                        subCategoryDTO.setIsvegnong(object.optString("isNonVeg"));
                                        subCategoryDTO.setIsDiscount(object.optInt("IsDiscount"));
                                        subCategoryDTO.setAmountplustax(prodamountplustax);
                                        subCategoryDTO.setStrikeprice(strikeamount);


                                        ///for save in local database
                                        if (quayoBasketDTOList.size() != 0) {
                                            for (int j = 0; j < quayoBasketDTOList.size(); j++) {
                                                if (quayoBasketDTOList.get(j).getProductName().equalsIgnoreCase(object.getString("i_name"))) {
                                                    subCategoryDTO.setAddtocart("1");
                                                    subCategoryDTO.setQuantity(quayoBasketDTOList.get(j).getQuantity());
                                                    break;
                                                } else {
                                                    subCategoryDTO.setAddtocart("0");
                                                    subCategoryDTO.setQuantity(0);

                                                }
                                            }
                                        } else {
                                            subCategoryDTO.setAddtocart("0");
                                            subCategoryDTO.setQuantity(0);
                                        }


                                        subCategoryDTOList.add(subCategoryDTO);
                                        if (object.optString("isNonVeg").equalsIgnoreCase("1"))
                                        {
                                            nvsubCategoryDTOList.add(subCategoryDTO);
                                        }
                                        else
                                        {
                                            vegsubCategoryDTOList.add(subCategoryDTO);

                                        }

                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    binding.dataLL.setVisibility(View.GONE);
                                    binding.norecrLL.setVisibility(View.VISIBLE);
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
                        if (progressDialog.isShowing())
                        {
                            progressDialog.dismiss();

                        }                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();


               if (from.equalsIgnoreCase("category") )
                {
                    params.put("Deptt_ID", session.getCategoryId().get(SessionManager.KEY_CATEGORY_ID));
                }
               else  if (from.equalsIgnoreCase("banner"))
               {
                   params.put("Banner_Id", session.getCategoryId().get(SessionManager.KEY_CATEGORY_ID));
               }

               else  if (from.equalsIgnoreCase("offer"))
                {
                    params.put("Offer_Id", session.getCategoryId().get(SessionManager.KEY_CATEGORY_ID));
                }

               else
               {
                   params.put("Item_Name",itemName );

               }

                Log.e("params",params.toString());
                return params;
            }
        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        queue.add(postRequest);
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

    public void runThread(List<QuayoBasketDTO> quayoBasketDTOS)
    {
        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            quayoBasketDTOList=quayoBasketDTOS;
                            loadDetails();
                           showCart();
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


//    public void getTasks() {
//        class GetTasks extends AsyncTask<Void, Void, List<KutumbDTO>> {
//
//            @SuppressLint("WrongThread")
//            @Override
//            protected List<KutumbDTO> doInBackground(Void... voids) {
//                kutumbDTOList = DatabaseClient
//                        .getInstance(getActivity().getApplicationContext())
//                        .getAppDatabase()
//                        .taskDao()
//                        .getAllProduct();
//
//                return kutumbDTOList;
//            }
//
//            @Override
//            protected void onPostExecute(List<KutumbDTO> tasks) {
//                super.onPostExecute(tasks);
////                TasksAdapter adapter = new TasksAdapter(CartActivity.this, tasks);
////                recyclerView.setAdapter(adapter);
//
//                showCart();
//            }
//        }
//
//        GetTasks gt = new GetTasks();
//        gt.execute();
//    }

    private void showCart()
    {
        getActivity().runOnUiThread(new Runnable()
        {

            @Override
            public void run() {
                try {
                    String countS = session.getCartSize().get(SessionManager.KEY_CART_SIZE);
                    int count = Integer.parseInt(countS);


                    if (count == 1) {
                        cartLL.setVisibility(View.VISIBLE);
                        countTV.setText(String.valueOf(count) + " ITEM");

                    } else if (count > 1) {
                        cartLL.setVisibility(View.VISIBLE);
                        countTV.setText(String.valueOf(count) + " ITEMS");

                    } else {
                        cartLL.setVisibility(View.GONE);
                    }
                } catch (Exception e) {

                }


            }
        });
    }


    @Override
    public void onProductSelected(SubCategoryDTO detailDTO) {

    }

    @Override
    public void filterProduct(String query) {

        adapter.getFilter().filter(query);
    }
}
