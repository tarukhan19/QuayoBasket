package com.project.quayobasket.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.project.quayobasket.R;
import com.project.quayobasket.session.SessionManager;

public class SliderActivity extends AppCompatActivity {
    private ViewPager viewPager;
    WelcomeSliderAdapter welcomeSliderAdapter;
    private LinearLayout dotsLayout;
    TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    boolean isdenied=false;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slider);
        session = new SessionManager(getApplicationContext());

        if (Build.VERSION.SDK_INT >= 21)
        {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
//                            return;
//                        }
//
//
//                        String token = task.getResult();
//                        session.setFirebaseRegId(token);
//
//                        Log.e("TAG", "Fetching FCM registration token>>>  "+ token);
//
//                        // Log and toast
//                        //String msg = getString(R.string.msg_token_fmt, token);
//
//
//                    }
//                });



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkPermission();
        }
        viewPager =  findViewById(R.id.view_pager);
        dotsLayout =  findViewById(R.id.layoutDots);

        btnSkip =  findViewById(R.id.btn_skip);
        btnNext =  findViewById(R.id.btn_next);

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3};

        addBottomDots(0);


        welcomeSliderAdapter = new WelcomeSliderAdapter();
        viewPager.setAdapter(welcomeSliderAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(v -> launchHomeScreen());
        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }
    private void launchHomeScreen() {
        startActivity(new Intent(SliderActivity.this, HomeActivity.class));
        finish();
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class WelcomeSliderAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        WelcomeSliderAdapter() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            assert layoutInflater != null;
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }



    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(SliderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                SliderActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
//                + ContextCompat.checkSelfPermission(
//                LoginActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                + ContextCompat.checkSelfPermission(
//                LoginActivity.this,Manifest.permission.READ_SMS)
//                + ContextCompat.checkSelfPermission(
//                LoginActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
//                + ContextCompat.checkSelfPermission(
//                LoginActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
//                + ContextCompat.checkSelfPermission(
//                LoginActivity.this,Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    SliderActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    SliderActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                    LoginActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                    LoginActivity.this,Manifest.permission.READ_SMS)
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                    LoginActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                    LoginActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
//                    || ActivityCompat.shouldShowRequestPermissionRationale(
//                    LoginActivity.this,Manifest.permission.READ_CONTACTS)



            ){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(SliderActivity.this);
//                builder.setMessage("Camera, Read Contacts and Write External" +
//                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                SliderActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                        Manifest.permission.READ_SMS,
//                                        Manifest.permission.ACCESS_FINE_LOCATION,
//                                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                                        Manifest.permission.READ_CONTACTS
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                        isdenied=true;
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        SliderActivity.this,
                        new String[]{

                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,

                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }else {
            // Do something, when permissions are already granted
            //  Toast.makeText(LoginActivity.this,"Permissions already granted",Toast.LENGTH_SHORT).show();

//            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                // When request is cancelled, the results array are empty
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ){
                    // Permissions are granted
                    //   Toast.makeText(LoginActivity.this,"Permissions granted.",Toast.LENGTH_SHORT).show();
                   // getLastLocation();
                }else {
                    // Permissions are denied
                    if (isdenied)
                    {
                        openPermissionSettings(SliderActivity.this);
                    }
                    //   Toast.makeText(LoginActivity.this,"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}