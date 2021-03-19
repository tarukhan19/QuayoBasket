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
import com.project.quayobasket.Model.CategoryDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.databinding.ItemCategoryBinding;
import com.project.quayobasket.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter  extends RecyclerView.Adapter<CategoryAdapter.ViewHolderPollAdapter> {
    private Context mcontex;
    private List<CategoryDTO> categoryDTOList;
    Activity activity;
    SessionManager sessionManager;
    ItemCategoryBinding binding;

    public CategoryAdapter(Context mcontex, List<CategoryDTO> categoryDTOList) {
        this.mcontex = mcontex;
        this.categoryDTOList = categoryDTOList;
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_category, parent, false);
        return new ViewHolderPollAdapter(binding);
    }



    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {

        holder.itemRowBinding.textview.setText(categoryDTOList.get(position).getDepartment());
        if(categoryDTOList.get(position).getImage().isEmpty())
        {
            try {
                Picasso.with(mcontex)
                        .load(R.color.purple_inactive)

                        .into(holder.itemRowBinding.imageView);
            }
            catch (Exception e)
            {

            }

        }
        else
        {
            try {
                Picasso.with(mcontex)
                        .load(categoryDTOList.get(position).getImage())
                        .placeholder(R.color.blue_bg_light)

                        .into(holder.itemRowBinding.imageView);
            }
            catch (Exception e)
            {

            }
        }

        holder.itemRowBinding.linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyborad.hideKeyboard(mcontex);

                SubCategoriesItemFragment dialogFragment = new SubCategoriesItemFragment();
                FragmentTransaction ft =((AppCompatActivity)mcontex).getSupportFragmentManager().beginTransaction();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
                sessionManager.setCategoryId(categoryDTOList.get(position).getId(), "category");
                ft.addToBackStack(null);
                dialogFragment.show(ft, "dialog");
            }
        });

    }


    @Override
    public int getItemCount() {
        return categoryDTOList != null ? categoryDTOList.size() : 0;
    }

    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {


        ItemCategoryBinding itemRowBinding;

        public ViewHolderPollAdapter(ItemCategoryBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }

}