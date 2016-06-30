package com.vince.empire.mbasera.scanner.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.mainmenu.CartActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VinceGee on 06/11/2016.
 */
public class SearchDetailsActivity extends Activity {
    public static int mTotalQuantity;
    public static List<Product> productsInCart = new ArrayList<Product>();

    TextView mTextViewItemQuantity;
    Button onButtonPlus;
    Button onButtonMinus;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private int itemQuantity = 0;

    //@OnClick(R.id.checkout_fragment_item_details_button_plus)
    public void onButtonPlusClick(View view) {
        itemQuantity++;
        mTextViewItemQuantity.setText(itemQuantity + "");
    }

    // @OnClick(R.id.checkout_fragment_item_details_button_minus)
    public void onButtonMinusClick(View view) {
        itemQuantity--;
        mTextViewItemQuantity.setText(itemQuantity + "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchdetails);

        mTextViewItemQuantity = (TextView) findViewById(R.id.search_checkout_fragment_item_details_text_view_item_quantity);
        onButtonPlus = (Button) findViewById(R.id.search_checkout_fragment_item_details_button_plus);
        onButtonMinus = (Button) findViewById(R.id.search_checkout_fragment_item_details_button_minus);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // ProductFragment.productsInCart
        //  List<Product> catalog = ShoppingCartHelper.getCatalog(getResources());

        final int productIndex = getIntent().getExtras().getInt(
                ShoppingCartHelper.PRODUCT_INDEX);
        final Product selectedProduct = SearchAdapter.getProductList().get(productIndex);
        //final Product selectedProduct = catalog.get(productIndex);

        // Set the proper image and text
        NetworkImageView image = (NetworkImageView) findViewById(R.id.searchImage);

        // user profile pic
        image.setImageUrl(selectedProduct.getImage(), imageLoader);
        // ImageView productImageView = (ImageView) findViewById(R.id.ImageViewProduct);
        // productImageView.setImageDrawable(selectedProduct.productImage);


        TextView productTitleTextView = (TextView) findViewById(R.id.TextViewSearchTitle);
        productTitleTextView.setText(selectedProduct.getName());
        TextView productDetailsTextView = (TextView) findViewById(R.id.TextViewSearchDetails);
        productDetailsTextView.setText(selectedProduct.getDescription());

        TextView productPriceTextView = (TextView) findViewById(R.id.TextViewSearchPrice);
        productPriceTextView.setText("$" + selectedProduct.getPrice());

        mTextViewItemQuantity.setText(itemQuantity + "");
        onButtonPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPlusClick(v);
            }
        });

        onButtonMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonMinusClick(v);
            }
        });

        // Update the current quantity in the cart
        TextView textViewCurrentQuantity = (TextView) findViewById(R.id.searchViewCurrentlyInCart);
        textViewCurrentQuantity.setText("Currently in Cart: " + 0/*selectedProduct.getQuantity()*/);


        // Save a reference to the quantity edit text
        // final EditText editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);

        Button addToCartButton = (Button) findViewById(R.id.searchButtonAddToCart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Check to see that a valid quantity was entered
                int quantity = 0;
                try {
                    quantity = itemQuantity;

                    if (quantity < 0) {
                        Toast.makeText(getBaseContext(),
                                "Please enter a quantity of 0 or higher",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }


                } catch (Exception e) {

                    Toast.makeText(getBaseContext(),
                            "Please enter a numeric quantity",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                // If we make it here, a valid quantity was entered
                ShoppingCartHelper.setQuantity(selectedProduct, quantity);
                mTotalQuantity = quantity;
                //for incrementing cart quantity
                AppConfig.CARTITEMSQUANTITY = mTotalQuantity;

                //add products to cart
                Product productInCrt = new Product(selectedProduct.getId(), selectedProduct.getName(), selectedProduct.getDescription()
                        , selectedProduct.getImage(), selectedProduct.getPrice(), selectedProduct.getSku(),selectedProduct.getBcode(),mTotalQuantity);


                for (int i = 0; i < productsInCart.size(); i++) {
                    //ShoppingCartHelper.removeProduct(selectedProduct);
                    if (productsInCart.get(i).getId().equalsIgnoreCase(selectedProduct.getId())) {

                        productsInCart.remove(i);
                        // ProductAdapter.getProductList().add(productInCrt);
                    }



                }

                AppConfig.CARTITEMSPRICE += productInCrt.getPrice().doubleValue()*mTotalQuantity;
                productsInCart.add(productInCrt);
//             ((BaseAdapter) CartActivity.mListViewCatalog.getAdapter()).notifyDataSetChanged();
                Log.e("SHOW product size", productsInCart.size() + "");
                Toast.makeText(getApplication(), "Product added to CART", Toast.LENGTH_SHORT).show();
                //  MainActivity main = new MainActivity();
                Intent intent = new Intent(v.getContext(), CartActivity.class);
                startActivity(intent);
                // Close the activity
                finish();
            }
        });

    }

}
