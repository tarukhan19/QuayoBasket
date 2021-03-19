package com.project.quayobasket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.Model.OffersDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.databinding.ItemOffersListBinding;
import com.project.quayobasket.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<OffersDTO> offersDTOList;
    Activity activity;
    SessionManager sessionManager;
    ItemOffersListBinding binding;
    BottomSheetDialog bottomSheet;
    double discountvalue,uptoamount,minorder;
    public OffersAdapter(Context mcontex, ArrayList<OffersDTO> offersDTOList, BottomSheetDialog bottomSheet) {

        this.mcontex = mcontex;
        this.offersDTOList = offersDTOList;
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
        this.bottomSheet=bottomSheet;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_offers_list, parent, false);
        return new ViewHolderPollAdapter(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position)
    {
        discountvalue=Double.parseDouble(offersDTOList.get(position).getDiscountValue());
        uptoamount=Double.parseDouble(offersDTOList.get(position).getDiscapvalue());
        minorder=Double.parseDouble(offersDTOList.get(position).getMinOrder());

//        String ta=sessionManager.getTotalRateForOffers().get(SessionManager.KEY_TOTALRATE_FOROFFERS);
//        totalamount=Double.parseDouble(ta);

        if (offersDTOList.get(position).getDiscoutType().equalsIgnoreCase("pct"))
        {
        holder.itemRowBinding.percentmsgTV.setText("GET "+discountvalue+"% OFF UPTO RS." +uptoamount);
        }
        else
        {
        holder.itemRowBinding.percentmsgTV.setText("GET Rs."+discountvalue+" OFF UPTO RS." +uptoamount);
        }
        holder.itemRowBinding.promocodeTV.setText(offersDTOList.get(position).getPromocode());

        holder.itemRowBinding.validmsgTV.setText("Valid on orders above Rs." +minorder);

        holder.itemRowBinding.applyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                offerscalculation(position);
            }
        });

    }


    public void offerscalculation(int position)
    {

        double amountorperc=Double.parseDouble(offersDTOList.get(position).getDiscountValue());
        double uptoamount=Double.parseDouble(offersDTOList.get(position).getDiscapvalue());
        double minamount=Double.parseDouble(offersDTOList.get(position).getMinOrder());
//        if (percentamount<uptoamount)
//        {
//            promoamountrupee=percentamount;
//        }
//        else
//        {
//            promoamountrupee=uptoamount;
//        }

        try {
            OrderFragment.getInstance().runThread(offersDTOList.get(position).getPromocode(),
                    amountorperc,minamount,uptoamount,offersDTOList.get(position).getDiscoutType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        bottomSheet.dismiss();
    }

    @Override
    public int getItemCount() {
        return offersDTOList != null ? offersDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {


        ItemOffersListBinding itemRowBinding;

        public ViewHolderPollAdapter(ItemOffersListBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }

}


