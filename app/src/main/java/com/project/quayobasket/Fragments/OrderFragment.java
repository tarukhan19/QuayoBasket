package com.project.quayobasket.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.project.quayobasket.Adapter.TasksAdapter;
import com.project.quayobasket.DialogFragment.AddressLHistoryFragment;
import com.project.quayobasket.DialogFragment.OffersFragment;
import com.project.quayobasket.DialogFragment.SubmitAddressFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.QuayoBasketDTO;
import com.project.quayobasket.RoomPersistanceLibrary.SqliteDbMethod;
import com.project.quayobasket.Utils.AppCurrentVersions;
import com.project.quayobasket.databinding.FragmentOrderBinding;
import com.project.quayobasket.session.SessionManager;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


public class OrderFragment extends Fragment {
    FragmentOrderBinding binding;
    RequestQueue queue;
    SessionManager session;
    static OrderFragment orderFragment;
    private List<QuayoBasketDTO> quayoBasketDTOList;
    double totalrate = 0.0, totaldeliverycharge = 0.0, totaltax = 0.0, grandtotal = 0.0,
            promocodeDiscountAmount = 0.0, promocodeminamount = 0.0,totalrateforoffercal=0.0,
            promocodeuptoamount = 0.0, promocodeamountorperc = 0.0, calculatAmount = 0.0;
    AppCurrentVersions appCurrentVersions;
    String appversion;
    String promocodeMsg="", promocodeoffermsg="", promoCode="", promocodediscounttype="";
    boolean isOffer=false,isupdate=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);
        View view = binding.getRoot();
        initialize();

        binding.applyofferTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                new OffersFragment().show(getChildFragmentManager(), "search_dialog");
            }
        });

        binding.editaddressLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AddressLHistoryFragment().show(getChildFragmentManager(), "search_dialog");

            }
        });

        binding.changeAddressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SubmitAddressFragment().show(getChildFragmentManager(), "search_dialog");

            }
        });

        binding.removeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.b4applyLL.setVisibility(View.VISIBLE);
                binding.promoLL.setVisibility(View.GONE);
                binding.afterapplyLL.setVisibility(View.GONE);
                promocodeDiscountAmount=0.0;
                otherCalculation(quayoBasketDTOList,"remove");
            }
        });
        return view;
    }
    private void setorderaddress() throws Exception {
        if (!session.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_DELIVERY_ID).isEmpty()) {
            binding.editaddressLL.setVisibility(View.VISIBLE);
            binding.deliveryaddressTV.setText(session.getDeliveryAddress().get(SessionManager.KEY_ADDRESS_DELIVERY));
        } else {
            binding.editaddressLL.setVisibility(View.GONE);

        }
    }


    public static OrderFragment getInstance() {
        return orderFragment;
    }



    private void initialize()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        appCurrentVersions = new AppCurrentVersions();
        appversion = appCurrentVersions.getCurrentVersion(getActivity());
        binding.versioncodeTV.setText(appversion);

        queue = Volley.newRequestQueue(getActivity());
        session = new SessionManager(getActivity().getApplicationContext());
        orderFragment = this;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.scheduleLayoutAnimation();

        if (session.isLoggedIn()) {
            binding.addressCV.setVisibility(View.VISIBLE);

        } else {
            binding.addressCV.setVisibility(View.GONE);

        }
