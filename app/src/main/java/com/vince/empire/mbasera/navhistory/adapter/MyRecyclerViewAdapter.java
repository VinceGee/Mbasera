package com.vince.empire.mbasera.navhistory.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.navhistory.HistoryViewActivity;
import com.vince.empire.mbasera.navhistory.adapter.model.Order;

import java.util.ArrayList;

/**
 * Created by VinceGee on 07/14/2016.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ProductHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<Order> mOrder;
    private static MyClickListener myClickListener;

    public MyRecyclerViewAdapter(ArrayList<Order> myProduct) {
        mOrder = myProduct;
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_items, parent, false);

        ProductHolder orderHolder = new ProductHolder(view);
        return orderHolder;
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        holder.id.setText("# "+mOrder.get(position).getOrderID());
        holder.date.setText(mOrder.get(position).getDate());
    }

    public void addItem(Order dataObj, int index) {
        mOrder.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mOrder.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mOrder.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class ProductHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView id;
        TextView date;

        public ProductHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.textOrder);
            date = (TextView) itemView.findViewById(R.id.textDateDisplay);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
            //Toast.makeText(v.getContext(),"New Activity",Toast.LENGTH_LONG).show();
            Intent historyDetailsIntent = new Intent(v.getContext(), HistoryViewActivity.class);
            historyDetailsIntent.putExtra("position", getPosition());
            /*historyDetailsIntent.putExtra("id", mOrder.get(getPosition()).getOrderID());
            historyDetailsIntent.putExtra("date", mOrder.get(getPosition()).getDate());
            historyDetailsIntent.putExtra("desc", mOrder.get(getPosition()).getDesc());
            historyDetailsIntent.putExtra("total", mOrder.get(getPosition()).getTotal());*/
            v.getContext().startActivity(historyDetailsIntent);

        }
    }
}