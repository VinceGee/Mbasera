package com.vince.empire.mbasera.scanner.mainstuff;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.R;


public class FullScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sc_activity_full_scanner);
        if (savedInstanceState == null) {
            replaceTutorialFragment();
        }


        // Update the current quantity in the cart
        TextView textViewCurrentQuantity = (TextView) findViewById(R.id.textViewYequantity);
        textViewCurrentQuantity.setText(AppConfig.CARTITEMSQUANTITY+"");

        // Update the total in the cart
        TextView textViewCurrentPrice = (TextView) findViewById(R.id.totalprice);
        textViewCurrentPrice.setText("Total: $"+ round(AppConfig.CARTITEMSPRICE,3)+"");
    }
    public void replaceTutorialFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.sc_container, new FullScannerFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
