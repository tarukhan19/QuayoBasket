package com.project.quayobasket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.project.quayobasket.DialogFragment.SubCategoriesItemFragment;
import com.project.quayobasket.Model.OfferImageDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.databinding.ItemOfferimageBinding;
import com.project.quayobasket.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OfferImageAdapter extends RecyclerView.Adapter<OfferImageAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<OfferImageDTO> offerImageDTOList;
    Activity activity;
    SessionManager sessionManager;
    ItemOfferimageBinding binding;

    public OfferImageAdapter(Context mcontex, List<OfferImageDTO> offerImageDTOList) {
        this.mcontex = mcontex;
        this.offerImageDTOList = offerImageDTOList;
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_offerimage, parent, false);
        return new ViewHolderPollAdapter(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        holder.itemRowBinding.textview.setText(offerImageDTOList.get(position).getName());

            try {
                Picasso.with(mcontex)
                        .load(offerImageDTOList.get(position).getImage())

                        .into(holder.itemRowBinding.imageView);
            }
            catch (Exception e)
            {}


        holder.itemRowBinding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HideKeyborad.hideKeyboard(mcontex);
                    sessionManager.setCategoryId(offerImageDTOList.get(position).getId(),
                            "offer");
                    SubCategoriesItemFragment dialogFragment = new SubCategoriesItemFragment();
                    FragmentTransaction ft =((AppCompatActivity)mcontex).getSupportFragmentManager().beginTransaction();
                    dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);

                    ft.addToBackStack(null);
                    dialogFragment.show(ft, "dialog");
                }
                catch (Exception e)
                {}

            }
        });
    }


    @Override
    public int getItemCount() {
        return offerImageDTOList != null ? offerImageDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {


        ItemOfferimageBinding itemRowBinding;

        public ViewHolderPollAdapter(ItemOfferimageBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }

}