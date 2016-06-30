package com.vince.empire.mbasera.scanner.mainstuff;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vince.empire.mbasera.AppConfig;
import com.vince.empire.mbasera.R;
import com.vince.empire.mbasera.mainmenu.CartActivity;


public class FullScannerActivity extends AppCompatActivity {

    private Button mViewCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sc_activity_full_scanner);
        if (savedInstanceState == null) {
            replaceTutorialFragment();
        }
        mViewCart = (Button)findViewById(R.id.viewCart);


        // Update the current quantity in the cart
        TextView textViewCurrentQuantity = (TextView) findViewById(R.id.textViewYequantity);
        textViewCurrentQuantity.setText(AppConfig.CARTITEMSQUANTITY+"");

        // Update the total in the cart
        TextView textViewCurrentPrice = (TextView) findViewById(R.id.totalprice);
        textViewCurrentPrice.setText("Total: $"+ round(AppConfig.CARTITEMSPRICE)+"");
        mViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScannerActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void replaceTutorialFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.sc_container, new FullScannerFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    public static double round(double value) {
        String str = String.format("%1.2f", value);
      //  d = Double.valueOf(str);
        return  Double.valueOf(str);
    }
}
