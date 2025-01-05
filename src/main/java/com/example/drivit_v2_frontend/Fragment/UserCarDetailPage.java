package com.example.drivit_v2_frontend.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.CarRental;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class UserCarDetailPage extends AppCompatActivity {

    ImageView image_car;
    TextView car_name,car_detail,rental_car_status;
    Button btn_cancel,btn_goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_car_detail_page);


        SessionManager sessionManager = new SessionManager(this);
        HashMap<String,String> userDetails = sessionManager.getUserDetailFromSession();

        // BackEnd Data
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/CAR-RENTAL-SERVICES";

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

        image_car = findViewById(R.id.image_car);
        car_name = findViewById(R.id.car_name);
        car_detail = findViewById(R.id.car_detail);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_goback = findViewById(R.id.btn_goback);
        rental_car_status = findViewById(R.id.rental_car_status);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        CarRental carRental = (CarRental) bundle.getSerializable("CarRentalItem");

        rental_car_status.setText(carRental.getStatusRental().toString());
        Glide.with(this).load(carRental.getCars().getCarImage()).into(image_car);
        car_name.setText(carRental.getCars().getCarName().toString());
        car_detail.setText(String.format("Introducing the " + carRental.getCars().getCarName() + ", a sleek and modern vehicle designed to meet your needs. " +
                "This car, a " + carRental.getCars().getCarModel() + ", combines style and performance, making it perfect for both daily use and special occasions. " +
                "With a competitive price of " + carRental.getCars().getCarPrix() + "/day , it offers great value for your journey. " +
                "The vehicle is identified by its unique license plate, " + carRental.getCars().getCarMatricul() + ", ensuring its authenticity and traceability. " +
                "Currently, this car is " + (carRental.getCars().getStatusDipo() == Status_dispo.AVAILABLE ? "available" : "not available") +
                ", so don’t miss the chance to reserve it while it’s still up for grabs."));

        // Backend endpoint for deleting a car rental
        String url = baseUrl + "/carrental/deletecarrental/" + carRental.getId_carRental();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Make a DELETE request using Volley
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                        response -> {
                            try {
                                String message = response.getString("msg");
                                int status = response.getInt("status");

                                switch (status) {
                                    case 200: // Success
                                        StyleableToast.makeText(UserCarDetailPage.this, message, R.style.mytoastdone).show();
                                        Intent intent = new Intent(UserCarDetailPage.this, DashBoard_USER.class);
                                        startActivity(intent);
                                        finish();
                                        break;

                                    case 404: // Not found
                                        StyleableToast.makeText(UserCarDetailPage.this, "CarRental not found. " + message, R.style.mytoasterror).show();
                                        break;

                                    case 400: // Bad request
                                        StyleableToast.makeText(UserCarDetailPage.this, "Bad request. " + message, R.style.mytoasterror).show();
                                        break;

                                    default: // Other unexpected statuses
                                        StyleableToast.makeText(UserCarDetailPage.this, "Unexpected error: " + message, R.style.mytoasterror).show();
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                StyleableToast.makeText(UserCarDetailPage.this, "Unexpected response from server", R.style.mytoasterror).show();
                            }
                        },
                        error -> {
                            if (error.networkResponse != null) {
                                int statusCode = error.networkResponse.statusCode;
                                String responseBody = new String(error.networkResponse.data);

                                try {
                                    JSONObject errorResponse = new JSONObject(responseBody);
                                    String message = errorResponse.getString("msg");
                                    StyleableToast.makeText(UserCarDetailPage.this, message, R.style.mytoasterror).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    StyleableToast.makeText(UserCarDetailPage.this, "Error parsing error response", R.style.mytoasterror).show();
                                }
                            } else {
                                StyleableToast.makeText(UserCarDetailPage.this, "Network error. Please check your connection.", R.style.mytoasterror).show();
                            }
                        });

                // Add the request to the Volley queue
                Volley.newRequestQueue(UserCarDetailPage.this).add(request);
            }
        });


        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserCarDetailPage.this, DashBoard_USER.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserCarDetailPage.this, DashBoard_USER.class);
        startActivity(intent);
        finish();
    }
}