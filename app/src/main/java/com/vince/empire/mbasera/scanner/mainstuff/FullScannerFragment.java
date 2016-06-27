package com.vince.empire.mbasera.scanner.mainstuff;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.scanner.MainActivityYeScanner;
import com.vince.empire.mbasera.scanner.search.CatalogActivity;
import com.vince.empire.mbasera.scanner.search.ShoppingCartFragment;
import com.vince.empire.mbasera.scanner.search.ShoppingCartHelper;
import com.vince.empire.mbasera.scanner.utilities.ZXingScannerView;
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


public class FullScannerFragment extends Fragment implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private RecyclerView mRecyclerView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mScannerView = new ZXingScannerView(getActivity());
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }
        setupFormats();


        return mScannerView;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem;

        if (mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);


        if (mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_flash:
                mFlash = !mFlash;
                if (mFlash) {
                    item.setTitle(R.string.flash_on);
                } else {
                    item.setTitle(R.string.flash_off);
                }
                mScannerView.setFlash(mFlash);
                return true;
            case R.id.menu_auto_focus:
                mAutoFocus = !mAutoFocus;
                if (mAutoFocus) {
                    item.setTitle(R.string.auto_focus_on);
                } else {
                    item.setTitle(R.string.auto_focus_off);
                }
                mScannerView.setAutoFocus(mAutoFocus);
                return true;
            case R.id.menu_formats:
                DialogFragment fragment = FormatSelectorDialogFragment.newInstance(this, mSelectedIndices);
                fragment.show(getActivity().getSupportFragmentManager(), "format_selector");
                return true;
            case R.id.menu_camera_selector:
                mScannerView.stopCamera();
                DialogFragment cFragment = CameraSelectorDialogFragment.newInstance(this, mCameraId);
                cFragment.show(getActivity().getSupportFragmentManager(), "camera_selector");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
        }

        searchkey = rawResult.getText();
        new LoadIdioms().execute();

    }

    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment.newInstance("Scan Results", message, this);
        fragment.show(getActivity().getSupportFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
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
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Searching for product. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            timerDelayRemoveDialog(20000, pDialog);
        }

        public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    d.dismiss();
                    Toast.makeText(getActivity(),"Error accessing server. Try again later",Toast.LENGTH_LONG).show();
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
            Log.e("Search keyword: ", searchkey);
            //params.add(new BasicNameValuePair("keyword", searchkey));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.URL_SEARCH, "GET", params);

            if (json != null) {
                // Check your log cat for JSON response
                Log.e("JSON result", json.toString());

                return json;
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
                Toast.makeText(getActivity(), "Network problem. Try again later.", Toast.LENGTH_LONG);

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
            getActivity().runOnUiThread(new Runnable() {
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
                                    productsList.get(i).getPrice(), productsList.get(i).getSku(), productsList.get(i).getBcode()));
                           /* addProductCatalog(productsList.get(i).getId(), productsList.get(i).getName(), productsList.get(i).getDescription(), productsList.get(i).getImage(),
                                    productsList.get(i).getPrice(), productsList.get(i).getSku());*/
                        }
                        ((BaseAdapter) ShoppingCartFragment.mListViewCatalog.getAdapter()).notifyDataSetChanged();
                        Log.e("Before intent", "Start another intent");

                        Intent intent = new Intent(getActivity(), CatalogActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        //  ((BaseAdapter) CatalogActivity.catalogList.getAdapter()).notifyDataSetChanged();
                        Log.e("After intent", "Start another intent");
                    }else{
                        Toast.makeText(getActivity(),"No such product. Try another.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivityYeScanner.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            });

        }

        public void addProductCatalog(String id, String title, String desc, String image, BigDecimal price, String sku, String bcode) {
            if (ShoppingCartHelper.catalog == null) {
                ShoppingCartHelper.catalog = new Vector<Product>();

                ShoppingCartHelper.catalog.add(new Product(id, title, desc, image, price, sku,bcode));
                int nothing;

            }
        }

    }
}
