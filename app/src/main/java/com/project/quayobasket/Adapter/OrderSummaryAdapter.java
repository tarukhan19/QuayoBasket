package com.project.quayobasket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.QuayoBasketDTO;
import com.project.quayobasket.databinding.ItemOrdersummaryBinding;
import com.project.quayobasket.session.SessionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.TasksViewHolder> {

    private Context mCtx;
    private List<QuayoBasketDTO> taskList;
    Activity activity;
    ItemOrdersummaryBinding binding;
    SessionManager sessionManager;



    private TextView paidamountTV;



    public OrderSummaryAdapter(Context mCtx, List<QuayoBasketDTO> taskList) {
        this.mCtx = mCtx;
        this.taskList = taskList;
    }


    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_ordersummary, parent, false);
        return new TasksViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final TasksViewHolder holder, final int position)
    {



        holder.itemRowBinding.nameTV.setText(taskList.get(position).getProductName());
        holder.itemRowBinding.qtyTV.setText("Quantity: " + taskList.get(position).getQuantity());
        holder.itemRowBinding.calculationTV.setText( taskList.get(position).getQuantity()+" X " + taskList.get(position).getAmountplustax());
        holder.itemRowBinding.descrTV.setText(taskList.get(position).getDescription());
        double amount=taskList.get(position).getQuantity() * taskList.get(position).getAmountplustax();
        BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
        amount =bd.doubleValue() ;
        holder.itemRowBinding.amountTV.setText("Rs. "+String.valueOf(amount));
//        if (taskList.get(position).getIsvegnonveg().equalsIgnoreCase("1"))
//        {
//            holder.itemRowBinding.vegnvwithoutimageIV.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.nonvegdot));
//        }
//        else
//        {
//            holder.itemRowBinding.vegnvwithoutimageIV.setImageDrawable(mCtx.getResources().getDrawable(R.drawable.vegdot));
//
//        }






    }


    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public class TasksViewHolder extends RecyclerView.ViewHolder
    {


        ItemOrdersummaryBinding itemRowBinding;

        public TasksViewHolder(ItemOrdersummaryBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }





}