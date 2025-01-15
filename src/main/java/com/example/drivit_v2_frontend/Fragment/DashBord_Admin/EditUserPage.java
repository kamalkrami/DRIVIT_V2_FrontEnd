package com.example.drivit_v2_frontend.Fragment.DashBord_Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.Activitys.LoginPage;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_ADMIN;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.Fragment.DashBord_User.UserCarDetailPage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class EditUserPage extends AppCompatActivity {

    TextView full_name, full_name_below;
    TextInputLayout firstName, lastName, email, phone, username, password, cin,role_select;
    Button btn_update,btn_go_back;

    MaterialAutoCompleteTextView materialAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user_page);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Users user = (Users) bundle.getSerializable("users");

        full_name = findViewById(R.id.full_name);
        full_name_below = findViewById(R.id.full_name_below);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        cin = findViewById(R.id.cin);
        role_select = findViewById(R.id.role_select);
        btn_update = findViewById(R.id.btn_update);
        btn_go_back = findViewById(R.id.btn_go_back);

        full_name.setText(user.getFirstName() +" "+user.getLastName());
        full_name_below.setText(user.getFirstName() +" "+user.getLastName());

        firstName.getEditText().setText(user.getFirstName());
        lastName.getEditText().setText(user.getLastName());
        email.getEditText().setText(user.getEmail());
        phone.getEditText().setText(user.getPhone());
        username.getEditText().setText(user.getUserName());
        password.getEditText().setText(user.getPassWord());
        cin.getEditText().setText(user.getCin());

        // BackEnd Data
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/USERS-SERVICES";

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFirstName() || !validateLastName() || !validateEmail() || !validatePhone() || !validateUsername() || !validateUserRole()) {
                    StyleableToast.makeText(EditUserPage.this, "Field cannot be empty", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                } else {

                    // Create a JSON object with field names matching the backend
                    JSONObject userJson = new JSONObject();
                    try {
                        userJson.put("id_user", user.getUserID());
                        userJson.put("firstName", firstName.getEditText().getText().toString());
                        userJson.put("lastName", lastName.getEditText().getText().toString());
                        userJson.put("email", email.getEditText().getText().toString());
                        userJson.put("cin", cin.getEditText().getText().toString());
                        userJson.put("phone", phone.getEditText().getText().toString());
                        userJson.put("userName", username.getEditText().getText().toString());
                        userJson.put("passWord", password.getEditText().getText().toString());
                        userJson.put("status", role_select.getEditText().getText().toString().toUpperCase());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        StyleableToast.makeText(EditUserPage.this, "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        return;
                    }
                    // Make a POST request using Volley
                    String url = baseUrl + "/users/updateuser"; // Backend endpoint

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, userJson,
                            response -> {
                                try {
                                    String message = response.getString("msg");
                                    int status = response.getInt("status");

                                    switch (status) {
                                        case 200: // Success
                                            StyleableToast.makeText(EditUserPage.this, "User updated successfully", Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                                            // Redirect to sign-in or perform the desired action
                                            startActivity(new Intent(EditUserPage.this, DashBoard_ADMIN.class));
                                        default: // Other unexpected statuses
                                            StyleableToast.makeText(EditUserPage.this, "Error: " + message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    StyleableToast.makeText(EditUserPage.this, "Unexpected response from server", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
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
                                        String message = errorResponse.getString("msg");

                                        StyleableToast.makeText(EditUserPage.this, message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        StyleableToast.makeText(EditUserPage.this, "Error parsing error response" + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                    }

                                } else {
                                    StyleableToast.makeText(EditUserPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            });
                    Volley.newRequestQueue(EditUserPage.this).add(request);
                }
            }
        });

        btn_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_Go_Back = new Intent(EditUserPage.this, DashBoard_ADMIN.class);
                startActivity(intent_Go_Back);
                finish();
            }
        });

    }
    private Boolean validateFirstName() {
        String value = firstName.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            firstName.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("[a-zA-Z\\s]+")) {
            firstName.setError("Invalid first name");
            return false;
        } else {
            firstName.setError(null);
            firstName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateLastName() {
        String value = lastName.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            lastName.setError("Field cannot be empty");
            return false;
        } else if (!value.matches("[a-zA-Z\\s]+")) {
            lastName.setError("Invalid last name");
            return false;
        } else {
            lastName.setError(null);
            lastName.setErrorEnabled(false);
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

    private Boolean validateUserRole() {
        String value = role_select.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            role_select.setError("Field cannot be empty");
            return false;
        } else {
            role_select.setError(null);
            role_select.setErrorEnabled(false);
            return true;
        }
    }
}