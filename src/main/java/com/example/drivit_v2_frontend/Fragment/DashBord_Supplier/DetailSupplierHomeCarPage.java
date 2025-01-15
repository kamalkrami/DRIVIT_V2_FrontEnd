package com.example.drivit_v2_frontend.Fragment.DashBord_Supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_SUPPLIER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;

import java.util.HashMap;

public class DetailSupplierHomeCarPage extends AppCompatActivity {
    ImageView image_car;
    TextView car_name,car_detail,car_status;
    Button btn_goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_supplier_car_page);

        SessionManager sessionManager = new SessionManager(this);
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

        image_car = findViewById(R.id.image_car);
        car_name = findViewById(R.id.car_name);
        car_detail = findViewById(R.id.car_detail);
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


        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailSupplierHomeCarPage.this, DashBoard_SUPPLIER.class);
                startActivity(intent);
                finish();
            }
        });

    }
}