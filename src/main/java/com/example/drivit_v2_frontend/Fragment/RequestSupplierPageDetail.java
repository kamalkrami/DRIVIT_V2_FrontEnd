package com.example.drivit_v2_frontend.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.Activitys.LoginPage;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.models.RequestSupplier;

import org.json.JSONException;
import org.json.JSONObject;

import io.github.muddz.styleabletoast.StyleableToast;

public class RequestSupplierPageDetail extends Fragment {

    Button btn_cancel_request_supplier, btn_goback;
    TextView requist_status;

    public RequestSupplierPageDetail() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_request_supplier_page_detail, container, false);

        RequestSupplier requestSupplier = (RequestSupplier) getArguments().getSerializable("requestSupplier");

        btn_goback = rootview.findViewById(R.id.btn_goback);
        btn_cancel_request_supplier = rootview.findViewById(R.id.btn_cancel_request_supplier);

        requist_status = rootview.findViewById(R.id.requist_status);
        requist_status.setText(String.valueOf(requestSupplier.getStatus()));

        // BackEnd
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/REQUEST-SUPPLIER-SERVICES";

        String url = baseUrl + "/requestsupplier/deleterequestsupplier/" + requestSupplier.getId_request();

        btn_cancel_request_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                        response -> {
                            try {
                                String msg = response.getString("msg");
                                int status = response.getInt("status");
                                // Check status code and display appropriate message
                                if (status == 200) {
                                    StyleableToast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                                    startActivity(new Intent(requireActivity(), DashBoard_USER.class));
                                } else {
                                    StyleableToast.makeText(requireActivity(), "Unexpected response: " + msg, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                StyleableToast.makeText(requireActivity(), "Error parsing response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                            }
                        }, error -> {
                    // Handle network errors
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody = new String(error.networkResponse.data);// Convert byte array to string

                        try {
                            // Parse the responseBody as a JSON object to extract msg
                            JSONObject errorResponse = new JSONObject(responseBody);
                            String message = errorResponse.getString("msg"); // Get the "msg" field from the error response

                            // Show the message in a Toast
                            StyleableToast.makeText(requireActivity(), message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            StyleableToast.makeText(requireActivity(), "Error parsing error response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }

                    } else {
                        // If no network response, it's likely a network error
                        StyleableToast.makeText(requireActivity(), "Network error. Please check your connection.", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    }
                });
                Volley.newRequestQueue(requireActivity()).add(request);
            }
        });

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