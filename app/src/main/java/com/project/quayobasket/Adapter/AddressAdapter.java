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
import com.project.quayobasket.Fragments.HomeFragment;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.Model.AddressDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.databinding.ItemAddressListBinding;
import com.project.quayobasket.session.SessionManager;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<AddressDTO> addressDTOList;
    Activity activity;
    SessionManager sessionManager;
    ItemAddressListBinding binding;
    BottomSheetDialog dialog;
    public AddressAdapter(Context mcontex, List<AddressDTO> addressDTOList, BottomSheetDialog dialog) {
        this.mcontex = mcontex;
        this.addressDTOList = addressDTOList;
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
        this.dialog=dialog;
    }

    @NonNull
    @Override
    public AddressAdapter.ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_address_list, parent, false);
        return new ViewHolderPollAdapter(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        holder.itemRowBinding.addresstypeTV.setText(addressDTOList.get(position).getAddresstype());
        String address =addressDTOList.get(position).getFlatno()+", "+addressDTOList.get(position).getAddress1()+", "+
                addressDTOList.get(position).getAddress2()+", \nLandmark: "+addressDTOList.get(position).getLandmark();
        holder.itemRowBinding.addressTV.setText(address);
        holder.itemRowBinding.pincodeTV.setText(addressDTOList.get(position).getPinNo());




        holder.itemRowBinding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sessionManager.setDeliveryAddress(addressDTOList.get(position).getAddrId(),address,
                       addressDTOList.get(position).getAddress1(),
                       addressDTOList.get(position).getAddress2(),addressDTOList.get(position).getPinNo());
                sessionManager.setAddressData(addressDTOList.get(position).getAddress(),String.valueOf(addressDTOList.get(position).getLat()),
                       String.valueOf( addressDTOList.get(position).getLng()));

                OrderFragment.getInstance().runThread("setorderaddress");
                HomeFragment.getInstance().runThread();
               dialog.dismiss();
            }
        });

    }


    @Override
    public int getItemCount() {
        return addressDTOList != null ? addressDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {

        ItemAddressListBinding itemRowBinding;

        public ViewHolderPollAdapter(ItemAddressListBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }


}
