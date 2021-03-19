package com.project.quayobasket.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.project.quayobasket.DialogFragment.AddressDialogFragment;
import com.project.quayobasket.Fragments.HomeFragment;
import com.project.quayobasket.Fragments.LoginWithOtpFragment;
import com.project.quayobasket.Fragments.OrderFragment;
import com.project.quayobasket.Fragments.ProfileFragment;
import com.project.quayobasket.Fragments.SearchFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.RoomPersistanceLibrary.SqliteDbMethod;
import com.project.quayobasket.Utils.ApiMethods;
import com.project.quayobasket.Utils.AppCurrentVersions;
import com.project.quayobasket.Utils.MapMethods;
import com.project.quayobasket.Utils.MyApplication;
import com.project.quayobasket.databinding.ActivityHomeBinding;
import com.project.quayobasket.session.SessionManager;


public class HomeActivity extends AppCompatActivity implements AddressDialogFragment.ItemClickListener {
    ActivityHomeBinding binding;
    SessionManager sessionManager;
    private static final int UPDATE_REQUEST_CODE = 111;

    TextView mTitle;
    private HomeFragment homeFragment = new HomeFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private LoginWithOtpFragment loginFragment = new LoginWithOtpFragment();
    private OrderFragment orderFragment = new OrderFragment();
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment activeFragment;
    ImageView backIV, logout;
    MapMethods mapMethods;
    int PERMISSION_ID = 44;

    BadgeDrawable badge;

    static HomeActivity homeActivity;
    AppUpdateManager appUpdateManager;
    ApiMethods apiMethods=new ApiMethods();

