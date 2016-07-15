package com.vince.empire.mbasera.navhistory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.login.SQLiteHandler;
import com.vince.empire.mbasera.navhistory.adapter.DividerItemDecoration;
import com.vince.empire.mbasera.navhistory.adapter.MyRecyclerViewAdapter;

/**
 * Created by VinceGee on 07/14/2016.
 */
public class SaveResultListActivity extends AppCompatActivity {
    public static String name = new String();
    private static String LOG_TAG = "RecyclerViewActivity";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.json_info_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_json);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Mbasera History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // name = getIntent().getExtras().getString("name");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // mAdapter = new MyRecyclerViewAdapter(getDataSet());
        db = new SQLiteHandler(getApplicationContext());
        AppConfig.productsOrderList.clear();
        AppConfig.productsOrderList = db.getOrderDetails();
        mAdapter = new MyRecyclerViewAdapter(AppConfig.productsOrderList);

        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecoration);

        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    /*private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 20; index++) {
            DataObject obj = new DataObject("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(index, obj);
        }
        return results;
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
       /* if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }else if (item.getItemId() == R.id.refresh) {
            Toast toast =Toast.makeText(getApplicationContext(),"Function deactivated",Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundResource(R.color.colorAccent_Red);
            toast.show();

        }else if (item.getItemId() == R.id.hiDelete) {
            db.deleteProduct();
            mAdapter.notifyDataSetChanged();
            Toast toast=Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT);
            View view = toast.getView();
            view.setBackgroundResource(R.color.colorAccent_Red);
            toast.show();
        }*/

        return super.onOptionsItemSelected(item);
    }

}
