package com.vince.empire.mbasera.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.vince.empire.mbasera.database.MainActivityYeDatabase;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.SettingsAngu;
import com.vince.empire.mbasera.login.LoginActivity;
import com.vince.empire.mbasera.login.SQLiteHandler;
import com.vince.empire.mbasera.login.SessionManager;
import com.vince.empire.mbasera.scanner.mainstuff.FullScannerActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by VinceGee on 05/30/2016.
 */
public class MainActivityYemainmenu extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, NavigationView.OnNavigationItemSelectedListener {
        SliderLayout mDemoSlider;

private ListView list;
private ArrayList<Beanclass> Bean;
private ListbaseAdapter baseAdapter;
private SQLiteHandler db;
private SessionManager session;


private int[] IMAGE = {R.drawable.slidertea, R.drawable.slider2, R.drawable.slider3,};

private String[] TITLE = {"Look for goods using a scanner", "Select from a list of products", "History"};

private String[] SUBTITLE = {"Scan with your phone camera.", "Search from a list", "Your Mbasera history"};

private String[] SHOP = {"Scan Now", "Select Now", "View history"};



@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_yemain_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);

        TextView nameView = (TextView) hView.findViewById(R.id.nameView);
        TextView emailView = (TextView) hView.findViewById(R.id.emailView);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
        logoutUser();
        }

        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        String fullname = user.get("name");
        String email = user.get("email");

        nameView.setText(fullname);
        emailView.setText(email);

        list = (ListView)findViewById(R.id.list);

//        *****listview*******

        Bean = new ArrayList<Beanclass>();

        for (int i= 0; i< TITLE.length; i++){
        Beanclass bean = new Beanclass(IMAGE[i], TITLE[i], SUBTITLE[i], SHOP[i]);
        Bean.add(bean);
        }

        baseAdapter = new ListbaseAdapter(MainActivityYemainmenu.this, Bean) {

        };

        list.setAdapter(baseAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position){
        case 0:
        intent = new Intent(getApplicationContext(), FullScannerActivity.class);
        startActivity(intent);
        break;
        case 1:
        intent = new Intent(getApplicationContext(), MainActivityYeDatabase.class);
        startActivity(intent);
        break;
        case 2:
        intent = new Intent(getApplicationContext(), SettingsAngu.class);
        startActivity(intent);
        break;
default:
        }

        }
        });

//        ******slider***********
        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("1", R.drawable.s1);
        file_maps.put("2",R.drawable.s2);
        file_maps.put("3", R.drawable.s3);

        for(String name : file_maps.keySet()){
        TextSliderView textSliderView = new TextSliderView(this);
        // initialize a SliderLayout
        textSliderView
        //  .description(name)
        .image(file_maps.get(name))
        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
        .setOnSliderClickListener(this);


        textSliderView.bundle(new Bundle());
        textSliderView.getBundle().putString("extra", name);

        mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new ChildAnimationExample());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        }

@Override
public void onSliderClick(BaseSliderView slider) {

        }

/**
 * Logging out the user. Will set isLoggedIn flag to false in shared
 * preferences Clears the user data from sqlite users table
 * */
private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivityYemainmenu.this, LoginActivity.class);
        startActivity(intent);
        finish();
        }

@Override
public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
        drawer.closeDrawer(GravityCompat.START);
        } else {
        super.onBackPressed();
        }
        }

@Override
public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
        }

@Override
public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        return true;
        }

        return super.onOptionsItemSelected(item);
        }

@SuppressWarnings("StatementWithEmptyBody")
@Override
public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
        // Handle the camera action
        } else if (id == R.id.nav_scan) {

        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_rate) {

        }  else if (id == R.id.nav_abt_mbasera) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
        }
        }
