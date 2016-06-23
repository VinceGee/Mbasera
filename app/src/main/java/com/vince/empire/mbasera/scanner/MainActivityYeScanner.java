package com.vince.empire.mbasera.scanner;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.login.LoginActivity;
import com.vince.empire.mbasera.login.SQLiteHandler;
import com.vince.empire.mbasera.login.SessionManager;
import com.vince.empire.mbasera.scanner.mainstuff.ProductListAdapterYeScanner;
import com.vince.empire.mbasera.scanner.search.CartFragmentYeScanner;
import com.vince.empire.mbasera.utilities.dialogs.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by VinceGee on 05/31/2016.
 */
public class MainActivityYeScanner extends AppCompatActivity implements ProductListAdapterYeScanner.ProductListAdapterYeScannerListener, View.OnClickListener  {
    private static final String TAG = MainActivityYeScanner.class.getSimpleName();

    private ListView listView;
    private Button btnCheckout;

    // To store all the products
    private List<Product> productsList;

    private int itemQuantity = 1;


    //private ProductListAdapter adapter;

    // Progress dialog
    private ProgressDialog pDialog;

    private static final int REQUEST_CODE_PAYMENT = 1;

    // To store the products those are added to cart
    public static List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();

    /*@Bind(R.id.checkout_fragment_item_details_text_view_item_quantity)
    TextView mTextViewItemQuantity;*/
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(AppConfig.PAYPAL_ENVIRONMENT).clientId(
                    AppConfig.PAYPAL_CLIENT_ID);

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;

    private int statusBarColor;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main_yescanner);
        setupActionBar();
        // setupDrawer();

        setupTabs();

        listView = (ListView) findViewById(R.id.list);
        btnCheckout = (Button) findViewById(R.id.btnCheckout);

        productsList = new ArrayList<Product>();
        // adapter = new ProductListAdapter(this, productsList, this);

        //listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Starting PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        ////////////////////LOGOUT////////////////////////////////////////
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");
    }

    /**
     * Sets up the action bar.
     */
    private void setupActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_redb));
        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        // ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle("Mbasera");
        //ab.setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Sets up the tabs.
     */
    private void setupTabs() {
        // Setup view pager
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        viewpager.setOffscreenPageLimit(MainPagerAdapter.NUM_ITEMS);
        updatePage(viewpager.getCurrentItem());

        // Setup tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                updatePage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    /**
     * Called when the selected page changes.
     *
     * @param selectedPage selected page
     */
    private void updatePage(int selectedPage) {
        //updateFab(selectedPage);
        // updateSnackbar(selectedPage);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, R.string.sheet_item_pressed, Toast.LENGTH_SHORT).show();
        //materialSheetFab.hideSheet();
    }
//////////////////////////////////////////////////////
    /**
     * Verifying the mobile payment on the server to avoid fraudulent payment
     * */
    private void verifyPaymentOnServer(final String paymentId,
                                       final String payment_client) {
        // Showing progress dialog before making request
        pDialog.setMessage("Verifying payment...");
        showpDialog();

        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_PAYMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "verify payment: " + response.toString());

                try {
                    JSONObject res = new JSONObject(response);
                    boolean error = res.getBoolean("error");
                    String message = res.getString("message");

                    // user error boolean flag to check for errors

                    Toast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();

                    if (!error) {
                        // empty the cart
                        productsInCart.clear();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // hiding the progress dialog
                hidepDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Verify Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hiding the progress dialog
                hidepDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("paymentId", paymentId);
                params.put("paymentClientJson", payment_client);

                return params;
            }
        };

        // Setting timeout to volley request as verification request takes sometime
        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        verifyReq.setRetryPolicy(policy);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(verifyReq);
    }

    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     * */
    private PayPalPayment prepareFinalCart() {

        PayPalItem[] items = new PayPalItem[productsInCart.size()];
        items = productsInCart.toArray(items);

        // Total amount
        BigDecimal subtotal = PayPalItem.getItemTotal(items);

        // If you have shipping cost, add it here
        BigDecimal shipping = new BigDecimal("0.0");

        // If you have tax, add it here
        BigDecimal tax = new BigDecimal("0.0");

        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(
                shipping, subtotal, tax);

        BigDecimal amount = subtotal.add(shipping).add(tax);

        PayPalPayment payment = new PayPalPayment(
                amount,
                AppConfig.DEFAULT_CURRENCY,
                "Description about transaction. This will be displayed to the user.",
                AppConfig.PAYMENT_INTENT);

        payment.items(items).paymentDetails(paymentDetails);

        // Custom field like invoice_number etc.,
        payment.custom("This is text that will be associated with the payment that the app can use.");

        return payment;
    }

    /**
     * Launching PalPay payment activity to complete the payment
     * */
    private void launchPayPalPayment() {

        PayPalPayment thingsToBuy = prepareFinalCart();

        Intent intent = new Intent(MainActivityYeScanner.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /**
     * Receiving the PalPay payment response
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject()
                                .toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        // Now verify the payment on the server side
                        verifyPaymentOnServer(paymentId, payment_client);

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onAddToCartScanner(final Product product) {


        // custom dialog
        final Dialog dialog = new Dialog(getApplicationContext());
        dialog.setContentView(R.layout.checkout_fragment_item_details);
        dialog.setTitle("Edit Cart");

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // set the custom dialog components - text, image and button
        final TextView mTextViewItemQuantity = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_quantity);
        TextView tvItemName = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_name);
        TextView tvDesc = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_description);
        TextView tvItemPrice = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_price);
        // TextView tvItemPrice = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_price);

        // text.setText("Android custom dialog example!");
        tvItemName.setText(product.getName());
        tvDesc.setText(product.getDescription());
        tvItemPrice.setText("Price: $" + product.getPrice());

        NetworkImageView image = (NetworkImageView) dialog
                .findViewById(R.id.checkout_fragment_item_details_image_view);

        //NetworkImageView mImage = new NetworkImageView(getActivity());
        image.setImageUrl(product.getImage(), imageLoader);

        Button bAdd = (Button) dialog.findViewById(R.id.checkout_fragment_item_details_button_plus);
        bAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                itemQuantity++;
                mTextViewItemQuantity.setText(itemQuantity + "");
            }
        });

        Button bSub = (Button) dialog.findViewById(R.id.checkout_fragment_item_details_button_minus);
        bSub.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                while (itemQuantity > 0) {
                    itemQuantity--;
                }
                mTextViewItemQuantity.setText(itemQuantity + "");
            }
        });

        Button dialogOKButton = (Button) dialog.findViewById(R.id.checkout_fragment_item_details_button_add_to_cart);
        // if button is clicked, close the custom dialog
        dialogOKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PayPalItem item = new PayPalItem(product.getName(), itemQuantity,
                        product.getPrice(), AppConfig.DEFAULT_CURRENCY, product.getSku());

                productsInCart.add(item);
                ((BaseAdapter) CartFragmentYeScanner.mListView.getAdapter()).notifyDataSetChanged();

                Toast.makeText(getApplicationContext(),
                        item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();


    }
    //////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //toggleDrawer();
                return true;
            case R.id.logout:
                new SweetAlertDialog(MainActivityYeScanner.this, SweetAlertDialog.WARNING_TYPE)
                        .setCustomImage(R.mipmap.ic_launcher)
                        .setTitleText("Mbasera")
                        .setContentText("Are you sure you want to logout?")
                        .setCancelText("No,cancel please!")
                        .setConfirmText("Logout!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.setTitleText("Mbasera")
                                        .setContentText("You have cancelled the logout")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);

                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                logoutUser();
                            }
                        })
                        .show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivityYeScanner.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
