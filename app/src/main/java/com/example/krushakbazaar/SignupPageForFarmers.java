package com.example.krushakbazaar;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class SignupPageForFarmers extends AppCompatActivity {

    private Button backButtonSignupPage_farmers, sign_up_button_for_farmers;
    private EditText username_et_farmers, emailid_et_farmers, password_et_farmers, phonenumber_et_farmers, address_et_farmers;
    private FloatingActionButton addProfilePhoto_farmers;
    private ImageButton detect_location_farmeers;

    private final String PHONE_PREFIX = "+91";
    private boolean isPasswordVisible = false;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Uri photoUri;
    private ShapeableImageView profilePhoto_farmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page_for_farmers);

        backButtonSignupPage_farmers = findViewById(R.id.back_button_signuppage_for_farmers);
        sign_up_button_for_farmers = findViewById(R.id.signup_button_signuppage_for_farmers);
        username_et_farmers = findViewById(R.id.username_edittext_for_farmers);
        emailid_et_farmers = findViewById(R.id.email_edittext_for_farmers);
        password_et_farmers = findViewById(R.id.password_edittext_for_farmers);
        phonenumber_et_farmers = findViewById(R.id.phonenumber_edittext_for_farmers);
        address_et_farmers = findViewById(R.id.address_edittext_for_farmers);
        addProfilePhoto_farmers = findViewById(R.id.profilephoto_farmer_add_button);
        detect_location_farmeers = findViewById(R.id.detectlocation_signup_for_farmers);
        profilePhoto_farmer = findViewById(R.id.signuppage_profilephoto_farmer);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        InputFilter noSpacesFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().contains(" ")) {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Spaces are not allowed.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                    return source.toString().replace(" ", "");
                }
                return null;
            }
        };

        password_et_farmers.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(12),
                noSpacesFilter
        });

        username_et_farmers.setFilters(new InputFilter[]{noSpacesFilter, new InputFilter.LengthFilter(12)});
        emailid_et_farmers.setFilters(new InputFilter[]{noSpacesFilter});
        password_et_farmers.setFilters(new InputFilter[]{noSpacesFilter});
        phonenumber_et_farmers.setFilters(new InputFilter[]{noSpacesFilter});

        phonenumber_et_farmers.setText(PHONE_PREFIX);
        phonenumber_et_farmers.setSelection(PHONE_PREFIX.length());
        phonenumber_et_farmers.addTextChangedListener(new TextWatcher() {
            boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) return;
                isFormatting = true;

                if (!s.toString().startsWith(PHONE_PREFIX)) {
                    phonenumber_et_farmers.setText(PHONE_PREFIX);
                    phonenumber_et_farmers.setSelection(PHONE_PREFIX.length());
                }
                isFormatting = false;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        password_et_farmers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_et_farmers.getRight() - password_et_farmers.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_et_farmers);
                        return true;
                    }
                }
                return false;
            }
        });

        backButtonSignupPage_farmers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupPageForFarmers.this, SignUpPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        detect_location_farmeers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SignupPageForFarmers.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignupPageForFarmers.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getLastLocation();
                }
            }
        });

        addProfilePhoto_farmers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SignupPageForFarmers.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignupPageForFarmers.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    openCamera();
                }
            }
        });

        sign_up_button_for_farmers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt = username_et_farmers.getText().toString().trim();
                String email_txt = emailid_et_farmers.getText().toString().trim();
                String password_txt = password_et_farmers.getText().toString().trim();
                String phonenumber_txt = phonenumber_et_farmers.getText().toString().trim();
                String address_txt = address_et_farmers.getText().toString();

                if (TextUtils.isEmpty(username_txt) || TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt) || TextUtils.isEmpty(phonenumber_txt) || TextUtils.isEmpty(address_txt))
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Please provide all of your credentials.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else if (!isValidUsername(username_txt))
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Username must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else if (!isValidEmail(email_txt))
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Please enter a valid email address",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else if (!isValidPassword(password_txt))
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Password must be 8 chars with A-Z,0-9 and symbols",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else if (!isValidPhoneNumber(phonenumber_txt))
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Enter a 10-digit phone number.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else if (!isValidAddress(address_txt))
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Please enter a valid address.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else if (profilePhoto_farmer.getDrawable() == null)
                {
                    MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                            "Error", "Please select a profile photo.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                }
                else
                {
                    registerUser(username_txt, email_txt, password_txt, phonenumber_txt, address_txt);
                }
            }
        });
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void registerUser(String username, String email, String password, String phone_number, String address) {
        Drawable drawable = profilePhoto_farmer.getDrawable();
        Bitmap profilePhoto = null;

        if (drawable instanceof BitmapDrawable) {
            profilePhoto = ((BitmapDrawable) drawable).getBitmap();
        } else {
            MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                    "Error", "Profile photo is not a valid image.",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
            return;
        }

        String profilePhotoBase64 = convertBitmapToBase64(profilePhoto);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.register_url, response -> {
            Log.d("REQUEST", "Username: " + username);
            Log.d("REQUEST", "Email: " + email);
            Log.d("REQUEST", "Password: " + password);
            Log.d("REQUEST", "Phone Number: " + phone_number);
            Log.d("REQUEST", "Address: " + address);
            Log.d("REQUEST", "Profile Photo: " + profilePhotoBase64);

            if (response.contains("registration successfull")) {
                showRegistrationSuccessDialog();
            } else if (response.contains("User data already exists")) {
                MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                        "Error", "User with those credentials already Exists!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
            } else {
                MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                        "Registration Failed", "Registration Failed!",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMessage = volleyError.getMessage();
                if (errorMessage == null) {
                    errorMessage = "Unknown error occurred";
                }

                MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                        "Internet Error", "Please check your internet connection",
                        MotionToastStyle.INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));

                Log.d("VOLLEY", errorMessage);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email_id", email);
                params.put("password", password);
                params.put("phone_number", phone_number);
                params.put("address", address);
                params.put("profile_photo", profilePhotoBase64);
                params.put("user_type", "farmer");

                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showRegistrationSuccessDialog()
    {
        Dialog successful_registration_dialogBox = new Dialog(SignupPageForFarmers.this);
        successful_registration_dialogBox.setContentView(R.layout.custom_success_dialogbox);
        Button dialogBox_ok_button = successful_registration_dialogBox.findViewById(R.id.okbutton_successDialogBox);
        dialogBox_ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                successful_registration_dialogBox.dismiss();
                Intent intent = new Intent(SignupPageForFarmers.this, IndexPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        successful_registration_dialogBox.show();

        MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                "Success", "Press OK to redirect to the Index Page for login.",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                if (photoUri != null) {
                    // Decode bitmap with inSampleSize set to ensure it fits the view
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri), null, options);

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, profilePhoto_farmer.getWidth(), profilePhoto_farmer.getHeight());

                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(photoUri), null, options);

                    if (bitmap != null) {
                        // Resize the bitmap to fit the ImageView while maintaining aspect ratio
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, profilePhoto_farmer.getWidth(), profilePhoto_farmer.getHeight(), true);
                        profilePhoto_farmer.setImageBitmap(resizedBitmap);
                    } else {
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error: No image file", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(SignupPageForFarmers.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String addressText = address.getAddressLine(0);
                                address_et_farmers.setText(addressText);
                            } else {
                                MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                                        "Error", "Failed to detect location.",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(SignupPageForFarmers.this, "Error Retrieving Location", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                                "Error", "Failed to get location.",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
                    }
                }
            });
        } else {
            MotionToast.Companion.createColorToast(SignupPageForFarmers.this,
                    "Error", "Location permission is not granted.",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(SignupPageForFarmers.this, R.font.montserrat_semibold));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                MotionToast.Companion.createColorToast(this,
                        "Error", "Location permission was denied.",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this, R.font.montserrat_semibold));
            }
        }
    }

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z]+");
    }

    private boolean isValidEmail(CharSequence target) {
        String emailPattern = "^[a-z][a-z0-9]*@gmail\\.com$";
        return (!TextUtils.isEmpty(target) && target.toString().matches(emailPattern));
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.startsWith(PHONE_PREFIX) && phoneNumber.length() == (PHONE_PREFIX.length() + 10);
    }

    private boolean isValidAddress(String address) {
        return !TextUtils.isEmpty(address) && address.length() > 5;
    }


    private void togglePasswordVisibility(EditText password_et_farmers) {
        if (isPasswordVisible) {
            password_et_farmers.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password_et_farmers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.password_hide, 0);
        } else {
            password_et_farmers.setInputType(InputType.TYPE_CLASS_TEXT);
            password_et_farmers.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon, 0, R.drawable.password_show, 0);
        }
        password_et_farmers.setSelection(password_et_farmers.length());
        isPasswordVisible = !isPasswordVisible;

        password_et_farmers.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_light));
    }
}
