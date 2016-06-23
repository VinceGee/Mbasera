package com.vince.empire.mbasera.scanner.search;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.vince.empire.mbasera.AppController;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.database.helper.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by VinceGee on 06/11/2016.
 */
public class CartListAdapter extends BaseAdapter {
    private static List<Product> productList = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;
    private boolean mShowQuantity;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public CartListAdapter(Activity activity, List<Product> feedItems, boolean showQuantity) {
        this.activity = activity;
        this.productList = feedItems;
        mShowQuantity = showQuantity;

    }
    public static List<Product> getProductList(){
        return productList;
    }
    public static void removeItem(Objects pos){
        productList.remove(pos);
    }
    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int location) {
        return productList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewItem item;

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.clist_item_cart, null);
            item = new ViewItem();

//            item.productImageView = (ImageView) convertView
//                    .findViewById(R.id.ImageViewItem);

            item.productTitle = (TextView) convertView
                    .findViewById(R.id.cTextViewItem);

            item.productQuantity = (TextView) convertView
                    .findViewById(R.id.ctextViewQuantity);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        Product curProduct = productList.get(position);

        item.image = (NetworkImageView) convertView
                .findViewById(R.id.cImageViewItem);

        // user profile pic
        item.image.setImageUrl(curProduct.getImage(), imageLoader);

        //  item.productImageView.setImageDrawable(curProduct.productImage);
        item.productTitle.setText(curProduct.getName());

        // Show the quantity in the cart or not
        if (mShowQuantity) {
            item.productQuantity.setText("Quantity: " + curProduct.getQuantity());//ShoppingCartHelper.getProductQuantity(curProduct
        } else {
            // Hid the view
            item.productQuantity.setVisibility(View.GONE);
        }

        return convertView;


    }
    private class ViewItem {
        NetworkImageView image;
        TextView productTitle;
        TextView productQuantity;
    }

}
