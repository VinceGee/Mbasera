package com.vince.empire.mbasera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by VinceGee on 05/31/2016.
 */
public class SettingsAngu extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setTheme(Utils.getCurrentTheme(this));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }
}
