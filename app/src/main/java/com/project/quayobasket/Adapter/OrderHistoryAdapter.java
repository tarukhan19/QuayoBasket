package com.project.quayobasket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.project.quayobasket.Model.OrderHistoryDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.databinding.ItemOrderHistoryBinding;
import com.project.quayobasket.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<OrderHistoryDTO> orderHistoryDTOArrayList;
    Activity activity;
    SessionManager sessionManager;
    ItemOrderHistoryBinding binding;

    public OrderHistoryAdapter(Context mcontex, ArrayList<OrderHistoryDTO> orderHistoryDTOArrayList) {

        this.mcontex = mcontex;
        this.orderHistoryDTOArrayList = orderHistoryDTOArrayList;
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_order_history, parent, false);
        return new ViewHolderPollAdapter(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        holder.itemRowBinding.itemTV.setText(orderHistoryDTOArrayList.get(position).getOrderItem());
        holder.itemRowBinding.orderdateTV.setText(orderHistoryDTOArrayList.get(position).getOrder_Date());
        holder.itemRowBinding.totalAmountTV.setText("Rs. "+orderHistoryDTOArrayList.get(position).getOrder_Aount());



    }


    @Override
    public int getItemCount() {
        return orderHistoryDTOArrayList != null ? orderHistoryDTOArrayList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {


        ItemOrderHistoryBinding itemRowBinding;

        public ViewHolderPollAdapter(ItemOrderHistoryBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }

}


