package com.vince.empire.mbasera;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.navhistory.adapter.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VinceGee on 05/30/2016.
 */
public class AppConfig {

    public static final String BASE_URL = "http://192.168.100.13/mbasera";
    // Server user login url
    public static String URL_LOGIN = BASE_URL + "/login.php";

    // Server user register url
    public static String URL_REGISTER = BASE_URL + "/register.php";

    public static final String URL_SEARCH = BASE_URL + "/search.php";

    // PayPal server urls
    public static final String URL_PRODUCTS = BASE_URL + "/v1/products";

    public static final String URL_VERIFY_PAYMENT = BASE_URL + "/v1/verifyPayment";

    // PayPal app configuration
    public static final String PAYPAL_CLIENT_ID = "AcH6Jak-xJ5mPm-x02qczkG_8nK3F-DHjeDEkZwH36i5GnkTX4qxTcsFz5hfw_ZLzYll51reFeLYTaCv";
    public static final String PAYPAL_CLIENT_SECRET = "EGu54LWzbTxtKyBBcviVrJNjZ0PCIX1Pnb9Xw4ylS1kbblvgjqGiC50tKhP2lucKpYlkFf7wmTEC8sYF";

    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "USD";
    public static int CARTITEMSQUANTITY = 0;
    public static double CARTITEMSPRICE = 0.00;
   public static List<Product> catalogueProductsInCart = new ArrayList<Product>();
    public static ArrayList<Order> productsOrderList = new ArrayList<>();
    //product for items in cart
    //public static List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();
    public static List<Product> productsInCart = new ArrayList<Product>();
}
