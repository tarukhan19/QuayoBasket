package com.project.quayobasket.Utils;

public class EndPoints {
 //   public static final String CONSTANT = "https://www.kutumb.in.net/app_api/";

 public static final String BASE_URL="https://quayobasket.posgst.com/";


    public static final String GET_USER_PROFILE = BASE_URL+"API/GET_USER_PROFILE.ashx?";
    public static final String GET_BANNER=BASE_URL+"API/GET_BANNERS.ashx?Offer_Type=BANNER";
    public static final String GET_OFFERS_IMAGES=BASE_URL+"API/GET_BANNERS.ashx?Offer_Type=OFFER";
    public static final String GET_CATEGORIES=BASE_URL+"API/GET_CATEGORIES.ashx?DeptId=0";

    public static final String LOAD_CATEGORY_SUBCATEGORY =BASE_URL+ "API/LOAD_CATEGORY_SUBCATEGORY.ashx?";
    public static final String LOAD_BANNER_SUBCATEGORY =BASE_URL+ "API/LOAD_BANNER_SUBCATEGORY.ashx?";
    public static final String LOAD_OFFER_SUBCATEGORY =BASE_URL+ "API/LOAD_OFFER_SUBCATEGORY.ashx?";
    public static final String SEARCH =BASE_URL+ "API/GET_ITEM_NAME_BY_SEARCH.ashx?";

    public static final String LOAD_ABOUTUS = "https://kutuapi.posgst.com/Services.ashx?About_Us=0";
    public static final String LOAD_PRIVACYPOLICY = "https://kutuapi.posgst.com/Services.ashx?Privacy_Policy=0";
    public static final String LOAD_TANDC = "https://kutuapi.posgst.com/Services.ashx?Term_Condition=0";

    public static final String GET_ADDRESS=BASE_URL+"API/GET_ADDRESS.ashx?";
    public static final String GET_ORDER_HISTORY=BASE_URL+"API/GET_ORDER_HISTORY.ashx?";
    public static final String GET_ORDER_HISTORY_DETAILS=BASE_URL+"API/GET_ORDER_HISTORY_DETAILS.ashx?";
    public static final String SAVE_CART=BASE_URL+"API/SAVE_CART.ashx?";
    public static final String GET_OFFERS=BASE_URL+"API/Offers_Select.ashx?";

    public static final String REGISTRATION_URL =BASE_URL+ "API/REGISTRATION.ashx?";
    public static final String LOGIN_URL =BASE_URL+ "API/LOGIN.ashx?";
    public static final String SAVE_ADDRESS =BASE_URL+ "API/Address.ashx?";
    public static final String SEND_OTP =BASE_URL+ "API/SEND_OTP.ashx?";
    public static final String VERIFY_OTP =BASE_URL+ "API/VERIFY_OTP.ashx?";
    public static final String SAVE_PAYMENT =BASE_URL+ "API/SAVE_PAYMENT.ashx?";

}


