package com.vince.empire.mbasera.scanner.search;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;

import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.mainmenu.CartActivity;
import com.vince.empire.mbasera.utilities.MyTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VinceGee on 06/11/2016.
 */
public class CartDetailsActivity extends Activity {
    private int itemQuantity = 0;
    public static int mTotalQuantity;

    // To store the products those are added to cart
    public static List<Product> productsInCart = new ArrayList<Product>();

    //@Bind(R.id.checkout_fragment_item_details_text_view_item_quantity)
    TextView mTextViewItemQuantity;
    ImageView onButtonPlus;
    ImageView onButtonMinus;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

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
        setContentView(R.layout.cartdetails);

        mTextViewItemQuantity = (TextView) findViewById(R.id.ccheckout_fragment_item_details_text_view_item_quantity);
        onButtonPlus = (ImageView) findViewById(R.id.ccheckout_fragment_item_details_button_plus);
        onButtonMinus = (ImageView) findViewById(R.id.ccheckout_fragment_item_details_button_minus);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        // ProductFragment.productsInCart
        //  List<Product> catalog = ShoppingCartHelper.getCatalog(getResources());

        final int productIndex = getIntent().getExtras().getInt(
                ShoppingCartHelper.PRODUCT_INDEX);
        final int obtainedQuantity = getIntent().getExtras().getInt("currQuantity");
        Log.e("Quantity",obtainedQuantity+"");
        final Product selectedProduct = CartListAdapter.getProductList().get(productIndex);
        //final Product selectedProduct = catalog.get(productIndex);
        selectedProduct.setQuantity(obtainedQuantity);

        // Set the proper image and text
        NetworkImageView image = (NetworkImageView)findViewById(R.id.cproductImage);

        // user profile pic
        image.setImageUrl(selectedProduct.getImage(), imageLoader);
        // ImageView productImageView = (ImageView) findViewById(R.id.ImageViewProduct);
        // productImageView.setImageDrawable(selectedProduct.productImage);


        TextView productTitleTextView = (TextView) findViewById(R.id.cTextViewProductTitle);
        productTitleTextView.setText(selectedProduct.getName());
        TextView productDetailsTextView = (TextView) findViewById(R.id.cTextViewProductDetails);
        productDetailsTextView.setText(selectedProduct.getDescription());

        TextView productPriceTextView = (TextView) findViewById(R.id.cTextViewProductPrice);
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
        //TextView textViewCurrentQuantity = (TextView) findViewById(R.id.ctextViewCurrentlyInCart);
        //textViewCurrentQuantity.setText("Currently in Cart: " + obtainedQuantity);


        // Save a reference to the quantity edit text
        // final EditText editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);

        MyTextView addToCartButton = (MyTextView) findViewById(R.id.cButtonAddToCart);
        MyTextView removeToCartButton = (MyTextView) findViewById(R.id.cButtonRemoveToCart);
        removeToCartButton.setOnClickListener(new View.OnClickListener() {

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
                //add products to cart
              /*  Product productInCrt = new Product(selectedProduct.getId(),selectedProduct.getName(),selectedProduct.getImage()
                        ,selectedProduct.getDescription(),selectedProduct.getPrice(),productIndex,mTotalQuantity);*/
                Product productInCrt = new Product(selectedProduct.getId(), selectedProduct.getName(), selectedProduct.getDescription()
                        , selectedProduct.getImage(), selectedProduct.getPrice(), selectedProduct.getSku(),selectedProduct.getBcode(),mTotalQuantity);




                CartListAdapter.getProductList().remove(productIndex);

                // productsInCart.add(productInCrt);
                ((BaseAdapter) CartActivity.mListViewCatalog.getAdapter()).notifyDataSetChanged();
                // Log.e("SHOW product szie",productsInCart.size()+"");

                // Close the activity

                finish();


            }
        });

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
                //add products to cart
              /*  Product productInCrt = new Product(selectedProduct.getId(),selectedProduct.getName(),selectedProduct.getImage()
                        ,selectedProduct.getDescription(),selectedProduct.getPrice(),productIndex,mTotalQuantity);*/
                Product productInCrt = new Product(selectedProduct.getId(), selectedProduct.getName(), selectedProduct.getDescription()
                        , selectedProduct.getImage(), selectedProduct.getPrice(), selectedProduct.getSku(),selectedProduct.getBcode(),mTotalQuantity);

                if(obtainedQuantity!=mTotalQuantity){
                    //ShoppingCartHelper.removeProduct(selectedProduct);
                    CartListAdapter.getProductList().remove(productIndex);
                    CartListAdapter.getProductList().add(productInCrt);

                }
                //editing cart quantity
                AppConfig.CARTITEMSQUANTITY = mTotalQuantity;

                //for (int i = 0; i < productsInCart.size(); i++) {


                  //  AppConfig.CARTITEMSPRICE += productsInCart.get(i).getPrice().doubleValue();

               // }

                // productsInCart.add(productInCrt);
                ((BaseAdapter) CartActivity.mListViewCatalog.getAdapter()).notifyDataSetChanged();
                // Log.e("SHOW product szie",productsInCart.size()+"");

                // Close the activity

                finish();
            }
        });

    }

}
