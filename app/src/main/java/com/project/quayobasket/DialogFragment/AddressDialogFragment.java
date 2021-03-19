package com.project.quayobasket.DialogFragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.quayobasket.Fragments.HomeFragment;
import com.project.quayobasket.R;
import com.project.quayobasket.Utils.DistanceCalcMethods;
import com.project.quayobasket.Utils.MapMethods;
import com.project.quayobasket.databinding.ItemAdressBottomlayoutBinding;
import com.project.quayobasket.session.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class AddressDialogFragment extends BottomSheetDialogFragment
        implements View.OnClickListener, OnMapReadyCallback {

  public static final String TAG = "ActionBottomDialog";

  private ItemClickListener mListener;
  ItemAdressBottomlayoutBinding binding;
  int PERMISSION_ID = 44;
  RequestQueue requestQueue;
  SessionManager sessionManager;
  private GoogleMap googleMap;
  SupportMapFragment supportMapFragment;
  ProgressDialog progressDialog;
  MapMethods mapMethods;
  AutocompleteSupportFragment autocompleteFragment;
  static AddressDialogFragment addressDialogFragment;
  DistanceCalcMethods distanceCalcMethods;
  public static AddressDialogFragment newInstance() {
    return new AddressDialogFragment();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

    binding = DataBindingUtil.inflate(inflater, R.layout.item_adress_bottomlayout, container, false);
    View view = binding.getRoot();

    initialize();

    binding.crossIV.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
      @Override
      public void onPlaceSelected(@NotNull Place place)
      {

        String address=place.getAddress();
        mapMethods.getLatLong(address);
        autocompleteFragment.setText(address);
      }
      @Override
      public void onError(@NotNull Status status) {
        // TODO: Handle the error.
        Log.i(TAG, "An error occurred: " + status);
      }
    });










    return  view;
  }

  private void initialize()
  {

    FragmentManager fm = getChildFragmentManager();/// getChildFragmentManager();
    supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.location_map);
    if (supportMapFragment == null) {
      supportMapFragment = SupportMapFragment.newInstance();
      fm.beginTransaction().replace(R.id.location_map, supportMapFragment).commit();
    }
    supportMapFragment.getMapAsync(this);
    distanceCalcMethods=new DistanceCalcMethods();
    requestQueue = Volley.newRequestQueue(getActivity());
    sessionManager = new SessionManager(getActivity().getApplicationContext());
    progressDialog = new ProgressDialog(getActivity());

    // Initialize the AutocompleteSupportFragment.
    autocompleteFragment = (AutocompleteSupportFragment)
            getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

    // Specify the types of place data to return.
    autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
            , Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.PHONE_NUMBER));
//   autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
    //  autocompleteFragment.getView().findViewById(R.id.place_autocomplete_clear_button).setVisibility(View.GONE);

    mapMethods=new MapMethods(getActivity(),"fragment");
    addressDialogFragment=this;
    // Set up a PlaceSelectionListener to handle the response.
  }


  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // binding.autocompleteFragment.
  }

  @Override
  public void onAttach(Context context)
  {
    super.onAttach(context);
    if (context instanceof ItemClickListener) {
      mListener = (ItemClickListener) context;
    } else {
      throw new RuntimeException(context.toString()
              + " must implement ItemClickListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onClick(View view) {
    TextView tvSelected = (TextView) view;
    mListener.onItemClick(tvSelected.getText().toString());
    dismiss();
  }

  public interface ItemClickListener {
    void onItemClick(String item);
  }

  @Override
  public void onMapReady(GoogleMap gMap) {
    googleMap = gMap;
    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    gMap.setMyLocationEnabled(true);

  }


  public static AddressDialogFragment getInstance() {
    return addressDialogFragment;
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == PERMISSION_ID) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mapMethods.getLastLocation();
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    if (mapMethods.checkPermissions()) {
      mapMethods.getLastLocation();
    }

  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }

  public void runThread(LatLng latlng, String address)
  {
    new Thread() {
      public void run() {
        try {
          getActivity().runOnUiThread(new Runnable()
          {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
              try {
                showPin( latlng,  address);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          });
          Thread.sleep(300);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }
    }.start();
  }

  public void showPin(LatLng latlng, String address)  throws Exception
  {
    googleMap.clear();
    googleMap.addMarker(new MarkerOptions().position(latlng).title(address));
    CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(12).build();
    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    double lat=latlng.latitude;
    double lng=latlng.longitude;

    sessionManager.setAddressData(address,String.valueOf(lat),String.valueOf(lng));
    double lat1= Double.parseDouble(sessionManager.getAddressData().get(SessionManager.KEY_LATITUDE));
    double lng1= Double.parseDouble(sessionManager.getAddressData().get(SessionManager.KEY_LONGITUDE));
   // distanceCalcMethods.getDistance(lat1,lng1, Constant.Lat,Constant.Lng,getActivity());
//    Log.e("distance",dis+"");
//    sessionManager.setLatLngDistance(String.valueOf(dis));

    HomeFragment.getInstance().runThread();

     SubmitAddressFragment.getInstance().runThread();



  }


}