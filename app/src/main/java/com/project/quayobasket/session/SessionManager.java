package com.project.quayobasket.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Shared pref file name
    private static final String PREF_NAME = "salesformPref";
    public static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "ID";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_MOBILENO = "mobileno";
    public static final String KEY_F_NAME = "fname";
    public static final String KEY_FIREBASE_REGID = "FREGID";

    public static final String KEY_RESPONSE = "response";
    public static final String KEY_CATEGORY_ID = "categoryid";
    public static final String KEY_CATEGORY_DEPARTMENT = "categDEPARTMENT";
    public static final String KEY_CART_SIZE = "cartsize";

    public static final String KEY_CURRENTADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";


    public static final String KEY_ADDRESS_DELIVERY = "ADDRESSDELIVERY";
    public static final String KEY_PAYMENTAMOUNT = "KEY_PAYMENTAMOUNT";
    public static final String KEY_LATLONG_DISTANCE = "KEY_LATLONG_DISTANCE";
    public static final String KEY_ADDRESS_DELIVERY_ID = "ADDRESSDELIVERYID";
    public static final String KEY_ADDRESS_LINE1 = "ADRESSLINE1";
    public static final String KEY_ADDRESS_LINE2 = "ADRESSLINE2";
    public static final String KEY_ADDRESS_PINCODE = "pincode";

    public static final String KEY_ORDERID= "orderid";
    public static final String KEY_APP_CURRENT_VERSION= "appcurrentversion";

    public static final String KEY_TOTALRATE_FOROFFERS = "KEY_TOTALRATE_FOROFFERS";


    public HashMap<String, String> getTotalRateForOffers() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_TOTALRATE_FOROFFERS, pref.getString(KEY_TOTALRATE_FOROFFERS, ""));
        return user;
    }


    public void setTotalRateForOffers(String rate)
    {
        editor.putString(KEY_TOTALRATE_FOROFFERS, rate);
        editor.commit();
    }

    // Constructor
    public SessionManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public HashMap<String, String> getAppCurrentVersion() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_APP_CURRENT_VERSION, pref.getString(KEY_APP_CURRENT_VERSION, "0"));
        return user;
    }


    public void setAppCurrentVersion(String appCurrentVersion)
    {
        editor.putString(KEY_APP_CURRENT_VERSION, appCurrentVersion);
        editor.commit();
    }

    public HashMap<String, String> getLatLngDistance() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_LATLONG_DISTANCE, pref.getString(KEY_LATLONG_DISTANCE, ""));
        return user;
    }


    public void setLatLngDistance(String distance)
    {
        editor.putString(KEY_LATLONG_DISTANCE, distance);
        editor.commit();
    }

    public HashMap<String, String> getFirebaseRegId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_FIREBASE_REGID, pref.getString(KEY_FIREBASE_REGID, ""));
        return user;
    }


    public void setFirebaseRegId(String supportValidation) {
        editor.putString(KEY_FIREBASE_REGID, supportValidation);
        editor.commit();
    }

    public HashMap<String, String> getOrderId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_ORDERID, pref.getString(KEY_ORDERID, ""));
        return user;
    }


    public void setOrderId(String orderId) {
        editor.putString(KEY_ORDERID, orderId);
        editor.commit();
    }

    public HashMap<String, String> getPaymentAmount() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_PAYMENTAMOUNT, pref.getString(KEY_PAYMENTAMOUNT, ""));

        return user;
    }


    public void setPaymentAmount(String amount)
    {
        editor.putString(KEY_PAYMENTAMOUNT, amount);

        editor.commit();
    }

    public HashMap<String, String> getDeliveryAddress() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_ADDRESS_DELIVERY, pref.getString(KEY_ADDRESS_DELIVERY, ""));
        user.put(KEY_ADDRESS_DELIVERY_ID, pref.getString(KEY_ADDRESS_DELIVERY_ID, ""));
        user.put(KEY_ADDRESS_LINE1, pref.getString(KEY_ADDRESS_LINE1, ""));
        user.put(KEY_ADDRESS_LINE2, pref.getString(KEY_ADDRESS_LINE2, ""));
        user.put(KEY_ADDRESS_PINCODE, pref.getString(KEY_ADDRESS_PINCODE, ""));

        return user;
    }


    public void setDeliveryAddress(String deliveryaddressid,String deliveryaddress,String addressline1,String addressline2,String pincode)
    {
        editor.putString(KEY_ADDRESS_DELIVERY, deliveryaddress);
        editor.putString(KEY_ADDRESS_DELIVERY_ID, deliveryaddressid);
        editor.putString(KEY_ADDRESS_LINE1, addressline1);
        editor.putString(KEY_ADDRESS_LINE2, addressline2);
        editor.putString(KEY_ADDRESS_PINCODE, pincode);

        editor.commit();
    }

    public HashMap<String, String> getAddressData() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_CURRENTADDRESS, pref.getString(KEY_CURRENTADDRESS, ""));
        user.put(KEY_LATITUDE, pref.getString(KEY_LATITUDE, ""));
        user.put(KEY_LONGITUDE, pref.getString(KEY_LONGITUDE, ""));

        return user;
    }


    public void setAddressData(String address,String latitude, String longitude)
    {
        editor.putString(KEY_CURRENTADDRESS, address);
        editor.putString(KEY_LATITUDE, latitude);
        editor.putString(KEY_LONGITUDE, longitude);

        editor.commit();
    }

    public HashMap<String, String> getLoginData() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_ID, pref.getString(KEY_ID, ""));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_MOBILENO, pref.getString(KEY_MOBILENO, ""));
        user.put(KEY_F_NAME, pref.getString(KEY_F_NAME, ""));

        return user;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void setLogin()
    {
        editor.putBoolean(IS_LOGIN,true);

        editor.commit();
    }



    public void setLoginData(String id,String fname,  String email, String phone)
    {
        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_F_NAME, fname);
        editor.putString(KEY_MOBILENO, phone);

        editor.commit();
    }


    public HashMap<String, String> getResponse() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_RESPONSE, pref.getString(KEY_RESPONSE, ""));

        return user;
    }


    public void setResponse(String response) {
        editor.putString(KEY_RESPONSE, response);

        editor.commit();
    }


    public HashMap<String, String> getCategoryId() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_CATEGORY_ID, pref.getString(KEY_CATEGORY_ID, ""));
        user.put(KEY_CATEGORY_DEPARTMENT, pref.getString(KEY_CATEGORY_DEPARTMENT, ""));

        return user;
    }


    public void setCategoryId(String categoryId,String depart) {
        editor.putString(KEY_CATEGORY_ID, categoryId);
        editor.putString(KEY_CATEGORY_DEPARTMENT, depart);

        editor.commit();
    }

    // Clear session details
    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void setCartSize(String size) {
        editor.putString(KEY_CART_SIZE, size);
        editor.commit();
    }

    // Get stored session data
    public HashMap<String, String> getCartSize() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_CART_SIZE, pref.getString(KEY_CART_SIZE, "0"));
        return user;
    }



}