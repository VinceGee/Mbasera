package com.vince.empire.mbasera.database.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.paypal.android.sdk.payments.PayPalItem;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.database.helper.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VinceGee on 06/09/2016.
 */
public class ProductFragment extends Fragment implements
        ProductListAdapter.ProductListAdapterListener {

    private static final int FRAGMENT_CONTAINER = R.id
            .pager;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //  private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PAYMENT = 1;
    // To store the products those are added to cart
    public static List<PayPalItem> productsInCart = new ArrayList<PayPalItem>();

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private Button btnCheckout;
    // To store all the products
    private List<Product> productsList;
    private ProductListAdapter adapter;
    // Progress dialog
    private ProgressDialog pDialog;

    private OnFragmentInteractionListener mListener;
    private int itemQuantity = 1;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        // Inflate the layout for this fragment

        listView = (ListView) view.findViewById(R.id.list);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        productsList = new ArrayList<Product>();
        adapter = new ProductListAdapter(getActivity(), productsList, this);

        listView.setAdapter(adapter);

        // Fetching products from server
        fetchProducts();
        return view;
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void fetchProducts() {
        // Showing progress dialog before making request

        pDialog.setMessage("Fetching products...");

        showpDialog();

        // Making json object request
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, AppConfig.URL_PRODUCTS, (String) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("This ProductFragment", response.toString());

                try {
                    JSONArray products = response.getJSONArray("products");

                    // looping through all product nodes and storing
                    // them in array list
                    for (int i = 0; i < products.length(); i++) {

                        JSONObject product = (JSONObject) products
                                .get(i);

                        String id = product.getString("id");
                        String name = product.getString("name");
                        String description = product.getString("description");
                        String image = product.getString("image");
                        BigDecimal price = new BigDecimal(product.getString("price"));
                        String sku = product.getString("sku");
                        String barcode = product.getString("bcode");

                        Product p = new Product(id, name, description,
                                image, price, sku,barcode);

                        productsList.add(p);
                    }

                    // notifying adapter about data changes, so that the
                    // list renders with new data
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                // hiding the progress dialog
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("ProductFragment", "Error: " + error.getMessage());
                Toast.makeText(getActivity(),"Mbasera could not find an internet connection. Please rectify this.", Toast.LENGTH_LONG).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    public void onAddToCartPressed(final Product product) {
        // custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.checkout_fragment_item_details);
        dialog.setTitle("Edit Cart");

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // set the custom dialog components - text, image and button
        final TextView mTextViewItemQuantity = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_quantity);
        TextView tvItemName = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_name);
        TextView tvDesc = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_description);
        TextView tvItemPrice = (TextView) dialog.findViewById(R.id.checkout_fragment_item_details_text_view_item_price);

        tvItemName.setText(product.getName());
        tvDesc.setText(product.getDescription());
        tvItemPrice.setText("Price: $" + product.getPrice());

        NetworkImageView image = (NetworkImageView) dialog.findViewById(R.id.checkout_fragment_item_details_image_view);

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

                PayPalItem item = new PayPalItem(
                        product.getName(),
                        itemQuantity,
                        product.getPrice(),
                        AppConfig.DEFAULT_CURRENCY,
                        product.getSku());

                productsInCart.add(item);

                ((BaseAdapter) CartFragment.mListView.getAdapter()).notifyDataSetChanged();

                Toast.makeText(getActivity(),item.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
