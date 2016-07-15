package com.vince.empire.mbasera.navhistory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.navhistory.adapter.model.Order;

import java.util.ArrayList;

/**
 * Created by VinceGee on 07/14/2016.
 */
public class HistoryViewActivity extends AppCompatActivity {
    private TextView mOrderId;
    private TextView mDate;
    private TextView mDesc;
    private TextView mTotal;
    private Order order;
    private ArrayList<Order> mOrderList;
    private static int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_his);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
// ...
        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Remove default title text
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setTitle("History Details");
        //getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mOrderId = (TextView)findViewById(R.id.hOrderIdtextView);
        mDate = (TextView)findViewById(R.id.hDatetextView);
        mDesc = (TextView)findViewById(R.id.hDesctextView);
        mTotal = (TextView)findViewById(R.id.hTotaltextView);

        final int orderIndex = getIntent().getExtras().getInt("position");
        index = orderIndex;

        order =  AppConfig.productsOrderList.get(orderIndex);

        mOrderId.setText("#"+order.getOrderID());
        mDate.setText(order.getDate());
        mDesc.setText(order.getDesc());
        mTotal.setText("$"+order.getTotal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }else if (item.getItemId() == R.id.hiDelete) {

            //  AppConfig.productsOrderList.get(index).
            //   finish();
            Toast.makeText(getApplicationContext(),"Work In Progress",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }


}
