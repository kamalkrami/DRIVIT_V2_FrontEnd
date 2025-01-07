package com.example.drivit_v2_frontend.DashBoards;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.Activitys.LoginPage;
import com.example.drivit_v2_frontend.Fragment.DashBord_User.CarsPage;
import com.example.drivit_v2_frontend.Fragment.HomePage;
import com.example.drivit_v2_frontend.Fragment.DashBord_User.RequestSupplierPage;
import com.example.drivit_v2_frontend.Fragment.DashBord_User.RequestSupplierPageDetail;
import com.example.drivit_v2_frontend.Fragment.DashBord_User.UserProfilePage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_Request_Supplier;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.RequestSupplier;
import com.example.drivit_v2_frontend.models.Users;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class DashBoard_USER extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView text_Menu,full_name,full_name_details;
    SessionManager sessionManager;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board_user);

        /*full_name = findViewById(R.layout.nav_header).findViewById(R.id.full_name);
        full_name_details = findViewById(R.layout.nav_header).findViewById(R.id.full_name_details);
        imageView = findViewById(R.layout.nav_header).findViewById(R.id.user_image);*/

        /*full_name.setText(_firstName+" "+_lastName);
        full_name_details.setText(_firstName+" "+_lastName);*/

        text_Menu = findViewById(R.id.text_Menu);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.bleu_2));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            text_Menu.setText("Menu Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new HomePage()).commit();
            navigationView.setCheckedItem(R.id.Home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        sessionManager = new SessionManager(this);
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

        if (item.getItemId() == R.id.Home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new HomePage()).commit();
            text_Menu.setText("Menu Home");
        } else if (item.getItemId() == R.id.Cars) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new CarsPage()).commit();
            text_Menu.setText("Menu Cars");
        } else if (item.getItemId() == R.id.Profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new UserProfilePage()).commit();
            text_Menu.setText("Menu Profile");
        } else if (item.getItemId() == R.id.RequestSupplier) {
            checkRequestSupplier(_userID);
        } else if (item.getItemId() == R.id.Logout) {
            startActivity(new Intent(DashBoard_USER.this, LoginPage.class));
            sessionManager.logoutUserFromSession();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public void checkRequestSupplier(String UserID) {
        // BackEnd
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/REQUEST-SUPPLIER-SERVICES";

        String url = baseUrl + "/requestsupplier/by_id_user/" + UserID;

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            if (status == 200) {
                                // Extract requestSupplier details
                                JSONObject requestSupplier = response.getJSONObject("requestSupplier");
                                String idRequest = requestSupplier.getString("id_request");
                                String requestDate = requestSupplier.getString("requestDate");
                                String requestStatus = requestSupplier.getString("status");

                                JSONObject userDetails = requestSupplier.getJSONObject("users");
                                String userID = userDetails.getString("id_user");
                                String firstName = userDetails.getString("firstName");
                                String lastName = userDetails.getString("lastName");
                                String userName = userDetails.getString("userName");
                                String passWord = userDetails.getString("passWord");
                                String cin = userDetails.getString("cin");
                                String email = userDetails.getString("email");
                                String phone = userDetails.getString("phone");
                                String status_user = userDetails.getString("status");

                                Users users = new Users(userID, firstName, lastName, userName, passWord, cin, email, phone, UserType.valueOf(status_user));

                                RequestSupplier requestSupplier1 = new RequestSupplier(idRequest, users, UserID, requestDate, Status_Request_Supplier.valueOf(requestStatus));
                                Bundle bundle = new Bundle();

                                RequestSupplierPageDetail requestSupplierPageDetail = new RequestSupplierPageDetail();
                                bundle.putSerializable("requestSupplier", requestSupplier1);
                                requestSupplierPageDetail.setArguments(bundle);

                                // Navigate to the fragment for users with existing requests
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, requestSupplierPageDetail).commit();
                                text_Menu.setText("Menu Request Supplier");
                            } else {
                                // Navigate to the fragment for new users
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new RequestSupplierPage()).commit();
                                text_Menu.setText("Menu Request Supplier");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            StyleableToast.makeText(getApplicationContext(), "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        error.printStackTrace();
                        // Enhanced error handling
                        if(error.networkResponse.statusCode == 404){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new RequestSupplierPage()).commit();
                            text_Menu.setText("Menu Request Supplier");
                        }
                    }
                }
        );
        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }

}