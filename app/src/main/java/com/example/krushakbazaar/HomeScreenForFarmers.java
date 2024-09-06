package com.example.krushakbazaar;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeScreenForFarmers extends AppCompatActivity {

    SessionManagement sessionManagement;
    private BottomNavigationView bottomNavigationView_farmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_for_farmers);

        sessionManagement = new SessionManagement(getApplicationContext());

        bottomNavigationView_farmer = findViewById(R.id.bottomNavigationMenu);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragmentForFarmers(), true, false);
        }

        bottomNavigationView_farmer.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                Fragment currentFragment = getCurrentFragment();
                boolean isNavigatingBack = false;

                if (itemId == R.id.home_menu) {
                    if (!(currentFragment instanceof HomeFragmentForFarmers)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof ProductFragmentForFarmer ||
                                        currentFragment instanceof ChatFragmentForFarmer ||
                                        currentFragment instanceof CategoryFragmentForFarmer ||
                                        currentFragment instanceof ProfileFragmentForFarmer);
                        loadFragment(new HomeFragmentForFarmers(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.product_menu) {
                    if (!(currentFragment instanceof ProductFragmentForFarmer)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof ChatFragmentForFarmer ||
                                        currentFragment instanceof CategoryFragmentForFarmer ||
                                        currentFragment instanceof ProfileFragmentForFarmer);
                        loadFragment(new ProductFragmentForFarmer(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.chat_menu) {
                    if (!(currentFragment instanceof ChatFragmentForFarmer)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof CategoryFragmentForFarmer ||
                                        currentFragment instanceof ProfileFragmentForFarmer);
                        loadFragment(new ChatFragmentForFarmer(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.category_menu) {
                    if (!(currentFragment instanceof CategoryFragmentForFarmer)) {
                        isNavigatingBack = currentFragment != null &&
                                (currentFragment instanceof ProfileFragmentForFarmer);
                        loadFragment(new CategoryFragmentForFarmer(), false, isNavigatingBack);
                    }
                } else if (itemId == R.id.profile_menu) {
                    if (!(currentFragment instanceof ProfileFragmentForFarmer)) {
                        loadFragment(new ProfileFragmentForFarmer(), false, false);
                    }
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment newFragment, boolean isAppInitialized, boolean isNavigatingBack) {
        Fragment currentFragment = getCurrentFragment();
        loadFragment(newFragment, currentFragment, isAppInitialized, isNavigatingBack);
    }

    private void loadFragment(Fragment newFragment, Fragment currentFragment, boolean isAppInitialized, boolean isNavigatingBack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (currentFragment != null) {
            if (isNavigatingBack) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_right,
                        R.anim.slide_in_right, R.anim.slide_out_left
                );
            } else {
                fragmentTransaction.setCustomAnimations(
                        R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right
                );
            }
        }

        if (isAppInitialized && fragmentManager.findFragmentById(R.id.frameLayout) == null) {
            fragmentTransaction.add(R.id.frameLayout, newFragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, newFragment);
        }

        fragmentTransaction.commit();
    }

    private Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frameLayout);
    }
}