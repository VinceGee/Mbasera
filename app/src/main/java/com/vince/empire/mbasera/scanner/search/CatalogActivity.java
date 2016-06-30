package com.vince.empire.mbasera.scanner.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;
import com.vince.empire.mbasera.mainmenu.CartActivity;
import com.vince.empire.mbasera.scanner.mainstuff.FullScannerFragment;

import java.util.List;

/**
 * Created by VinceGee on 06/11/2016.
 */
public class CatalogActivity extends Activity {

    public static ListView catalogList;
    private List<Product> mProductList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalog);

        // Obtain a reference to the product catalog
        // mProductList = ShoppingCartHelper.getCatalog(getResources());
        mProductList = AppConfig.catalogueProductsInCart;
        // Create the list
        catalogList = (ListView) findViewById(R.id.zicataloglist);
        catalogList.setAdapter(new SearchAdapter(this, mProductList, getLayoutInflater(), false));

        catalogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent productDetailsIntent = new Intent(getBaseContext(), SearchDetailsActivity.class);
                productDetailsIntent.putExtra(ShoppingCartHelper.PRODUCT_INDEX, position);
                startActivity(productDetailsIntent);

            }
        });

        Button viewShoppingCart = (Button) findViewById(R.id.ButtonViewCart);
        viewShoppingCart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });

    }
}
