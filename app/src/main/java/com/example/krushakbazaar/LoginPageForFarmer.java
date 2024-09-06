package com.example.krushakbazaar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class LoginPageForFarmer extends AppCompatActivity {

    private EditText username_editText, phonenumber_editText, password_editText;
    private Button backButtonLoginpage_farmer, login_button_farmer;
    private boolean isPasswordVisible = false;
    private SessionManagement sessionManagement;

    //private final String PHONE_PREFIX = "+91";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page_for_farmer);

        backButtonLoginpage_farmer = findViewById(R.id.back_button_loginpage_for_farmers);
        username_editText = findViewById(R.id.username_login_edittext_for_farmers);
        password_editText = findViewById(R.id.password_login_edittext_for_farmers);
        login_button_farmer = findViewById(R.id.login_button_loginpage_for_farmers);

        sessionManagement = new SessionManagement(this);

        InputFilter noSpacesFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().contains(" ")) {
                    MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                            "Error", "Spaces are not allowed.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                    return source.toString().replace(" ", "");
                }
                return null;
            }
        };

        username_editText.setFilters(new InputFilter[]{noSpacesFilter, new InputFilter.LengthFilter(12)});
        password_editText.setFilters(new InputFilter[]{noSpacesFilter});

        password_editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password_editText.getRight() - password_editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility(password_editText);
                        return true;
                    }
                }
                return false;
            }
        });

        backButtonLoginpage_farmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPageForFarmer.this, LoginPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        login_button_farmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt = username_editText.getText().toString().trim();
                String password_txt = password_editText.getText().toString().trim();
                String user_type = "farmer";

                if (TextUtils.isEmpty(username_txt) || TextUtils.isEmpty(password_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                            "Error", "Please provide all of your credentials.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                }
                else if (!isValidUsername(username_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                            "Error", "Username must contain only letters.",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                }
                else if(!isValidPassword(password_txt))
                {
                    MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                            "Error", "Password must be 8 chars with A-Z,0-9 and symbols",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                }
                else
                {
                    loginUser(username_txt,password_txt,user_type);
                }
            }
        });
    }

    private boolean isValidUsername(String username) {
        return username.matches("[a-zA-Z]+");
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        String passwordPattern = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(passwordPattern);
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

    public void loginUser(final String username, final String password, final String userType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String jsonResponse = extractJsonResponse(response);

                        Log.d("retrievedResponse", "Response: " + jsonResponse);

                        try {
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String status = jsonObject.optString("status");
                            String message = jsonObject.optString("message");

                            if ("found".equals(status)) {
                                retrieveUserId(username);
                                retrieveEmailid(username);
                                retrievePhonenumber(username);

                                sessionManagement.setUsername(username);
                                sessionManagement.setUserType("farmer");

                                MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                        "Success", "Login Successful",
                                        MotionToastStyle.SUCCESS,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));

                                Intent intent = new Intent(LoginPageForFarmer.this, HomeScreenForFarmers.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else if ("not found".equals(status)) {
                                MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                        "Error", "No such user found with those credentials",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                            } else {
                                MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                        "Error", "Some Error Occurred",
                                        MotionToastStyle.ERROR,
                                        MotionToast.GRAVITY_BOTTOM,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                            }
                        } catch (JSONException e) {
                            MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                    "Error", "Unexpected response format",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                            Log.d("JSON_ERROR", e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPageForFarmer.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                        Log.d("VOLLEY", error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("user_type", userType);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private String extractJsonResponse(String response) {
        try {
            int startIndex = response.indexOf("{");
            int endIndex = response.lastIndexOf("}") + 1;
            return response.substring(startIndex, endIndex);
        } catch (Exception e) {
            Log.d("ResponseExtraction", "Failed to extract JSON from response: " + response);
            return "{}"; // Return empty JSON object if extraction fails
        }
    }

    private void retrieveUserId(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.getUserid_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retrieveUserId", "Response: " + response);

                        String userId = extractUserIdFromResponse(response);

                        if (userId != null) {
                            sessionManagement.setUserId(userId);
                            Log.d("Session Userid", "User Id: " + userId);
                        }
                        else
                        {
                            MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                    "Error", "User not found.",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPageForFarmer.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String extractUserIdFromResponse(String response) {
        String pattern = "connection successfull(\\w+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find()) {
            return m.group(1);
        }

        Log.d("extractUserIdFromResponse", "Failed to extract user ID from response: " + response);
        return null;
    }

    private void retrieveEmailid(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.getEmailId_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retrieveEmailid", "Response: " + response);

                        String emailId = extractEmailidFromResponse(response);

                        if (emailId != null) {
                            sessionManagement.setEmailId(emailId);
                            Log.d("Session Emailid", "Email id: " + emailId);
                        }
                        else
                        {
                            MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                    "Error", "User not found.",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPageForFarmer.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String extractEmailidFromResponse(String response) {
        String pattern = "connection successfull([\\w\\-.]+@[\\w\\-.]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find()) {
            return m.group(1);
        }

        Log.d("extractEmailidFromResponse", "Failed to extract emailid from response: " + response);
        return null;
    }

    private void retrievePhonenumber(final String username) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiEndpoints.getPhonenumber_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("retrievePhonenumber", "Response: " + response);

                        String phonenumber = extractPhonenumberFromResponse(response);

                        if (phonenumber != null) {
                            sessionManagement.setPhoneNumber(phonenumber);
                            Log.d("Session Phonenumber", "Phonenumber: " + phonenumber);
                        }
                        else
                        {
                            MotionToast.Companion.createColorToast(LoginPageForFarmer.this,
                                    "Error", "User not found.",
                                    MotionToastStyle.ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(LoginPageForFarmer.this, R.font.montserrat_semibold));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginPageForFarmer.this, "Error Occured", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String extractPhonenumberFromResponse(String response) {
        String pattern = "connection successfull([\\w\\-.]+@[\\w\\-.]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(response);

        if (m.find()) {
            return m.group(1);
        }

        Log.d("extractPhonenumberFromResponse", "Failed to extract phonenumber from response: " + response);
        return null;
    }


}