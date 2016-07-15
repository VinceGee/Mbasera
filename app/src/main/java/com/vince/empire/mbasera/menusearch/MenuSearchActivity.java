package com.vince.empire.mbasera.menusearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.mainmenu.MainActivityYemainmenu;
import com.vince.empire.mbasera.scanner.search.CatalogActivity;
import com.vince.empire.mbasera.utilities.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by VinceGee on 07/14/2016.
 */
public class MenuSearchActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_IDIOMS = "idioms";
    private static final String TAG_ID = "id";
    private static final String TAG_BARCODE = "bcode";
    private static final String TAG_PNAME = "name";
    private static final String TAG_PDESC = "description";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_PRICE = "price";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_SKU = "sku";
    //search key value
    private String searchkey;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> idiomsList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    // products JSONArray
    JSONArray idioms = null;
    private ProgressDialog pDialog;
    private List<Product> productsList = new ArrayList<Product>();
    private Button mSearchButton;
    private EditText mEditTextSearch;
    //products in cart
    //public static List<Product> catalogueProductsInCart = new ArrayList<Product>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_search);
        mSearchButton = (Button) findViewById(R.id.searchButton);
        mEditTextSearch = (EditText) findViewById(R.id.editTextSearch);



        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchkey = mEditTextSearch.getText().toString();
                Log.e("Search Text:",searchkey);
                // if( isNetworkAvailable()) {
                new LoadIdioms().execute();
                // }else{
                //      Toast.makeText(getApplicationContext(),"No Network Services",Toast.LENGTH_LONG).show();
                // }
            }
        });

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Background Async Task to Load Idioms by making HTTP Request
     */
    class LoadIdioms extends AsyncTask<String, String, JSONObject> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MenuSearchActivity.this);
            pDialog.setMessage("Searching for product. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            //timerDelayRemoveDialog(20000, pDialog);
        }

        public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    d.dismiss();
                    Toast.makeText(getApplicationContext(),"Error accessing server. Try again later",Toast.LENGTH_LONG).show();
                }
            }, time);
        }

        /**
         * getting Idioms from url
         */
        protected JSONObject doInBackground(String... args) {
            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("keyword", searchkey);
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //value captured from previous intent
            Log.e("AAAA Search keyword: ", searchkey);
            //params.add(new BasicNameValuePair("keyword", searchkey));
            // getting JSON string from URL


            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_SEARCH, "GET", params);

            if (json != null) {
                // Check your log cat for JSON response
                Log.e("JSON result", json.toString());

                //return json;
            }

            // Check your log cat for JSON response
            //Log.e("Search idioms: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    idioms = json.getJSONArray(TAG_IDIOMS);

                    // looping through All Products
                    for (int i = 0; i < idioms.length(); i++) {
                        JSONObject c = idioms.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String barcode = c.getString(TAG_BARCODE);
                        String name = c.getString(TAG_PNAME);
                        String desc = c.getString(TAG_PDESC);
                        //  String category = c.getString(TAG_CATEGORY);
                        String image = c.getString(TAG_IMAGE);
                        BigDecimal price = new BigDecimal(c.getString(TAG_PRICE));
                        String sku = c.getString(TAG_SKU);



                        //adding to list
                        productsList.add(new Product(id, name, desc, image, price, sku,barcode));


                    }
                } else {
                    // no idioms found
                    //do something
                }
            } catch (JSONException e) {
                //Toast.makeText(getActivity(), "Network problem. Try again later.", Toast.LENGTH_LONG);

                Log.e("JSONException", e.getMessage());
            }

            //return "success";
            return null;
        }



        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(JSONObject jsonObject) {
            // dismiss the dialog after getting the related idioms
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */

                    Log.e("List size", productsList.size() + "");
                    //  Log.e("Before intent",TAG_CATEGORY+TAG_PNAME+TAG_PDESC);
                    AppConfig.catalogueProductsInCart.clear();
                    if(productsList.size()>0) {
                        for (int i = 0; i < productsList.size(); i++) {

                            AppConfig.catalogueProductsInCart.add(new Product(productsList.get(i).getId(), productsList.get(i).getName(), productsList.get(i).getDescription(), productsList.get(i).getImage(),
                                    productsList.get(i).getPrice(), productsList.get(i).getSku(), productsList.get(i).getBcode()));
                           /* addProductCatalog(productsList.get(i).getId(), productsList.get(i).getName(), productsList.get(i).getDescription(), productsList.get(i).getImage(),
                                    productsList.get(i).getPrice(), productsList.get(i).getSku());*/
                        }
                        // ((BaseAdapter) ShoppingCartFragment.mListViewCatalog.getAdapter()).notifyDataSetChanged();
                        Log.e("Before intent", "Start another intent");

                        Intent intent = new Intent(getApplicationContext(), CatalogActivity.class);
                        startActivity(intent);
                        finish();
                        //  ((BaseAdapter) CatalogActivity.catalogList.getAdapter()).notifyDataSetChanged();
                        Log.e("After intent", "Start another intent");
                    }else{
                        Toast.makeText(getApplicationContext(),"No such product. Try another.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivityYemainmenu.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }



    }
}
