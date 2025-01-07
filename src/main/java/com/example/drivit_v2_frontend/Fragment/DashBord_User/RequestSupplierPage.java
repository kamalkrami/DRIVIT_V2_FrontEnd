package com.example.drivit_v2_frontend.Fragment.DashBord_User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_Request_Supplier;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.Users;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class RequestSupplierPage extends Fragment {

    Button btn_request_supplier,btn_goback;

    public RequestSupplierPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_request_supplier_page, container, false);

        SessionManager sessionManager = new SessionManager(getActivity());
        HashMap<String,String> userDetails = sessionManager.getUserDetailFromSession();

        String _userID = userDetails.get(SessionManager.KEY_ID);
        String _firstName = userDetails.get(SessionManager.KEY_FIRSTNAME);
        String _lastName = userDetails.get(SessionManager.KEY_LASTNAME);
        String _userName = userDetails.get(SessionManager.KEY_USERNAME);
        String _passWord = userDetails.get(SessionManager.KEY_PASSWORD);
        String _cin = userDetails.get(SessionManager.KEY_CIN);
        String _email = userDetails.get(SessionManager.KEY_EMAIL);
        String _phone = userDetails.get(SessionManager.KEY_PHONE);
        String _status_user = userDetails.get(SessionManager.KEY_STATUS);

        Users users = new Users(_userID,_firstName,_lastName,_userName,_passWord,_cin,_email,_phone, UserType.valueOf(_status_user));

        btn_request_supplier = rootview.findViewById(R.id.btn_request_supplier);

        // BackEnd
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/REQUEST-SUPPLIER-SERVICES";

        String url = baseUrl + "/requestsupplier/addRequestsupplier";

        btn_request_supplier.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                JSONObject requestBody = new JSONObject();
                try {
                    // Add necessary fields to the request body (example fields, adjust as per your backend)
                    Gson gson = new Gson(); // Use Gson to convert objects to JSON strings

                    requestBody.put("users",new JSONObject(gson.toJson(users)));
                    requestBody.put("id_user", _userID);
                    requestBody.put("requestDate", LocalDate.now().toString());
                    requestBody.put("status", Status_Request_Supplier.PENDING);
                    // Add other fields as required
                } catch (JSONException e) {
                    e.printStackTrace();
                    StyleableToast.makeText(requireActivity(), "Error creating request data", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    return;
                }
                // Create a POST request using Volley
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                        response -> {
                            // Handle successful response
                            try {
                                String msg = response.getString("msg");
                                int status = response.getInt("status");
                                // Check status code and display appropriate message
                                if (status == 200) {
                                    StyleableToast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                                    startActivity(new Intent(requireActivity(),DashBoard_USER.class));
                                } else {
                                    StyleableToast.makeText(requireActivity(), "Unexpected response: " + msg, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                StyleableToast.makeText(requireActivity(), "Error parsing response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                            }
                        },
                        error -> {
                            // Handle error response
                            StyleableToast.makeText(requireActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }
                );
                // Add the request to the Volley request queue
                Volley.newRequestQueue(requireActivity()).add(jsonObjectRequest);
            }
        });

        btn_goback = rootview.findViewById(R.id.btn_goback);

        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), DashBoard_USER.class);
                startActivity(intent);
            }
        });

        return rootview;
    }
}