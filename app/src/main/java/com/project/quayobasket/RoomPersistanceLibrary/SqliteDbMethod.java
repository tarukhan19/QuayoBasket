package com.project.quayobasket.RoomPersistanceLibrary;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.project.quayobasket.Activity.HomeActivity;
import com.project.quayobasket.Adapter.SubCategoryAdapter;
import com.project.quayobasket.DialogFragment.SubCategoriesItemFragment;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.Interface.SetCartInterface;
import com.project.quayobasket.Payment.PaymentActivity;
import com.project.quayobasket.Payment.RazorPayActivity;

import java.util.List;

public class SqliteDbMethod implements SetCartInterface
{

 List<QuayoBasketDTO> kutumbDTOList;

    private void deleteTask(final Context context)
    {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(context).getAppDatabase()
                        .taskDao()
                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //new CartActivity().getVisiblity(taskList);

            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }
//1st

    public void getTasks(Context context,String from) {
        class GetTasks extends AsyncTask<Void, Void, List<QuayoBasketDTO>> {

            @Override
            protected List<QuayoBasketDTO> doInBackground(Void... voids) {
                kutumbDTOList = DatabaseClient
                        .getInstance(context.getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAllProduct();

                return kutumbDTOList;
            }

            @Override
            protected void onPostExecute(List<QuayoBasketDTO> tasks) {
                super.onPostExecute(tasks);
                Log.e("fromm",from);
                if (from.equalsIgnoreCase("payment"))
                {
                    RazorPayActivity.getInstance().runThread(kutumbDTOList);
                }
                else if (from.equalsIgnoreCase("subcategoryitem"))
                {
                    SubCategoriesItemFragment.getInstance().runThread(kutumbDTOList);
                }
                else if (from.equalsIgnoreCase("sabcategadapter"))
                {
                    SubCategoryAdapter.getInstance().runThread(kutumbDTOList);
                }
                else if (from.equalsIgnoreCase("order"))
                {
                    OrderFragment.getInstance().runThread(kutumbDTOList);
                }
                else if (from.equalsIgnoreCase("home"))
                {
                    HomeActivity.getInstance().runThread(kutumbDTOList.size());
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }



}