//        getTasks();
        new SqliteDbMethod().getTasks(getActivity(),"order");
        try {
            setorderaddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //2nd
    public void runThread(List<QuayoBasketDTO> quayoBasketList)
    {
        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            quayoBasketDTOList=quayoBasketList
                            ;
                            runThread("setvisibility");
                            TasksAdapter adapter = new TasksAdapter(getActivity(), quayoBasketDTOList, binding.nrfLL,
                                    binding.recyclerView, binding.checkoutLL);
                            binding.recyclerView.setAdapter(adapter);
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    public void runThread(String from) {

        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            if (from.equalsIgnoreCase("getTasks")) {
                                isupdate=false;
                                new SqliteDbMethod().getTasks(getActivity(),"order");
                            }
                            else  if (from.equalsIgnoreCase("updateTasks")) {
                                isupdate=true;
                                new SqliteDbMethod().getTasks(getActivity(),"order");
                            }
                            else if (from.equalsIgnoreCase("setvisibility")) {
                                getVisiblity(quayoBasketDTOList);
                            } else if (from.equalsIgnoreCase("setorderaddress"))
                            {
                                try {
                                    setorderaddress();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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

    public void getVisiblity(List<QuayoBasketDTO> tasklist) {
        if (tasklist.size() == 0) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.checkoutLL.setVisibility(View.GONE);
            binding.nrfLL.setVisibility(View.VISIBLE);
            binding.calcCV.setVisibility(View.GONE);
            binding.offersCV.setVisibility(View.GONE);


        } else
        {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.checkoutLL.setVisibility(View.VISIBLE);
            binding.nrfLL.setVisibility(View.GONE);
            binding.calcCV.setVisibility(View.VISIBLE);
            binding.offersCV.setVisibility(View.VISIBLE);

            otherCalculation(tasklist,"add");



        }


    }
    public void runThread(String promocode, double amountorperc,
                          double minamount, double uptoamount, String discounttype) throws Exception {
        new Thread() {
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {

                            promoCode = promocode;
                            promocodeminamount = minamount;
                            promocodeuptoamount = uptoamount;
                            promocodediscounttype = discounttype;
                            promocodeamountorperc = amountorperc;

                            offerCalculation(session.getTotalRateForOffers().get(SessionManager.KEY_TOTALRATE_FOROFFERS));
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }








    public void otherCalculation(List<QuayoBasketDTO> tasklist,String from)
    {
        totalrate = 0.0;
        grandtotal = 0.0;
        totaltax = 0.0;
        totaldeliverycharge = 0.0;
        isOffer=false;
        for (int i = 0; i < tasklist.size(); i++)
        {
            double rate = (quayoBasketDTOList.get(i).getProductMRP());
            BigDecimal bd = new BigDecimal(quayoBasketDTOList.get(i).getTaxamount()).setScale(2, RoundingMode.HALF_UP);
            double taxamount = bd.doubleValue();
            int quant = quayoBasketDTOList.get(i).getQuantity();
            totalrate = totalrate + (quant * rate);
            totaltax = totaltax + (quant * taxamount);
        }


            totalRateForOffer(from);


        if (from.equalsIgnoreCase("add"))
        {
            if (isOffer)
            {
                binding.offersCV.setVisibility(View.VISIBLE);

            }
            else
            {
                binding.offersCV.setVisibility(View.GONE);
                binding.b4applyLL.setVisibility(View.GONE);
                binding.promoLL.setVisibility(View.GONE);
                binding.afterapplyLL.setVisibility(View.GONE);
                promocodeDiscountAmount=0.0;
            }
        }

        BigDecimal trbd = new BigDecimal(totalrate).setScale(2, RoundingMode.HALF_UP);
        totalrate = trbd.doubleValue();
        BigDecimal ttbd = new BigDecimal(totaltax).setScale(2, RoundingMode.HALF_UP);
        totaltax = ttbd.doubleValue();


        Log.e("totalrateforoffercal3",totalrateforoffercal+"");
        Log.e("promocodeDiscountAmount",promocodeDiscountAmount+"");
        Log.e("totaltax",totaltax+"");
        Log.e("totalrate",totalrate+"");



            grantTotalCalc();



        binding.itemtotalTV.setText(String.valueOf("Rs. " + totalrate));
        binding.taxchargesTV.setText(String.valueOf("Rs. " + totaltax));
        binding.deliveryChargesTV.setText(String.valueOf("Rs. " + totaldeliverycharge));


    }

    public void totalRateForOffer(String from)
    {
        totalrateforoffercal=0.0;
        if (from.equalsIgnoreCase("add"))
        {
            for (int i = 0; i < quayoBasketDTOList.size(); i++)
            {
                double rate = (quayoBasketDTOList.get(i).getProductMRP());
                int quant = quayoBasketDTOList.get(i).getQuantity();
                int isDiscount=quayoBasketDTOList.get(i).getIsDiscount();
                Log.e("totalrateforoffercal1",totalrateforoffercal+"");

                if (isDiscount==1 )
                {
                    isOffer=true;
                    totalrateforoffercal=totalrateforoffercal+ (quant * rate);
                    Log.e("totalrateforoffercal2",totalrateforoffercal+"");
                    session.setTotalRateForOffers(String.valueOf(totalrateforoffercal));

                }


            }
            BigDecimal trocbd = new BigDecimal(totalrateforoffercal).setScale(2, RoundingMode.HALF_UP);
            totalrateforoffercal = trocbd.doubleValue();

            if (isupdate  && !promoCode.isEmpty())
            {
                offerCalculation(String.valueOf(totalrateforoffercal));
            }
        }


    }

    public  void grantTotalCalc()
    {
        grandtotal = totaldeliverycharge + (totalrateforoffercal - promocodeDiscountAmount) + totaltax
                + (totalrate-totalrateforoffercal);
        BigDecimal bd = new BigDecimal(grandtotal).setScale(2, RoundingMode.HALF_UP);
        grandtotal = bd.doubleValue();

        binding.grandtotalTV.setText(String.valueOf("Rs. " + grandtotal));
        binding.paidamountTV.setText(String.valueOf("Rs. " + grandtotal));


    }

    private void offerCalculation(String totalrateforoffer) {
        totalrateforoffercal=Double.parseDouble(totalrateforoffer);

        if (totalrateforoffercal >= promocodeminamount)
        {
            binding.b4applyLL.setVisibility(View.GONE);
            binding.promoLL.setVisibility(View.VISIBLE);
            binding.afterapplyLL.setVisibility(View.VISIBLE);


            promocodeMsg = "Code " + promoCode + " applied.";
            if (promocodediscounttype.equalsIgnoreCase("pct")) {

                promocodeoffermsg = "GET " + promocodeamountorperc + "% OFF UPTO RS." + promocodeuptoamount;
                calculatAmount = (promocodeamountorperc / 100) * totalrateforoffercal;

                BigDecimal ttbd = new BigDecimal(calculatAmount).setScale(2, RoundingMode.HALF_UP);
                calculatAmount = ttbd.doubleValue();
            } else {
                promocodeoffermsg = "GET Rs." + promocodeamountorperc + " OFF UPTO RS." + promocodeuptoamount;
                calculatAmount = promocodeamountorperc;

                BigDecimal ttbd = new BigDecimal(calculatAmount).setScale(2, RoundingMode.HALF_UP);
                calculatAmount = ttbd.doubleValue();
            }

            if (calculatAmount > promocodeuptoamount) {
                promocodeDiscountAmount = promocodeuptoamount;
            } else {
                promocodeDiscountAmount = calculatAmount;
            }

            binding.promocodenameAATV.setText(promocodeMsg + "");
            binding.promopercmsgAATV.setText(promocodeoffermsg + "");
            binding.promonameTV.setText("promo - (" + promoCode + ")");
            binding.offeramountAATV.setText("-Rs." + promocodeDiscountAmount + "");
            binding.promoamountTV.setText("-Rs." + promocodeDiscountAmount + "");

            if (!isupdate)
            {
                otherCalculation(quayoBasketDTOList
                        ,"add");

            }

        }
        else
        {
            binding.b4applyLL.setVisibility(View.VISIBLE);
            binding.promoLL.setVisibility(View.GONE);
            binding.afterapplyLL.setVisibility(View.GONE);
            promocodeDiscountAmount = 0.0;


        }





    }


}