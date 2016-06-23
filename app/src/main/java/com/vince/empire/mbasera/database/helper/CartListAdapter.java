package com.vince.empire.mbasera.database.helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalItem;
import com.vince.empire.mbasera.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VinceGee on 06/09/2016.
 */
public class CartListAdapter extends BaseAdapter {
    private List<PayPalItem> palItems = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;

    public CartListAdapter(Activity activity, List<PayPalItem> feedItems) {
        this.activity = activity;
        this.palItems = feedItems;

    }

    @Override
    public int getCount() {
        return palItems.size();
    }

    @Override
    public Object getItem(int location) {
        return palItems.get(location);
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
            convertView = inflater.inflate(R.layout.list_item_cart, null);
        TextView name = (TextView) convertView.findViewById(R.id.cProName);
        TextView price = (TextView) convertView.findViewById(R.id.cPrice);
        // TextView quantity = (TextView) convertView.findViewById(R.id.cQnty);
        name.setText(palItems.get(position).getName());
        //quantity.setText("Qnty: 1 * "+palItems.get(position).getQuantity());
        price.setText("$ "+palItems.get(position).getPrice()+" * "+palItems.get(position).getQuantity());
        //image.setImageUrl(product.getImage(), imageLoader);
        return convertView;
    }

}

