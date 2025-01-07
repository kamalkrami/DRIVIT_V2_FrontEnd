package com.example.drivit_v2_frontend.Fragment.DashBord_User;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.Status_rental;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class DetailCarPage extends AppCompatActivity {
    ImageView image_car;
    TextView car_name,car_detail,car_status;
    Button btn_rental,btn_goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_car_page);

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

        Users users = new Users(_userID,_firstName,_lastName,_userName,_passWord,_cin,_email,_phone,UserType.valueOf(_status_user));

        image_car = findViewById(R.id.image_car);
        car_name = findViewById(R.id.car_name);
        car_detail = findViewById(R.id.car_detail);
        btn_rental = findViewById(R.id.btn_rental);
        btn_goback = findViewById(R.id.btn_goback);
        car_status = findViewById(R.id.car_status);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Cars car = (Cars) bundle.getSerializable("carsItem");

        car_status.setText(String.valueOf(car.getStatusDipo()));
        Glide.with(this).load(car.getCarImage()).into(image_car);
        car_name.setText(car.getCarName().toString());
        car_detail.setText(String.format("Introducing the " + car.getCarName() + ", a sleek and modern vehicle designed to meet your needs. " +
                "This car, a " + car.getCarModel() + ", combines style and performance, making it perfect for both daily use and special occasions. " +
                "With a competitive price of " + car.getCarPrix() + "/day , it offers great value for your journey. " +
                "The vehicle is identified by its unique license plate, " + car.getCarMatricul() + ", ensuring its authenticity and traceability. " +
                "Currently, this car is " + (car.getStatusDipo() == Status_dispo.AVAILABLE ? "available" : "not available") +
                ", so don’t miss the chance to reserve it while it’s still up for grabs."));


        btn_rental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a JSON object with field names matching the backend
                JSONObject userJson = new JSONObject();
                Gson gson = new Gson(); // Use Gson to convert objects to JSON strings
                try {
                    // Serialize car and users objects to JSON
                    userJson.put("cars", new JSONObject(gson.toJson(car)));
                    userJson.put("id_car", car.getId_car());
                    userJson.put("users", new JSONObject(gson.toJson(users)));
                    userJson.put("id_user", users.getUserID());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        userJson.put("rentalTime", LocalDate.now().toString());
                    }
                    userJson.put("statusRental", Status_rental.PENDING.name());
                } catch (JSONException e) {
                    e.printStackTrace();
                    StyleableToast.makeText(DetailCarPage.this, "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    return;
                }

                // Make a POST request using Volley
                String url = baseUrl + "/carrental/addCarrental"; // Backend endpoint

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, userJson,
                        response -> {
                            try {
                                String message = response.getString("msg");
                                int status = response.getInt("status");

                                switch (status) {
                                    case 200: // Success
                                        StyleableToast.makeText(DetailCarPage.this, message, Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                                        Intent intent = new Intent(DetailCarPage.this, DashBoard_USER.class);
                                        startActivity(intent);
                                        finish();
                                        break;

                                    default: // Other unexpected statuses
                                        StyleableToast.makeText(DetailCarPage.this, "Error: " + message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                StyleableToast.makeText(DetailCarPage.this, "Unexpected response from server", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                            }
                        },
                        error -> {
                            if (error.networkResponse != null) {
                                int statusCode = error.networkResponse.statusCode;
                                String responseBody = new String(error.networkResponse.data);

                                try {
                                    JSONObject errorResponse = new JSONObject(responseBody);
                                    String message = errorResponse.getString("msg");
                                    StyleableToast.makeText(DetailCarPage.this, message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    StyleableToast.makeText(DetailCarPage.this, "Error parsing error response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            } else {
                                StyleableToast.makeText(DetailCarPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                            }
                        });

                // Add the request to the Volley queue
                Volley.newRequestQueue(DetailCarPage.this).add(request);
            }
        });


        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCarPage.this, DashBoard_USER.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailCarPage.this, DashBoard_USER.class);
        startActivity(intent);
        finish();
    }
}