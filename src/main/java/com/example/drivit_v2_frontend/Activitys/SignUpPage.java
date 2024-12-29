package com.example.drivit_v2_frontend.Activitys;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.enums.UserType;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpPage extends AppCompatActivity {

    Button btn_old_user,btn_go;
    TextInputLayout firstname,lastname,email,cin,phone,username,password;
    TextView slogan_name,logo_name;
    ImageView logo_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);

        // All Inputs
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        cin = findViewById(R.id.cin);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        // Buttons
        btn_old_user = findViewById(R.id.btn_old_user);
        btn_go = findViewById(R.id.btn_go);

        // Animations
        slogan_name = findViewById(R.id.slogan_name);
        logo_name = findViewById(R.id.logo_name);
        logo_image = findViewById(R.id.logo_image);

        // BackEnd Data
        final String port = "8888";
        final String ip_address = "192.168.11.113";
        final String baseUrl = "http://" + ip_address + ":" + port + "/USERS-SERVICES";

        // Call Login Page
        btn_old_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallSignIn();
            }
        });

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFirstName() || !validateLastName() || !validateEmail() || !validateCin() ||
                        !validatePhone() || !validateUsername() || !validatePassword()) {
                    Toast.makeText(SignUpPage.this, "Field cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    // Collect user data
                    String user_firstname = firstname.getEditText().getText().toString();
                    String user_lastname = lastname.getEditText().getText().toString();
                    String user_email = email.getEditText().getText().toString();
                    String user_cin = cin.getEditText().getText().toString();
                    String user_phone = phone.getEditText().getText().toString();
                    String user_username = username.getEditText().getText().toString();
                    String user_password = password.getEditText().getText().toString();

                    // Create a JSON object with field names matching the backend
                    JSONObject userJson = new JSONObject();
                    try {
                        userJson.put("firstName", user_firstname);
                        userJson.put("lastName", user_lastname);
                        userJson.put("email", user_email);
                        userJson.put("cin", user_cin);
                        userJson.put("phone", user_phone);
                        userJson.put("userName", user_username);
                        userJson.put("passWord", user_password);
                        userJson.put("status", UserType.USER);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SignUpPage.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Make a POST request using Volley
                    String url = baseUrl + "/users/registerUser"; // Backend endpoint

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, userJson,
                            response -> {
                                try {
                                    String message = response.getString("msg");
                                    int status = response.getInt("status");

                                    switch (status) {
                                        case 200: // Success
                                            Toast.makeText(SignUpPage.this, "User Registered Successfully!", Toast.LENGTH_LONG).show();
                                            CallSignIn(); // Redirect to sign-in or perform the desired action
                                            break;

                                        default: // Other unexpected statuses
                                            Toast.makeText(SignUpPage.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SignUpPage.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(SignUpPage.this, message, Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(SignUpPage.this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    // If no network response, it's likely a network error
                                    Toast.makeText(SignUpPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                                }
                            });
                    // Add the request to the Volley queue
                    Volley.newRequestQueue(SignUpPage.this).add(request);
                }
            }
        });
    }

    private void CallSignIn(){
        Intent intent = new Intent(SignUpPage.this, LoginPage.class);

        Pair[] pairs = new Pair[7];
        pairs[0] = new Pair<View,String>(logo_image,"logo_image");
        pairs[1] = new Pair<View,String>(logo_name,"logo_name");
        pairs[2] = new Pair<View,String>(slogan_name,"slogan_name");
        pairs[3] = new Pair<View,String>(email,"email");
        pairs[4] = new Pair<View,String>(password,"password");
        pairs[5] = new Pair<View,String>(btn_go,"btn_signIn");
        pairs[6] = new Pair<View,String>(btn_old_user,"btn_signUp");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpPage.this,pairs);

        startActivity(intent,options.toBundle());
    }

    private Boolean validateFirstName() {
        String value = firstname.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            firstname.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("[a-zA-Z\\s]+")) {
            firstname.setError("Invalid first name");
            return false;
        } else {
            firstname.setError(null);
            firstname.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateLastName() {
        String value = lastname.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            lastname.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("[a-zA-Z\\s]+")) {
            lastname.setError("Invalid last name");
            return false;
        } else {
            lastname.setError(null);
            lastname.setErrorEnabled(false);
            return true;
        }
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

    private Boolean validateCin() {
        String value = cin.getEditText().getText().toString().trim();
        // CIN regex pattern: 1 or 2 characters followed by exactly 6 digits
        String cinPattern = "^[a-zA-Z]{1,2}\\d{6}$";

        if (value.isEmpty()) {
            cin.setError("Field cannot be empty");
            return false;
        } else if (!value.matches(cinPattern)) {
            cin.setError("Invalid email CIN");
            return false;
        } else {
            cin.setError(null);
            cin.setErrorEnabled(false);
            return true;
        }
    }


    private Boolean validatePhone() {
        String value = phone.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            phone.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("\\d{10}")) {
            phone.setError("Invalid phone number (10 digits)");
            return false;
        } else {
            phone.setError(null);
            phone.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUsername() {
        String value = username.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            username.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("[a-zA-Z0-9._-]+")) {
            username.setError("Invalid username");
            return false;
        } else {
            username.setError(null);
            username.setErrorEnabled(false);
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
        } else if (!value.matches(passwordPattern)) {
            password.setError("Password is Too Weak");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}