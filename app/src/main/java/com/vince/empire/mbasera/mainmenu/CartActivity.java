package com.vince.empire.mbasera.mainmenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
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
import com.vince.empire.mbasera.database.fragments.ProductFragment;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.login.SQLiteHandler;
import com.vince.empire.mbasera.login.SessionManager;
import com.vince.empire.mbasera.scanner.search.CartDetailsActivity;
import com.vince.empire.mbasera.scanner.search.CartListAdapter;
import com.vince.empire.mbasera.scanner.search.SearchDetailsActivity;
import com.vince.empire.mbasera.scanner.search.ShoppingCartHelper;
import com.vince.empire.mbasera.utilities.dialogs.SweetAlertDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = CartActivity.class.getSimpleName();

    private List<Product> mCartList;
    // To store the products those are added to cart
    public static List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();
    private CartListAdapter mProductAdapter;
    private Button mCheckOut;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private Button mContinueShopping;

    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(AppConfig.PAYPAL_ENVIRONMENT).clientId(
                    AppConfig.PAYPAL_CLIENT_ID);

    private int i = -1;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    public static ListView mListViewCatalog;

    public String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mCheckOut = (Button) findViewById(R.id.checkOutButton);
        mContinueShopping = (Button) findViewById(R.id.continueShopping);


        mCartList = AppConfig.productsInCart;
        Log.e("Check this size", "" + mCartList.size());

        // Make sure to clear the selections
        for (int i = 0; i < mCartList.size(); i++) {
            mCartList.get(i).selected = false;
        }



        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Starting PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);


        // Create the list
        mListViewCatalog = (ListView) findViewById(R.id.listViewCatalog);
        mProductAdapter = new CartListAdapter(this, mCartList, true);
        mListViewCatalog.setAdapter(mProductAdapter);

        mListViewCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                // int prodPos = mCartList.get(position).getPos();
                Intent productDetailsIntent = new Intent(getApplicationContext(), CartDetailsActivity.class);
                productDetailsIntent.putExtra(ShoppingCartHelper.PRODUCT_INDEX, position);
                productDetailsIntent.putExtra("currQuantity", mCartList.get(position).getQuantity());
                startActivity(productDetailsIntent);
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(this);

        // SQLite database handler
        db = new SQLiteHandler(this);

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();

        username = user.get("name");

        mContinueShopping.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,MainActivityYemainmenu.class);
                startActivity(intent);
            }
        });

        mCheckOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new SweetAlertDialog(CartActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setCustomImage(R.mipmap.ic_launcher)
                        .setTitleText("Mbasera")
                        .setContentText("Submit Order?")
                        .setCancelText("No,wait!")
                        .setConfirmText("Submit!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                // reuse previous dialog instance, keep widget user state, reset them if you need
                                sDialog.setTitleText("Mbasera")
                                        .setContentText("Keep making orders")
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
                                for(int i=0;i<mCartList.size();i++) {
                                    PayPalItem item = new PayPalItem(mCartList.get(i).getName(), mCartList.get(i).getQuantity(),
                                            mCartList.get(i).getPrice(), AppConfig.DEFAULT_CURRENCY, mCartList.get(i).getSku());

                                    productsInCart.add(item);
                                }

                                launchPayPalPayment();
                                sDialog.dismiss();
                            }


                        })

                       .show();
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the data
        if (mProductAdapter != null) {
            //mProductAdapter = new ProductAdapter(getActivity(),mCartList,getActivity().getLayoutInflater(), true);
            mProductAdapter.notifyDataSetChanged();
        }

        double subTotal = 0;
        for (Product p : mCartList) {
            int quantity = ShoppingCartHelper.getProductQuantity(p);
            //subTotal += p.getPrice() * quantity;
        }

        TextView productPriceTextView = (TextView) findViewById(R.id.TextViewSubtotal);
        productPriceTextView.setText("Total Items In Cart :" + mCartList.size());
    }

    /**
     * Preparing final cart amount that needs to be sent to PayPal for payment
     */
    private PayPalPayment prepareFinalCart() {

        PayPalItem[] items = new PayPalItem[ProductFragment.productsInCart.size()];
        items = ProductFragment.productsInCart.toArray(items);


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
     */
    private void launchPayPalPayment() {

        PayPalPayment thingsToBuy = prepareFinalCart();

        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /**
     * Receiving the PalPay payment response
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e("ProductFragment", confirm.toJSONObject().toString(4));
                        Log.e("ProductFragment", confirm.getPayment().toJSONObject()
                                .toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e("ProductFragment", "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);

                        // Now verify the payment on the server side
                        verifyPaymentOnServer(paymentId, payment_client);

                    } catch (JSONException e) {
                        Log.e("ProductFragment", "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("ProductFragment", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e("ProductFragment",
                        "An invalid Payment or PayPalConfiguration was submitted.");
            }
        }
    }

    /**
     * Verifying the mobile payment on the server to avoid fraudulent payment
     */
    private void verifyPaymentOnServer(final String paymentId, final String payment_client) {
        // Showing progress dialog before making request
        pDialog.setMessage("Verifying payment...");
        showDialog();

        StringRequest verifyReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_PAYMENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("ProductFragment", "verify payment: " + response.toString());

                try {
                    JSONObject res = new JSONObject(response);
                    boolean error = res.getBoolean("error");
                    String message = res.getString("message");

                    // user error boolean flag to check for errors

                    Toast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_SHORT).show();

                    if (!error) {
                        // empty the cart
                        ProductFragment.productsInCart.clear();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // hiding the progress dialog
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CartFragment", "Verify Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hiding the progress dialog
                hideDialog();
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
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if ((this.pDialog != null) && this.pDialog.isShowing()) {
            this.pDialog.dismiss();
        }
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
