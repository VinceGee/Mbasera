package com.vince.empire.mbasera.scanner.search;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.scanner.MainActivityYeScanner;
import com.vince.empire.mbasera.utilities.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by VinceGee on 06/10/2016.
 */
public class ListResult extends ListActivity {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_IDIOMS = "idioms";
    private static final String TAG_ID = "id";
    private static final String TAG_PNAME = "name";
    private static final String TAG_PDESC = "description";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_PRICE = "price";
    private static final String TAG_IMAGE = "image";
    private static final String TAG_SKU = "sku";
    //search key value
    public String searchkey;
    // Progress Dialog
    int pple;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> idiomsList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    // products JSONArray
    JSONArray idioms = null;
    private ProgressDialog pDialog;
    private List<Product> productsList = new ArrayList<Product>();
    //products in cart
    public static List<Product> catalogueProductsInCart = new ArrayList<Product>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_result);
        Intent myIntent = getIntent();
        // gets the arguments from previously created intent
        searchkey = myIntent.getStringExtra("keyword");

        new LoadIdioms().execute(searchkey);
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
            pDialog = new ProgressDialog(ListResult.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            timerDelayRemoveDialog(20000, pDialog);
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

            HashMap<String, String> params = new HashMap<>();
            params.put("keyword", searchkey);
            // Building Parameters
            //List<NameValuePair> params = new ArrayList<NameValuePair>();
            //value captured from previous intent
            Log.e("Search keyword: ", searchkey);
            //params.add(new BasicNameValuePair("keyword", searchkey));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_SEARCH, "GET", params);

            if (json != null) {
                Log.e("JSON result", json.toString());

                //return json;
            }

            // Check your log cat for JSON response
            //Log.e("Search idioms: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                //Log.e("eeeeeeeeeeeee", success+"");
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    idioms = json.getJSONArray(TAG_IDIOMS);

                    // looping through All Products
                    for (int i = 0; i < idioms.length(); i++) {
                        JSONObject c = idioms.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String name = c.getString(TAG_PNAME);
                        String desc = c.getString(TAG_PDESC);
                        String image = c.getString(TAG_IMAGE);
                        BigDecimal price = new BigDecimal(c.getString(TAG_PRICE));
                        String sku = c.getString(TAG_SKU);

                        //adding to list
                        productsList.add(new Product(id, name, desc, image, price, sku));


                    }
                } else {
                    // no idioms found
                    //do something
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            //return "success";
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(JSONObject json) {
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
                    catalogueProductsInCart.clear();
                    if(productsList.size()>0) {
                        for (int i = 0; i < productsList.size(); i++) {

                            catalogueProductsInCart.add(new Product(productsList.get(i).getId(), productsList.get(i).getName(), productsList.get(i).getDescription(), productsList.get(i).getImage(),
                                    productsList.get(i).getPrice(), productsList.get(i).getSku()));
                           /* addProductCatalog(productsList.get(i).getId(), productsList.get(i).getName(), productsList.get(i).getDescription(), productsList.get(i).getImage(),
                                    productsList.get(i).getPrice(), productsList.get(i).getSku());*/
                        }
                        ((BaseAdapter) ShoppingCartFragment.mListViewCatalog.getAdapter()).notifyDataSetChanged();
                        Log.e("Before intent", "Start another intent");

                        Intent intent = new Intent(ListResult.this, CatalogActivity.class);
                        startActivity(intent);
                        finish();
                        //  ((BaseAdapter) CatalogActivity.catalogList.getAdapter()).notifyDataSetChanged();
                        Log.e("After intent", "Start another intent");
                    }else{
                        Toast.makeText(getApplicationContext(),"No such product. Try another.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ListResult.this, MainActivityYeScanner.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }

        public void addProductCatalog(String id, String title, String desc, String image, BigDecimal price, String sku) {
            if (ShoppingCartHelper.catalog == null) {
                ShoppingCartHelper.catalog = new Vector<Product>();

                ShoppingCartHelper.catalog.add(new Product(id, title, desc, image,
                        price, sku));
                int nothing;

            }
        }

    }

}
