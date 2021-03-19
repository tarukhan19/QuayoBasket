package com.project.quayobasket.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.Interface.SubcategoryAdpaterListener;
import com.project.quayobasket.Model.SubCategoryDTO;
import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.DatabaseClient;
import com.project.quayobasket.RoomPersistanceLibrary.QuayoBasketDTO;
import com.project.quayobasket.RoomPersistanceLibrary.SqliteDbMethod;
import com.project.quayobasket.Utils.HideKeyborad;
import com.project.quayobasket.databinding.ItemSubcategoryBinding;
import com.project.quayobasket.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolderPollAdapter> implements Filterable {
    private Context mcontex;
    private List<SubCategoryDTO> subCategoryDTOList, subCategoryDTOListFiltered;
    Activity activity;
    SessionManager sessionManager;
    ItemSubcategoryBinding binding;
    private List<QuayoBasketDTO> quayoBasketDTOList;
   // TasksAdapter adapter;
    RelativeLayout cartLL;
    TextView amountTV, countTV;
    int count;
    SubcategoryAdpaterListener listener;
    int pos, deleteupdatepos;
    BottomSheetBehavior addtocartBottomSheet;
    int quantity;
    static SubCategoryAdapter subCategoryAdapter;

    public SubCategoryAdapter(Context mcontex, List<SubCategoryDTO> subCategoryDTOList,
                              SubcategoryAdpaterListener listener, RelativeLayout cartLL, TextView amountTV, TextView countTV, BottomSheetBehavior addtocartBottomSheet) {
        this.mcontex = mcontex;
        this.subCategoryDTOList = subCategoryDTOList;
        sessionManager = new SessionManager(mcontex);
        activity = (Activity) mcontex;
        this.countTV = countTV;
        this.cartLL = cartLL;
        this.amountTV = amountTV;
        this.addtocartBottomSheet = addtocartBottomSheet;
        this.listener = listener;
        this.subCategoryDTOListFiltered = subCategoryDTOList;
        subCategoryAdapter=this;

    }


    public static SubCategoryAdapter getInstance()
    {
        return subCategoryAdapter;
    }

    @NonNull
    @Override
    public ViewHolderPollAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_subcategory, parent, false);
        return new ViewHolderPollAdapter(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderPollAdapter holder, final int position) {
        new SqliteDbMethod().getTasks(mcontex,"sabcategadapter");


        cartLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HideKeyborad.hideKeyboard(mcontex);

                Intent in7 = new Intent(mcontex, HomeActivity.class);
                in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                in7.putExtra("for","order");
                mcontex.startActivity(in7);
                activity.overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);
            }
        });



        if (subCategoryDTOList.get(position).getStrikeprice()==0  ||
                subCategoryDTOList.get(position).getStrikeprice()==0.0 )
        {
            holder.itemRowBinding.strikepriceTV.setVisibility(View.GONE);
        }
        else
        {
            holder.itemRowBinding.strikepriceTV.setPaintFlags(holder.itemRowBinding.strikepriceTV.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemRowBinding.strikepriceTV.setVisibility(View.VISIBLE);
            holder.itemRowBinding.strikepriceTV.setText(" "+subCategoryDTOList.get(position).getStrikeprice());


        }

        holder.itemRowBinding.nameTV.setText(subCategoryDTOList.get(position).getI_name());
        holder.itemRowBinding.priceTV.setText("Rs. " + subCategoryDTOList.get(position).getRate());
        holder.itemRowBinding.descrTV.setText(subCategoryDTOList.get(position).getItem_Descp());
        if (subCategoryDTOList.get(position).getItem_Image().isEmpty())
        {
            holder.itemRowBinding.vegnvwithoutimageIV.setVisibility(View.VISIBLE);
//            if (subCategoryDTOList.get(position).getIsvegnong().equalsIgnoreCase("1"))
//            {
//                holder.itemRowBinding.vegnvwithoutimageIV.setImageDrawable(mcontex.getResources().getDrawable(R.drawable.nonvegdot));
//            }
//            else
//            {
//                holder.itemRowBinding.vegnvwithoutimageIV.setImageDrawable(mcontex.getResources().getDrawable(R.drawable.vegdot));
//
//            }


        } else
        {
            holder.itemRowBinding.vegnvwithoutimageIV.setVisibility(View.GONE);
//            if (subCategoryDTOList.get(position).getIsvegnong().equalsIgnoreCase("1"))
//            {
//                holder.itemRowBinding.vegnvIV.setImageDrawable(mcontex.getResources().getDrawable(R.drawable.nonvegdot));
//            }
//            else
//            {
//                holder.itemRowBinding.vegnvIV.setImageDrawable(mcontex.getResources().getDrawable(R.drawable.vegdot));
//
//            }



        }

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
              try {
                  listener.onProductSelected(subCategoryDTOList.get(position));
              }
            catch (Exception e)
            {}
            }
        });

        if (subCategoryDTOList.get(position).getQuantity() == 0) {
            holder.itemRowBinding.qtyTV.setText("Add");

        } else {
            holder.itemRowBinding.qtyTV.setText(String.valueOf(subCategoryDTOList.get(position).getQuantity()));

        }


        if (!sessionManager.getCartSize().get(SessionManager.KEY_CART_SIZE).equalsIgnoreCase("0")) {
            count = Integer.parseInt(sessionManager.getCartSize().get(SessionManager.KEY_CART_SIZE));

        }

        holder.itemRowBinding.plusTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    pos = position;
                    plusMethod();

                } catch (Exception e) {
                }


            }
        });


        holder.itemRowBinding.minusTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pos = position;
                    minusMethod();
                } catch (Exception e) {
                }

            }
        });


    }

    private void plusMethod()
    {

            quantity = subCategoryDTOList.get(pos).getQuantity();
            if (quantity >= 0) {
                quantity = quantity + 1;
                count = count + 1;
                sessionManager.setCartSize(String.valueOf(count));


                if (quayoBasketDTOList.size() == 0) {

                    SaveTask st = new SaveTask();
                    st.execute();


                } else {
                    if (subCategoryDTOList.get(pos).getAddtocart().equalsIgnoreCase("1")) {

                        for (int i = 0; i < quayoBasketDTOList.size(); i++) {
                            if (subCategoryDTOList.get(pos).getI_name().equalsIgnoreCase(quayoBasketDTOList.get(i).getProductName())) {
                                deleteupdatepos = i;
                            }
                        }

                        QuayoBasketDTO task = quayoBasketDTOList.get(deleteupdatepos);

                        updateTask(task);
                    } else {
                        SaveTask st = new SaveTask();
                        st.execute();
                    }

                }

            }



    }


    private void minusMethod()
    {

            quantity = subCategoryDTOList.get(pos).getQuantity();

            if (quantity > 1) {


             //   Toast.makeText(mcontex, quantity+"", Toast.LENGTH_SHORT).show();
                quantity = quantity - 1;
                count = count -1;

                sessionManager.setCartSize(String.valueOf(count));
                for (int i = 0; i < quayoBasketDTOList.size(); i++)
                {
                    if (subCategoryDTOList.get(pos).getI_name().equalsIgnoreCase(quayoBasketDTOList.get(i).getProductName()))
                    {
                        deleteupdatepos = i;
                    }
                }
                QuayoBasketDTO task = quayoBasketDTOList.get(deleteupdatepos);
                updateTask(task);


            }
            else if (quantity==1){

                quantity = subCategoryDTOList.get(pos).getQuantity();
                quantity = quantity - 1;
                count = count -1;

                sessionManager.setCartSize(String.valueOf(count));
                for (int i = 0; i < quayoBasketDTOList.size(); i++) {
                    if (subCategoryDTOList.get(pos).getI_name().equalsIgnoreCase(quayoBasketDTOList.get(i).getProductName())) {
                        deleteupdatepos = i;
                    }
                }
                QuayoBasketDTO task = quayoBasketDTOList.get(deleteupdatepos);
                deleteTask(task);
            }


    }


    private void showCartCount(int size)
    {
        ((Activity) mcontex).runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (size != 0) {

                    cartLL.setVisibility(View.VISIBLE);
                    if (size ==1)
                    {
                        countTV.setText(String.valueOf(size) + " ITEM");

                    }
                    else
                    {
                        countTV.setText(String.valueOf(size) + " ITEMS");

                    }
                    addtocartBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
                    HomeActivity.getInstance().runThread(size);
                    OrderFragment.getInstance().runThread("getTasks");

                    // HomeActivity.getInstance().showBadge();
                } else {
                    cartLL.setVisibility(View.GONE);
                    HomeActivity.getInstance().runThread(size);
                    OrderFragment.getInstance().runThread( "getTasks");


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return subCategoryDTOList != null ? subCategoryDTOList.size() : 0;
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    subCategoryDTOList = subCategoryDTOListFiltered;
                } else {
                    ArrayList<SubCategoryDTO> filteredList = new ArrayList<>();
                    for (SubCategoryDTO row : subCategoryDTOListFiltered) {

                        if ((row.getI_name()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }

                    }

                    subCategoryDTOList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = subCategoryDTOList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                subCategoryDTOList = (ArrayList<SubCategoryDTO>) filterResults.values;

                if (subCategoryDTOList.isEmpty()) {
                    //norecordfoundTV.setVisibility(View.GONE);
                    notifyDataSetChanged();

                } else {
                    //norecordfoundTV.setVisibility(View.VISIBLE);
                    notifyDataSetChanged();
                }

            }
        };
    }


    public class ViewHolderPollAdapter extends RecyclerView.ViewHolder {


        ItemSubcategoryBinding itemRowBinding;

        public ViewHolderPollAdapter(ItemSubcategoryBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemRowBinding = itemRowBinding;
        }


    }


    private void deleteTask(final QuayoBasketDTO task)
    {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(mcontex.getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .delete(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
               // Toast.makeText(mcontex, "deleted", Toast.LENGTH_SHORT).show();

                subCategoryDTOList.get(pos).setAddtocart("0");
                subCategoryDTOList.get(pos).setQuantity(0);
                notifyItemChanged(pos);
                notifyDataSetChanged();
                //getTasks();
                new SqliteDbMethod().getTasks(mcontex,"sabcategadapter");
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }


    class SaveTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            QuayoBasketDTO task = new QuayoBasketDTO();
            task.setProductName(subCategoryDTOList.get(pos).getI_name());
            task.setProductMRP(subCategoryDTOList.get(pos).getRate());
            task.setProductId(subCategoryDTOList.get(pos).getItem_id());
            task.setProductImage(subCategoryDTOList.get(pos).getItem_Image());
            task.setDescription(subCategoryDTOList.get(pos).getItem_Descp());
            task.setIsvegnonveg(subCategoryDTOList.get(pos).getIsvegnong());
            task.setAmountplustax(subCategoryDTOList.get(pos).getAmountplustax());
            task.setTaxpercent(subCategoryDTOList.get(pos).getTaxpercent());
            task.setTaxamount(subCategoryDTOList.get(pos).getTaxamount());
            task.setStrikeprice(subCategoryDTOList.get(pos).getStrikeprice());
            task.setIsDiscount(subCategoryDTOList.get(pos).getIsDiscount());


            task.setQuantity(quantity);
            task.setAddtocart("1");

//            if (sessionManager.isLoggedIn()) {
//                task.setCustomerId(sessionManager.getLoginData().get(SessionManager.KEY_ID));
//            }


            DatabaseClient.getInstance(mcontex.getApplicationContext()).getAppDatabase()
                    .taskDao()
                    .insert(task);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

               // Toast.makeText(mcontex, "saved", Toast.LENGTH_SHORT).show();
                subCategoryDTOList.get(pos).setAddtocart("1");
                subCategoryDTOList.get(pos).setQuantity(quantity);
                notifyItemChanged(pos);
                notifyDataSetChanged();
                new SqliteDbMethod().getTasks(mcontex,"sabcategadapter");


            } catch (Exception e) {
            }
        }
    }

    private void updateTask(final QuayoBasketDTO task)
    {
        class UpdateTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                task.setProductName(subCategoryDTOList.get(pos).getI_name());
                task.setProductMRP(subCategoryDTOList.get(pos).getRate());
                task.setProductId(subCategoryDTOList.get(pos).getItem_id());
                task.setProductImage(subCategoryDTOList.get(pos).getItem_Image());
                task.setDescription(subCategoryDTOList.get(pos).getItem_Descp());
                task.setIsvegnonveg(subCategoryDTOList.get(pos).getIsvegnong());
                task.setAmountplustax(subCategoryDTOList.get(pos).getAmountplustax());
                task.setTaxpercent(subCategoryDTOList.get(pos).getTaxpercent());
                task.setTaxamount(subCategoryDTOList.get(pos).getTaxamount());
                task.setStrikeprice(subCategoryDTOList.get(pos).getStrikeprice());
                task.setIsDiscount(subCategoryDTOList.get(pos).getIsDiscount());


                task.setQuantity(quantity);
                task.setAddtocart("1");


                DatabaseClient.getInstance(mcontex).getAppDatabase()
                        .taskDao()
                        .update(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try {
                 //   Toast.makeText(mcontex, "updated", Toast.LENGTH_SHORT).show();
                    subCategoryDTOList.get(pos).setQuantity(quantity);

                    notifyItemChanged(pos);
                    notifyDataSetChanged();
                    new SqliteDbMethod().getTasks(mcontex,"sabcategadapter");


                } catch (Exception e) {
                }
            }
        }

        UpdateTask gt = new UpdateTask();
        gt.execute();
    }


//    private void getTasks() {
//        class GetTasks extends AsyncTask<Void, Void, List<KutumbDTO>> {
//
//            @Override
//            protected List<KutumbDTO> doInBackground(Void... voids) {
//                kutumbDTOList = DatabaseClient
//                        .getInstance(mcontex.getApplicationContext())
//                        .getAppDatabase()
//                        .taskDao()
//                        .getAllProduct();
//
//
//
//
//
//                return kutumbDTOList;
//            }
//
//            @Override
//            protected void onPostExecute(List<KutumbDTO> tasks) {
//                super.onPostExecute(tasks);
//
//                showCartCount();
//            }
//        }
//
//        GetTasks gt = new GetTasks();
//        gt.execute();
//    }

    public void runThread(List<QuayoBasketDTO> quayoBasketList)
    {
        new Thread() {
            public void run() {
                try {
                    ((Activity)mcontex).runOnUiThread(new Runnable()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            quayoBasketDTOList=quayoBasketList;
                            showCartCount(quayoBasketDTOList.size());
                                          }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

}