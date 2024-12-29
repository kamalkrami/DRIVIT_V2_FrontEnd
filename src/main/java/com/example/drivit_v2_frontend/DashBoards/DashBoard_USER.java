package com.example.drivit_v2_frontend.DashBoards;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.drivit_v2_frontend.Activitys.LoginPage;
import com.example.drivit_v2_frontend.Fragment.CarsPage;
import com.example.drivit_v2_frontend.Fragment.HomePage;
import com.example.drivit_v2_frontend.Fragment.UserProfilePage;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

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

        sessionManager = new SessionManager(this);
        HashMap<String,String> userDetails = sessionManager.getUserDetailFromSession();

        /*full_name = findViewById(R.layout.nav_header).findViewById(R.id.full_name);
        full_name_details = findViewById(R.layout.nav_header).findViewById(R.id.full_name_details);
        imageView = findViewById(R.layout.nav_header).findViewById(R.id.user_image);*/

        String _userID = userDetails.get(SessionManager.KEY_ID);
        String _firstName = userDetails.get(SessionManager.KEY_FIRSTNAME);
        String _lastName = userDetails.get(SessionManager.KEY_LASTNAME);
        String _email = userDetails.get(SessionManager.KEY_EMAIL);
        String _cin = userDetails.get(SessionManager.KEY_CIN);
        String _phone = userDetails.get(SessionManager.KEY_PHONE);
        String _userName = userDetails.get(SessionManager.KEY_USERNAME);
        String _passWord = userDetails.get(SessionManager.KEY_PASSWORD);
        String _status_user = userDetails.get(SessionManager.KEY_STATUS);

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
        if (item.getItemId() == R.id.Home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new HomePage()).commit();
            text_Menu.setText("Menu Home");
        } else if (item.getItemId() == R.id.Cars) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new CarsPage()).commit();
            text_Menu.setText("Menu Cars");
        } else if (item.getItemId() == R.id.Profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout,new UserProfilePage()).commit();
            text_Menu.setText("Menu Profile");
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
}