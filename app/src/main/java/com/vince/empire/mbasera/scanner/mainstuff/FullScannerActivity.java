package com.vince.empire.mbasera.scanner.mainstuff;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.vince.empire.mbasera.R;


public class FullScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sc_activity_full_scanner);
        if (savedInstanceState == null) {
            replaceTutorialFragment();
        }
    }
    public void replaceTutorialFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.sc_container, new FullScannerFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
