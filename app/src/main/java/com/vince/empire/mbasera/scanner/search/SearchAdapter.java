package com.vince.empire.mbasera.scanner.search;

import android.app.Activity;
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

import java.util.List;

/**
 * Created by VinceGee on 06/11/2016.
 */
public class SearchAdapter extends BaseAdapter {

    private static List<Product> mProductList;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private LayoutInflater mInflater;
    private boolean mShowQuantity;

    public SearchAdapter(Activity activity, List<Product> list, LayoutInflater inflater, boolean showQuantity) {
        mProductList = list;
        mInflater = inflater;
        mShowQuantity = showQuantity;
    }

    public static List<Product> getProductList() {
        return mProductList;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewItem item;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search, null);
            item = new ViewItem();

//            item.productImageView = (ImageView) convertView
//                    .findViewById(R.id.ImageViewItem);

            item.productTitle = (TextView) convertView
                    .findViewById(R.id.sTextViewItem);
            item.productDesc = (TextView) convertView.findViewById(R.id.sTextViewDesc);

            item.productQuantity = (TextView) convertView
                    .findViewById(R.id.stextViewQuantity);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        Product curProduct = mProductList.get(position);

        item.image = (NetworkImageView) convertView
                .findViewById(R.id.sImageViewItem);

        // user profile pic
        item.image.setImageUrl(curProduct.getImage(), imageLoader);

        //  item.productImageView.setImageDrawable(curProduct.productImage);
        item.productTitle.setText(curProduct.getName());


        item.productDesc.setText(curProduct.getDescription());

        // Show the quantity in the cart or not
        if (mShowQuantity) {
            item.productQuantity.setText("Quantity: " + ShoppingCartHelper.getProductQuantity(curProduct));
        } else {
            // Hid the view
            item.productQuantity.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewItem {
        NetworkImageView image;
        TextView productTitle;
        TextView productDesc;
        TextView productQuantity;
    }

}
