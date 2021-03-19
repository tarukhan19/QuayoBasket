package com.project.quayobasket.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.project.quayobasket.DialogFragment.AddressDialogFragment;
import com.project.quayobasket.Fragments.HomeFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.session.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapMethods {

    double lng, lat;
    String address;
    int PERMISSION_ID = 44;
    Context mctx;
    HomeFragment homeFragment;
    SessionManager sessionManager;
    FusedLocationProviderClient mFusedLocationClient;
    RequestQueue requestQueue;
    String from;
    DistanceCalcMethods distanceCalcMethods;
    public MapMethods(Context mctx, HomeFragment homeFragment,String from) {
        this.mctx = mctx;
        this.homeFragment=homeFragment;
        this.from=from;
        initialize();

    }

    public MapMethods(Context mctx, String from) {
        this.mctx = mctx;
        this.from=from;
        initialize();
    }


    private void initialize() {
        requestQueue = Volley.newRequestQueue(mctx);
        sessionManager = new SessionManager(mctx);
        distanceCalcMethods=new DistanceCalcMethods();
        if (!Places.isInitialized()) {
            Places.initialize(mctx.getApplicationContext(), mctx.getString(R.string.api_key));
        }
        PlacesClient placesClient = Places.createClient(mctx);

        // Create a new Places client instance.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mctx);

        if (ActivityCompat.checkSelfPermission(mctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mctx, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            // Write you code here if permission already given.
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                       requestNewLocationData();
                                } else {
                                    lat = location.getLatitude();
                                    lng = location.getLongitude();

                                    Log.e("latlng",lat+"  "+lng);


                                    getAdress();


                                }
                            }
                        }
                );
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mctx.startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mctx);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat=mLastLocation.getLatitude();
            lng=mLastLocation.getLongitude();
            Log.e("lat",lat+"");
            Log.e("lng",lng+"");
            LatLng latlng = new LatLng(lat, lng);

            getAdress();
            if (from.equalsIgnoreCase("activity")  || from.equalsIgnoreCase("apimethod"))
            {
                homeFragment.runThread();

            }
            else
            {
                AddressDialogFragment.getInstance().runThread(latlng,address);

            }
        }
    };


    public void getLatLong(final String youraddress) {
        Log.e("youraddress", youraddress);

        String uri = "https://maps.google.com/maps/api/geocode/json?key="+mctx.getString(R.string.api_key)+"&address=" + youraddress + "&sensor=true";
        uri = uri.replaceAll(" ", "%20");
        Log.e("uri", uri);

        StringRequest postRequest = new StringRequest(Request.Method.GET, uri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", response);


                        try {
                            //Log.e("response", response);
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));

                            lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");


                            lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");

                            LatLng latlng = new LatLng(lat, lng);
                             // getAdress();

                            if (from.equalsIgnoreCase("activity"))
                            {
                                homeFragment.runThread();

                            }
                            else
                            {
                                AddressDialogFragment.getInstance().runThread(latlng,youraddress);

                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {

        };
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        requestQueue.add(postRequest);
    }




    public void getAdress() {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(mctx, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            Log.e("latlng",address);
            LatLng latLng=new LatLng(lat, lng);

            sessionManager.setAddressData(address, String.valueOf(lat), String.valueOf(lng));
            double lat1= Double.parseDouble(sessionManager.getAddressData().get(SessionManager.KEY_LATITUDE));
            double lng1= Double.parseDouble(sessionManager.getAddressData().get(SessionManager.KEY_LONGITUDE));
           //tarukhan19@gmail.com distanceCalcMethods.getDistance(lat1,lng1, Constant.Lat,Constant.Lng,mctx);
//            Log.e("distance",dis+"");
//            sessionManager.setLatLngDistance(String.valueOf(dis));

            if (from.equalsIgnoreCase("activity"))
            {
                homeFragment.runThread();

            }
            else
            {
                AddressDialogFragment.getInstance().runThread(latLng,address);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(mctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void requestPermissions() {
        ActivityCompat.requestPermissions(
                (Activity) mctx,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mctx.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }





}
