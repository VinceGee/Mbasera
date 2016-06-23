package com.vince.empire.mbasera.scanner.search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.scanner.MainActivityYeScanner;
import com.vince.empire.mbasera.scanner.mainstuff.FullScannerActivity;
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
public class Search extends Fragment implements View.OnClickListener {
    private EditText mTxtkeyword;
    private Button mBtnsearch;
    private Button mScan;

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
    // static FirstPageFragmentListener firstPageListener;


   /* public static Search newInstance() {
        return new Search(firstPageListener);
    }


    public Search(FirstPageFragmentListener listener) {
        firstPageListener = listener;
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_search, container, false);
        mTxtkeyword = (EditText) v.findViewById(R.id.txtkeyword);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mBtnsearch = (Button) v.findViewById(R.id.btnsearch);
        mScan = (Button) v.findViewById(R.id.scan);
        mBtnsearch.setOnClickListener(this);
        mScan.setOnClickListener(this);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search);


//        insertbtn=(Button)view.findViewById(R.id.insertbtn);
//        insertbtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnsearch) {

            //searchkey = mTxtkeyword.getText().toString();
            //new LoadIdioms().execute();


            Intent searchIntent = new Intent(getActivity(), ListResult.class);
            //send the keyword to the next screen
            searchIntent.putExtra("keyword", mTxtkeyword.getText().toString());
            //call the screen for listing
            startActivity(searchIntent);
        } else if (v.getId() == R.id.scan) {
            Intent searchIntent = new Intent(getActivity(), FullScannerActivity.class);
            //send the keyword to the next screen
            searchIntent.putExtra("keyword", mTxtkeyword.getText().toString());
            //call the screen for listing
            startActivity(searchIntent);
            //  firstPageListener.onSwitchToNextFragment();

        }
    }

}
