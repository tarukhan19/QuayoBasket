package com.project.quayobasket.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.project.quayobasket.DialogFragment.SubCategoriesItemFragment;
import com.project.quayobasket.Model.BannerDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BannerAdapter extends PagerAdapter {
    private Context mContext;
    SessionManager sessionManager;

    ArrayList<BannerDTO> sliderImgList;

    public BannerAdapter(Context context, ArrayList<BannerDTO> sliderImgList)
    {
        mContext = context;
        this.sliderImgList = sliderImgList;
        sessionManager = new SessionManager(mContext);

    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_banner, collection, false);
        ImageView iv = layout.findViewById(R.id.sliderImg);

        try {
            Picasso.with(mContext)
                    .load(sliderImgList.get(position).getImage())
                    .fit()
                    .into(iv);
        }
        catch (Exception e)
        {}


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyborad.hideKeyboard(mContext);

                SubCategoriesItemFragment dialogFragment = new SubCategoriesItemFragment();
                FragmentTransaction ft =((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
                dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogFragmentTheme);
                sessionManager.setCategoryId(sliderImgList.get(position).getId(),
                        "banner");
                ft.addToBackStack(null);
                dialogFragment.show(ft, "dialog");
            }
        });
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return sliderImgList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}