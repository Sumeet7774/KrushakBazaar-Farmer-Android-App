package com.example.krushakbazaar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class WalkthroughScreen extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private Button backBtn, nextBtn, skipBtn, finishBtn;
    private TextView[] dots;
    private ViewPagerAdapter viewPagerAdapter;
    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_walkthrough_screen);

        sessionManagement = new SessionManagement(this);

        if(!sessionManagement.getUserId().isEmpty()) {
            Intent intent = new Intent(WalkthroughScreen.this, SplashScreen.class);
            startActivity(intent);
            finish();
        }

        backBtn = findViewById(R.id.backButton);
        nextBtn = findViewById(R.id.nextButton);
        skipBtn = findViewById(R.id.skipButton);
        finishBtn = findViewById(R.id.finishButton);
        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.indicator_layout);

        backBtn.setVisibility(View.GONE);

        backBtn.setOnClickListener(view -> {
            if (getitem(0) > 0) {
                mSlideViewPager.setCurrentItem(getitem(-1), true);
            }
        });

        nextBtn.setOnClickListener(view -> {
            if (getitem(0) < 4) {
                mSlideViewPager.setCurrentItem(getitem(1), true);
            }
        });

        skipBtn.setOnClickListener(view -> checkAndRequestPermissions());

        finishBtn.setOnClickListener(view -> checkAndRequestPermissions());

        viewPagerAdapter = new ViewPagerAdapter(this);
        mSlideViewPager.setAdapter(viewPagerAdapter);

        setUpIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    private void setUpIndicator(int position) {
        dots = new TextView[5];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.inactive, getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.active, getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

            if (position <= 0) {
                backBtn.setVisibility(View.GONE);
                nextBtn.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.VISIBLE);
                finishBtn.setVisibility(View.GONE);
            } else if (position == 4) {
                backBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
                skipBtn.setVisibility(View.GONE);
                finishBtn.setVisibility(View.VISIBLE);
            } else {
                backBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                skipBtn.setVisibility(View.VISIBLE);
                finishBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private int getitem(int i) {
        return mSlideViewPager.getCurrentItem() + i;
    }

    private boolean checkPermissions() {
        int resultSendSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int resultReadSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int resultCallPhone = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int resultInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        return resultSendSMS == PackageManager.PERMISSION_GRANTED &&
                resultReadSMS == PackageManager.PERMISSION_GRANTED &&
                resultCallPhone == PackageManager.PERMISSION_GRANTED &&
                resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultRecordAudio == PackageManager.PERMISSION_GRANTED &&
                resultInternet == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.INTERNET
        }, PERMISSION_REQUEST_CODE);
    }

    private void checkAndRequestPermissions() {
        if (checkPermissions()) {
            proceedToNextActivity();
        } else {
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean sendSMS = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readSMS = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean callPhone = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean camera = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                boolean recordAudio = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                boolean internet = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                if (sendSMS && readSMS && callPhone && camera && recordAudio && internet) {
                    proceedToNextActivity();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                        showSettingsDialog();
                    } else {
                        MotionToast.Companion.createColorToast(WalkthroughScreen.this,
                                "Permissions", "All permissions are required",
                                MotionToastStyle.INFO,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(WalkthroughScreen.this, R.font.montserrat_semibold));
                    }
                }
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WalkthroughScreen.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs all the required permissions to work properly. You can grant them in the app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
    }

    private void proceedToNextActivity() {
        Intent intent = new Intent(WalkthroughScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
