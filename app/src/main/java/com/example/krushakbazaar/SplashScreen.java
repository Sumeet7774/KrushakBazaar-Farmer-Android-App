package com.example.krushakbazaar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    SessionManagement sessionManagement;
    private static final int SPLASH_SCREEN_TIMEOUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        sessionManagement = new SessionManagement(this);

        new Handler().postDelayed(() -> {
            String userType = sessionManagement.getUserType();
            Intent intent;
            if ("Farmer".equals(userType))
            {
                intent = new Intent(SplashScreen.this, HomeScreenForFarmers.class);
            }
            else if ("admin".equals(userType))
            {
                intent = new Intent(SplashScreen.this, HomeScreenForAdmin.class);
            }
            else
            {
                intent = new Intent(SplashScreen.this, HomeScreenForConsumers.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, SPLASH_SCREEN_TIMEOUT);
    }
}