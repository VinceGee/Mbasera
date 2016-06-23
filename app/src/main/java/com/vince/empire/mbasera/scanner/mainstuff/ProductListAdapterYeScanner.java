package com.vince.empire.mbasera.scanner.mainstuff;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;


import java.util.List;

/**
 * Created by VinceGee on 03/13/2016.
 */
public class ProductListAdapterYeScanner extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Product> products;
    private ProductListAdapterYeScannerListener listener;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProductListAdapterYeScanner(Activity activity, List<Product> feedItems,ProductListAdapterYeScannerListener listener) {
        this.activity = activity;
        this.products = feedItems;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int location) {
        return products.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_product, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        TextView name = (TextView) convertView.findViewById(R.id.productName);
        TextView description = (TextView) convertView
                .findViewById(R.id.productDescription);
        TextView price = (TextView) convertView.findViewById(R.id.productPrice);

        NetworkImageView image = (NetworkImageView) convertView
                .findViewById(R.id.productImage);

        Button btnAddToCart = (Button) convertView
                .findViewById(R.id.btnAddToCart);

        final Product product = products.get(position);

        name.setText(product.getName());

        description.setText(product.getDescription());

        price.setText("Price: $" + product.getPrice());

        // user profile pic
        image.setImageUrl(product.getImage(), imageLoader);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.onAddToCartScanner(product);
            }
        });

        return convertView;
    }

    public interface ProductListAdapterYeScannerListener {
        public void onAddToCartScanner(Product product);
    }
}
