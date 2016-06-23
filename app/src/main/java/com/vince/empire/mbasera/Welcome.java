package com.vince.empire.mbasera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.vince.empire.mbasera.login.LoginActivity;
import com.vince.empire.mbasera.login.SignupActivity;

/**
 * Created by VinceGee on 05/30/2016.
 */
public class Welcome extends AppCompatActivity {
    TextView signin;
    TextView signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        signin = (TextView)findViewById(R.id.signin);
        signup = (TextView)findViewById(R.id.Signup);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(Welcome.this, SignupActivity.class);
                startActivity(it);

            }
        });



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(Welcome.this, LoginActivity.class);
                startActivity(it);


            }
        });

    }
}
