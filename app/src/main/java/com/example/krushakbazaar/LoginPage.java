package com.example.krushakbazaar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    private Button backButtonLoginPage, loginButtonForGovtOfficial, loginButtonForFarmer, loginButtonForConsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        backButtonLoginPage = findViewById(R.id.back_button_loginpage);
        loginButtonForGovtOfficial = findViewById(R.id.login_for_govtofficial_button_loginpage);
        loginButtonForFarmer = findViewById(R.id.login_for_farmer_button_loginpage);
        loginButtonForConsumer = findViewById(R.id.login_for_consumer_button_loginpage);

        backButtonLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        loginButtonForGovtOfficial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, LoginPageForGovtOfficial.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        loginButtonForFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, LoginPageForFarmer.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });

        loginButtonForConsumer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, LoginPageForConsumer.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }
}