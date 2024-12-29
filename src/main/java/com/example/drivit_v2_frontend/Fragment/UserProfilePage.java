package com.example.drivit_v2_frontend.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.Activitys.LoginPage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;


public class UserProfilePage extends Fragment {

    TextView full_name,full_name_below;
    TextInputLayout firstName,lastName,email,phone,username,password;
    Button btn_update;

    public UserProfilePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  rootView = inflater.inflate(R.layout.fragment_user_profile_, container, false);

        SessionManager sessionManager = new SessionManager(getActivity());
        HashMap<String,String> userDetails = sessionManager.getUserDetailFromSession();

        String _userID = userDetails.get(SessionManager.KEY_ID);
        String _firstName = userDetails.get(SessionManager.KEY_FIRSTNAME);
        String _lastName = userDetails.get(SessionManager.KEY_LASTNAME);
        String _email = userDetails.get(SessionManager.KEY_EMAIL);
        String _cin = userDetails.get(SessionManager.KEY_CIN);
        String _phone = userDetails.get(SessionManager.KEY_PHONE);
        String _userName = userDetails.get(SessionManager.KEY_USERNAME);
        String _passWord = userDetails.get(SessionManager.KEY_PASSWORD);
        String _status_user = userDetails.get(SessionManager.KEY_STATUS);

        full_name = rootView.findViewById(R.id.full_name);
        full_name_below = rootView.findViewById(R.id.full_name_below);
        firstName = rootView.findViewById(R.id.firstName);
        lastName = rootView.findViewById(R.id.lastName);
        email = rootView.findViewById(R.id.email);
        phone = rootView.findViewById(R.id.phone);
        username = rootView.findViewById(R.id.username);
        password = rootView.findViewById(R.id.password);
        btn_update = rootView.findViewById(R.id.btn_update);

        // BackEnd Data
        final String port = "8888";
        final String ip_address = "192.168.11.113";
        final String baseUrl = "http://" + ip_address + ":" + port + "/USERS-SERVICES";

        full_name.setText(_firstName+" "+_lastName);
        full_name_below.setText(_firstName+" "+_lastName);
        firstName.getEditText().setText(_firstName);
        lastName.getEditText().setText(_lastName);
        email.getEditText().setText(_email);
        phone.getEditText().setText(_phone);
        username.getEditText().setText(_userName);
        password.getEditText().setText(_passWord);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFirstName() || !validateLastName() || !validateEmail() || !validatePhone() || !validateUsername()) {
                    //Toast.makeText(LoginPage.this, "Field cannot be empty", Toast.LENGTH_LONG).show();
                    StyleableToast.makeText(requireActivity(), "Field cannot be empty", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                } else {

                    // Create a JSON object with field names matching the backend
                    JSONObject userJson = new JSONObject();
                    try {
                        userJson.put("id_user", _userID);
                        userJson.put("firstName", firstName.getEditText().getText().toString());
                        userJson.put("lastName", lastName.getEditText().getText().toString());
                        userJson.put("email", email.getEditText().getText().toString());
                        userJson.put("cin", _cin);
                        userJson.put("phone", phone.getEditText().getText().toString());
                        userJson.put("userName", username.getEditText().getText().toString());
                        userJson.put("passWord", password.getEditText().getText().toString());
                        userJson.put("status", _status_user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Toast.makeText(LoginPage.this, "Error creating JSON", Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(requireActivity(), "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
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
                                            //Toast.makeText(LoginPage.this, "Welcome, " + firstName +" "+ lastName + "!", Toast.LENGTH_LONG).show();
                                            StyleableToast.makeText(requireActivity(), "User updated successfully", Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                                            // Redirect to sign-in or perform the desired action
                                            if (_status_user.equals("USER")) {
                                                startActivity(new Intent(requireActivity(), LoginPage.class));
                                                sessionManager.logoutUserFromSession();
                                            } else if (_status_user.equals("SUPPLIER")) {
                                                System.out.println("SUPPLIER");
                                            } else if (_status_user.equals("ADMIN")) {
                                                System.out.println("ADMIN");
                                            } else {
                                                //Toast.makeText(LoginPage.this, "Failed To Auth User", Toast.LENGTH_LONG).show();
                                                StyleableToast.makeText(requireActivity(), "Failed To Update User", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                            }
                                            break;

                                        default: // Other unexpected statuses
                                            //Toast.makeText(LoginPage.this, "Error: " + message, Toast.LENGTH_LONG).show();
                                            StyleableToast.makeText(requireActivity(), "Error: " + message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Toast.makeText(LoginPage.this, "Unexpected response from server", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(requireActivity(), "Unexpected response from server", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
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
                                        StyleableToast.makeText(requireActivity(), message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        //Toast.makeText(LoginPage.this, "Error parsing error response", Toast.LENGTH_SHORT).show();
                                        StyleableToast.makeText(requireActivity(), "Error parsing error response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                    }

                                } else {
                                    // If no network response, it's likely a network error
                                    //Toast.makeText(LoginPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                                    StyleableToast.makeText(requireActivity(), "Network error. Please check your connection.", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            });
                    // Add the request to the Volley queue
                    Volley.newRequestQueue(requireActivity()).add(request);
                }
            }
        });

        return rootView;
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
}