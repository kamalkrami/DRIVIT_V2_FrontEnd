package com.example.drivit_v2_frontend.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.RecyclerViews.RecylerViewAdapterCarsPage;
import com.example.drivit_v2_frontend.RecyclerViews.RecylerViewAdapterHomePage;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_add;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.Status_rental;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.CarRental;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;


public class CarsPage extends Fragment {

    private RecyclerView recyclerView;
    private RecylerViewAdapterCarsPage adapter;
    ArrayList<CarRental> carList;

    public CarsPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  rootView =  inflater.inflate(R.layout.fragment_cars_page, container, false);

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

        recyclerView=rootView.findViewById(R.id.rv_carpage);
        carList = new ArrayList<CarRental>();
        adapter = new RecylerViewAdapterCarsPage(requireActivity(),carList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        // BackEnd
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/CAR-RENTAL-SERVICES";

        String url = baseUrl + "/carrental/" + _userID;

        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            carList.clear(); // Clear existing data

                            // Loop through each rental in the JSON array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject rentalObject = response.getJSONObject(i);

                                // Parse carRental data
                                String id_carRental = rentalObject.getString("id_carRental");
                                String id_user_rental = rentalObject.getString("id_user");
                                String id_car_rental = rentalObject.getString("id_car");
                                String rentalTime = rentalObject.getString("rentalTime");
                                String statusRental = rentalObject.getString("statusRental");

                                // Parse nested car object
                                JSONObject carObject = rentalObject.getJSONObject("cars");
                                Cars car = new Cars();
                                car.setId_car(carObject.getString("id_car"));
                                car.setId_user(carObject.getString("id_user"));
                                car.setCarName(carObject.getString("carName"));
                                car.setCarPrix(carObject.getString("carPrix"));
                                car.setCarModel(carObject.getString("carModel"));
                                car.setCarMatricul(carObject.getString("carMatricul"));
                                car.setCarImage(carObject.getString("carImage"));
                                car.setStatusDipo(Status_dispo.valueOf(carObject.getString("statusDipo")));
                                car.setStatusAdd(Status_add.valueOf(carObject.getString("statusAdd")));

                                // Parse nested user object inside car
                                JSONObject carUserObject = carObject.getJSONObject("users");
                                Users carUser = new Users(
                                        carUserObject.getString("id_user"),
                                        carUserObject.getString("firstName"),
                                        carUserObject.getString("lastName"),
                                        carUserObject.getString("email"),
                                        carUserObject.getString("cin"),
                                        carUserObject.getString("phone"),
                                        carUserObject.getString("userName"),
                                        carUserObject.getString("passWord"),
                                        UserType.valueOf(carUserObject.getString("status"))
                                );
                                car.setUsers(carUser);

                                // Parse main user object
                                JSONObject mainUserObject = rentalObject.getJSONObject("users");
                                Users mainUser = new Users(
                                        mainUserObject.getString("id_user"),
                                        mainUserObject.getString("firstName"),
                                        mainUserObject.getString("lastName"),
                                        mainUserObject.getString("email"),
                                        mainUserObject.getString("cin"),
                                        mainUserObject.getString("phone"),
                                        mainUserObject.getString("userName"),
                                        mainUserObject.getString("passWord"),
                                        UserType.valueOf(mainUserObject.getString("status"))
                                );

                                // Add parsed carRental to the list
                                CarRental carRental = new CarRental(
                                        id_carRental,
                                        car,
                                        id_car_rental,
                                        mainUser,
                                        id_user_rental,
                                        rentalTime,
                                        Status_rental.valueOf(statusRental)
                                );
                                carList.add(carRental);
                            }

                            adapter.notifyDataSetChanged(); // Notify adapter about data changes
                        } catch (JSONException e) {
                            e.printStackTrace();
                            StyleableToast.makeText(requireActivity(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error: " + error.getMessage());
                        StyleableToast.makeText(requireActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonArrayRequest);


        return rootView;
    }
}