    String appversion;
    AppCurrentVersions appCurrentVersions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
         initialize();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogOutDialog();
            }
        });


        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeFragment == homeFragment) {


                    finish();

                } else {
                    MenuItem homeItem = binding.navigation.getMenu().getItem(0);
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                    activeFragment = homeFragment;
                    binding.navigation.setSelectedItemId(homeItem.getItemId());
                    mTitle.setText("Home");
                }
            }
        });
    }


    public static HomeActivity getInstance() {
        return homeActivity;
    }


    private void initialize()
    {
        sessionManager = new SessionManager(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        homeActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        backIV = toolbar.findViewById(R.id.plusimage);
        logout = toolbar.findViewById(R.id.logoutImage);
        appCurrentVersions=new AppCurrentVersions();

        fragmentManager.beginTransaction().add(R.id.container, homeFragment, "USER").hide(homeFragment)
                .add(R.id.container, searchFragment, "SEARCH").hide(searchFragment)
                .add(R.id.container, orderFragment, "ORDER").hide(orderFragment)
                .add(R.id.container, profileFragment, "PROFILE").hide(profileFragment)
                .add(R.id.container, loginFragment, "LOGIN").hide(loginFragment)

                .commit();

        if (getIntent().hasExtra("for")) {
            if (getIntent().getStringExtra("for").equalsIgnoreCase("login")) {
                mTitle.setText("Login");

                activeFragment = loginFragment;
                MenuItem homeItem = binding.navigation.getMenu().getItem(2);
                binding.navigation.setSelectedItemId(homeItem.getItemId());
                fragmentManager.beginTransaction().hide(activeFragment).show(loginFragment).commit();

            } else if (getIntent().getStringExtra("for").equalsIgnoreCase("order"))
            {
                mTitle.setText("Order");
                activeFragment = orderFragment;
                MenuItem homeItem = binding.navigation.getMenu().getItem(1);
                binding.navigation.setSelectedItemId(homeItem.getItemId());
                fragmentManager.beginTransaction().hide(activeFragment).show(orderFragment).commit();

            } else if (getIntent().getStringExtra("for").equalsIgnoreCase("profile"))
            {
                mTitle.setText("Profile");
                activeFragment = profileFragment;
                MenuItem homeItem = binding.navigation.getMenu().getItem(2);
                binding.navigation.setSelectedItemId(homeItem.getItemId());
                fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit();

            }


            else {
                mTitle.setText("Home");

                activeFragment = homeFragment;
                MenuItem homeItem = binding.navigation.getMenu().getItem(0);
                binding.navigation.setSelectedItemId(homeItem.getItemId());
                fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();

            }

        } else {
            mTitle.setText("Home");

            activeFragment = homeFragment;
            MenuItem homeItem = binding.navigation.getMenu().getItem(0);
            binding.navigation.setSelectedItemId(homeItem.getItemId());
            fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();

        }


        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);





        badge = binding.navigation.getOrCreateBadge(R.id.navigation_order);

        new SqliteDbMethod().getTasks(this,"home");

        mapMethods=new MapMethods(this,homeFragment,"activity");

        if (sessionManager.isLoggedIn())
        {
            apiMethods.loadAddress(this,"home");
        }

        hideItem();

        appversion=appCurrentVersions.getCurrentVersion(this);


        inAppUpdate();


    }




    public void hideItem() {

        if (!sessionManager.isLoggedIn()) {
            binding.navigation.getMenu().removeItem(R.id.navigation_profile);
            logout.setVisibility(View.GONE);
        } else {
            binding.navigation.getMenu().removeItem(R.id.navigation_login);
            logout.setVisibility(View.VISIBLE);

        }

    }
    public void runShowBottomsheetThread() {

        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {

                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                        showBottomSheet();
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }


    public void runThread(final int notificationCount)
    {
        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable()
                    {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            showBadge((notificationCount));
                        }
                    });
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void showBadge(int quantity)
    {

        if (quantity > 0) {
            badge.setVisible(true);
            badge.setNumber(quantity);

        } else {
            badge.setVisible(false);

        }
    }

    private void showLogOutDialog()
    {
        final Dialog dialog = new Dialog(this, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_logout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        TextView descriptionTV = dialog.findViewById(R.id.descriptionTV);
        TextView titleTV = dialog.findViewById(R.id.titleTV);
        ImageView imageView = dialog.findViewById(R.id.imageView);
        Button yesBTN = dialog.findViewById(R.id.yesbtn);
        Button noBtn = dialog.findViewById(R.id.nobtn);

        yesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
             //   deleteTask("logout");
                sessionManager.logoutUser();

                Intent in7 = new Intent(HomeActivity.this, HomeActivity.class);
                in7.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in7);
                overridePendingTransition(R.anim.trans_left_in,
                        R.anim.trans_left_out);


            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
    }

    @Override
    public void onBackPressed()
    {

        if (activeFragment == homeFragment) {

            finish();

        } else {
            fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
            activeFragment = homeFragment;
            MenuItem homeItem = binding.navigation.getMenu().getItem(0);
            binding.navigation.setSelectedItemId(homeItem.getItemId());
            mTitle.setText("Home");

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentManager.beginTransaction().hide(activeFragment).show(homeFragment).commit();
                    activeFragment = homeFragment;
                    mTitle.setText("Home");
                    return true;
                case R.id.navigation_order:
                    fragmentManager.beginTransaction().hide(activeFragment).show(orderFragment).commit();
                    activeFragment = orderFragment;
                    mTitle.setText("Order");
                    return true;
                case R.id.navigation_login:
                    fragmentManager.beginTransaction().hide(activeFragment).show(loginFragment).commit();
                    activeFragment = loginFragment;
                    mTitle.setText("Login");

                    return true;

                case R.id.navigation_profile:
                    fragmentManager.beginTransaction().hide(activeFragment).show(profileFragment).commit();
                    activeFragment = profileFragment;
                    mTitle.setText("Profile");

                    return true;

            }
            return false;
        }
    };


    @Override
    public void onResume()
    {
        super.onResume();
        MyApplication.activityResumed();

        if (mapMethods.checkPermissions()) {
            try {
                if (sessionManager.getAddressData().get(SessionManager.KEY_CURRENTADDRESS).isEmpty())
                {
                    mapMethods.getLastLocation();

                }
                else
                {
                    HomeFragment.getInstance().runThread();
                }

            }
            catch (Exception e)
            {}
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               try {
                   if (sessionManager.getAddressData().get(SessionManager.KEY_CURRENTADDRESS).isEmpty())
                   {
                       mapMethods.getLastLocation();

                   }
                   else
                   {
                       HomeFragment.getInstance().runThread();
                   }
               }
               catch (Exception e)
               {}
            }
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
       // initialize();
    }


    public void showBottomSheet() {
                AddressDialogFragment addPhotoBottomDialogFragment =
                AddressDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                AddressDialogFragment.TAG);
    }



    private void inAppUpdate()
    {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new com.google.android.play.core.tasks.OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                Log.e("AVAILABLE_VERSION_CODE", appUpdateInfo.availableVersionCode()+"");
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // Request the update.

                    try {
                        //deleteTask("update");
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                HomeActivity.this,
                                // Include a request code to later monitor this update request.
                                UPDATE_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException ignored) {

                    }
                }
            }
        });

        appUpdateManager.registerListener(installStateUpdatedListener);

    }
    //lambda operation used for below listener
    InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                HomeActivity.this.popupSnackbarForCompleteUpdate();
            } else
                Log.e("UPDATE", "Not downloaded yet");
        }
    };
    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "Update almost finished!",
                        Snackbar.LENGTH_INDEFINITE);
        //lambda operation used for below action
        snackbar.setAction(this.getString(R.string.restart), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public void onItemClick(String item) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }



}