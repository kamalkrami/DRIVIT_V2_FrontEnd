package com.example.drivit_v2_frontend.Activitys;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class LoginPage extends AppCompatActivity {

    Button btn_signUp, btn_forget_pass, btn_signIn;

    ImageView imageView;

    TextView logo_name, slogan_name;

    TextInputLayout email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_page);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        imageView = findViewById(R.id.logo_image);
        logo_name = findViewById(R.id.logo_name);
        slogan_name = findViewById(R.id.slogan_name);

        btn_forget_pass = findViewById(R.id.btn_forget_pass);
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_signIn = findViewById(R.id.btn_signIn);

        // BackEnd Data
        final String port = "8888";
        final String ip_address = "192.168.11.113";
        final String baseUrl = "http://" + ip_address + ":" + port + "/USERS-SERVICES";

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, SignUpPage.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(imageView, "logo_image");
                pairs[1] = new Pair<View, String>(logo_name, "logo_name");
                pairs[2] = new Pair<View, String>(slogan_name, "slogan_name");
                pairs[3] = new Pair<View, String>(email, "email");
                pairs[4] = new Pair<View, String>(password, "password");
                pairs[5] = new Pair<View, String>(btn_signUp, "btn_signIn");
                pairs[6] = new Pair<View, String>(btn_signIn, "btn_signUp");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, pairs);

                startActivity(intent, options.toBundle());
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail() || !validatePassword()) {
                    //Toast.makeText(LoginPage.this, "Field cannot be empty", Toast.LENGTH_LONG).show();
                    StyleableToast.makeText(LoginPage.this, "Field cannot be empty", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                } else {
                    // Collect user data
                    String user_email = email.getEditText().getText().toString();
                    String user_password = password.getEditText().getText().toString();

                    // Create a JSON object with field names matching the backend
                    JSONObject userJson = new JSONObject();
                    try {
                        userJson.put("email", user_email);
                        userJson.put("passWord", user_password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Toast.makeText(LoginPage.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(LoginPage.this, "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        return;
                    }
                    // Make a POST request using Volley
                    String url = baseUrl + "/users/authUser"; // Backend endpoint

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, userJson,
                            response -> {
                                try {
                                    String message = response.getString("msg");
                                    int status = response.getInt("status");

                                    switch (status) {
                                        case 200: // Success
                                            // Getting User Data to Stocked In Session
                                            JSONObject userObject = response.getJSONObject("user");
                                            String userID = userObject.getString("id_user");
                                            String firstName = userObject.getString("firstName");
                                            String lastName = userObject.getString("lastName");
                                            String email = userObject.getString("email");
                                            String cin = userObject.getString("cin");
                                            String phone = userObject.getString("phone");
                                            String userName = userObject.getString("userName");
                                            String passWord = userObject.getString("passWord");
                                            String status_user = userObject.getString("status");

                                            // Session
                                            SessionManager sessionManager = new SessionManager(LoginPage.this);
                                            sessionManager.createLoginSession(userID, firstName, lastName, email, cin, phone, userName, passWord, status_user);

                                            //Toast.makeText(LoginPage.this, "Welcome, " + firstName +" "+ lastName + "!", Toast.LENGTH_LONG).show();
                                            StyleableToast.makeText(LoginPage.this, "Welcome, " + firstName + " " + lastName + "!", Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                                            // Redirect to sign-in or perform the desired action
                                            if (status_user.equals("USER")) {
                                                startActivity(new Intent(getApplicationContext(), DashBoard_USER.class));
                                            } else if (status_user.equals("SUPPLIER")) {
                                                System.out.println("SUPPLIER");
                                            } else if (status_user.equals("ADMIN")) {
                                                System.out.println("ADMIN");
                                            } else {
                                                //Toast.makeText(LoginPage.this, "Failed To Auth User", Toast.LENGTH_LONG).show();
                                                StyleableToast.makeText(LoginPage.this, "Failed To Auth User", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                            }
                                            break;

                                        default: // Other unexpected statuses
                                            //Toast.makeText(LoginPage.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                            StyleableToast.makeText(LoginPage.this, "Error: " + message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Toast.makeText(LoginPage.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(LoginPage.this, "Unexpected response from server", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            },
                            error -> {
                                // Handle network errors
                                if (error.networkResponse != null) {
                                    int statusCode = error.networkResponse.statusCode;
                                    String responseBody = new String(error.networkResponse.data);// Convert byte array to string

                                    try {
                                        // Parse the responseBody as a JSON object to extract msg
                                        JSONObject errorResponse = new JSONObject(responseBody);
                                        String message = errorResponse.getString("msg"); // Get the "msg" field from the error response

                                        // Show the message in a Toast
                                        //Toast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT).show();
                                        StyleableToast.makeText(LoginPage.this, message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //Toast.makeText(LoginPage.this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                                        StyleableToast.makeText(LoginPage.this, "Error parsing error response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                    }

                                } else {
                                    // If no network response, it's likely a network error
                                    //Toast.makeText(LoginPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(LoginPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            });
                    // Add the request to the Volley queue
                    Volley.newRequestQueue(LoginPage.this).add(request);
                }
            }
        });


    }

    private Boolean validateEmail() {
        String value = email.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            email.setError("Invalid email address");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String value = password.getEditText().getText().toString().trim();
        // Password regex pattern
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if (value.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}