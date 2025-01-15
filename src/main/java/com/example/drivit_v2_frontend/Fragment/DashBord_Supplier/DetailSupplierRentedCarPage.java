package com.example.drivit_v2_frontend.Fragment.DashBord_Supplier;

import android.annotation.SuppressLint;
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
import com.example.drivit_v2_frontend.DashBoards.DashBoard_SUPPLIER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.Status_rental;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.CarRental;
import com.example.drivit_v2_frontend.models.Users;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.time.LocalDate;
import java.util.HashMap;
import io.github.muddz.styleabletoast.StyleableToast;

public class DetailSupplierRentedCarPage extends AppCompatActivity {

    ImageView image_car;
    TextView car_name, car_detail, rental_car_status;
    Button btn_accept, btn_refuse, btn_goback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_car_detail_page);


        SessionManager sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();

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

        Users users = new Users(_userID, _firstName, _lastName, _userName, _passWord, _cin, _email, _phone, UserType.valueOf(_status_user));

        image_car = findViewById(R.id.image_car);
        car_name = findViewById(R.id.car_name);
        car_detail = findViewById(R.id.car_detail);
        btn_refuse = findViewById(R.id.btn_refuse);
        btn_accept = findViewById(R.id.btn_accept);
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
        String url = baseUrl + "/carrental/updatecarrental";


        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set car status to NOT_AVAILABLE
                carRental.getCars().setStatusDipo(Status_dispo.NOT_AVAILABLE);
                // Create a JSON object with field names matching the backend
                JSONObject carJson = new JSONObject();
                Gson gson = new Gson();
                try {
                    carJson.put("id_carRental", carRental.getId_carRental());
                    carJson.put("cars", new JSONObject(gson.toJson(carRental.getCars())));
                    carJson.put("id_car", carRental.getId_car());
                    carJson.put("users", new JSONObject(gson.toJson(users)));
                    carJson.put("id_user", carRental.getId_user());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        carJson.put("rentalTime", LocalDate.now().toString());
                    }
                    carJson.put("statusRental", Status_rental.ACCEPTED.name());
                } catch (JSONException e) {
                    e.printStackTrace();
                    StyleableToast.makeText(DetailSupplierRentedCarPage.this,
                            "Error creating JSON: " + e.getMessage(), R.style.mytoasterror).show();
                    return;
                }

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, carJson,
                        response -> {
                            try {
                                String message = response.getString("msg");
                                int status = response.getInt("status");

                                if (status == 201) { // Success
                                    StyleableToast.makeText(DetailSupplierRentedCarPage.this, message, R.style.mytoastdone).show();
                                    Intent intent = new Intent(DetailSupplierRentedCarPage.this, DashBoard_SUPPLIER.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Unexpected error: " + message, R.style.mytoasterror).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Unexpected response from server", R.style.mytoasterror).show();
                            }
                        },
                        error -> {
                            if (error.networkResponse != null) {
                                int statusCode = error.networkResponse.statusCode;
                                String responseBody = new String(error.networkResponse.data);

                                try {
                                    JSONObject errorResponse = new JSONObject(responseBody);
                                    String message = errorResponse.getString("msg");
                                    StyleableToast.makeText(DetailSupplierRentedCarPage.this,
                                            message, R.style.mytoasterror).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    // Fallback for non-JSON error responses
                                    StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Unexpected response: " + responseBody, R.style.mytoasterror).show();
                                }
                            } else {
                                StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Network error. Please check your connection.", R.style.mytoasterror).show();
                            }
                        });
                Volley.newRequestQueue(DetailSupplierRentedCarPage.this).add(request);
            }
        });


        btn_refuse.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                carRental.getCars().setStatusDipo(Status_dispo.AVAILABLE);
                // Create a JSON object with field names matching the backend for btn_refuse
                JSONObject carJson = new JSONObject();
                Gson gson = new Gson();
                try {
                    carJson.put("id_carRental", carRental.getId_carRental());
                    carJson.put("cars", new JSONObject(gson.toJson(carRental.getCars())));
                    carJson.put("id_car", carRental.getId_car());
                    carJson.put("users", new JSONObject(gson.toJson(users)));
                    carJson.put("id_user", carRental.getId_user());
                    carJson.put("rentalTime", LocalDate.now().toString());
                    carJson.put("statusRental", Status_rental.REFUSED);
                } catch (JSONException e) {
                    e.printStackTrace();
                    StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    return;
                }
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, carJson,
                        response -> {
                            try {
                                String message = response.getString("msg");
                                int status = response.getInt("status");

                                switch (status) {
                                    case 201: // Success
                                        StyleableToast.makeText(DetailSupplierRentedCarPage.this, message, R.style.mytoastdone).show();
                                        Intent intent = new Intent(DetailSupplierRentedCarPage.this, DashBoard_SUPPLIER.class);
                                        startActivity(intent);
                                        finish();
                                        break;
                                    default: // Other unexpected statuses
                                        StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Unexpected error: " + message, R.style.mytoasterror).show();
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Unexpected response from server", R.style.mytoasterror).show();
                            }
                        },
                        error -> {
                            if (error.networkResponse != null) {
                                int statusCode = error.networkResponse.statusCode;
                                String responseBody = new String(error.networkResponse.data);

                                try {
                                    JSONObject errorResponse = new JSONObject(responseBody);
                                    String message = errorResponse.getString("msg");
                                    StyleableToast.makeText(DetailSupplierRentedCarPage.this, message, R.style.mytoasterror).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Error parsing error response", R.style.mytoasterror).show();
                                }
                            } else {
                                StyleableToast.makeText(DetailSupplierRentedCarPage.this, "Network error. Please check your connection.", R.style.mytoasterror).show();
                            }
                        });
                Volley.newRequestQueue(DetailSupplierRentedCarPage.this).add(request);
            }
        });

        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailSupplierRentedCarPage.this, DashBoard_SUPPLIER.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetailSupplierRentedCarPage.this, DashBoard_SUPPLIER.class);
        startActivity(intent);
        finish();
    }
}