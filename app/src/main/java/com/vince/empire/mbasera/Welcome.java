package com.vince.empire.mbasera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.vince.empire.mbasera.login.LoginActivity;
import com.vince.empire.mbasera.login.SessionManager;
import com.vince.empire.mbasera.login.SignupActivity;
import com.vince.empire.mbasera.mainmenu.MainActivityYemainmenu;

/**
 * Created by VinceGee on 05/30/2016.
 */
public class Welcome extends AppCompatActivity {
    TextView signin;
    TextView signup;
    private SessionManager session;
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


        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Welcome.this, MainActivityYemainmenu.class);
            startActivity(intent);
            finish();

        }else {

            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent it = new Intent(Welcome.this, LoginActivity.class);
                    startActivity(it);


                }
            });
        }
    }
}
