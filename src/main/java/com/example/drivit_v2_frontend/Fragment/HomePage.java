package com.example.drivit_v2_frontend.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.RecyclerViews.RecylerViewAdapterHomePage;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_add;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class HomePage extends Fragment {

    private RecyclerView recyclerView;
    private RecylerViewAdapterHomePage adapter;
    ArrayList<Cars> carList;

    TextView hello_name;

    public HomePage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  rootView =  inflater.inflate(R.layout.fragment_home_page, container, false);

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

        hello_name = rootView.findViewById(R.id.hello_name);
        hello_name.setText(_firstName+" "+_lastName);


        recyclerView=rootView.findViewById(R.id.rv_1);
        carList = new ArrayList<Cars>();
        adapter = new RecylerViewAdapterHomePage(requireActivity(),carList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        // BackEnd
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/CAR-SERVICES";

        String url = baseUrl + "/cars/dispo/AVAILABLE/ACCEPTED";

        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            carList.clear(); // Clear existing data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject carObject = response.getJSONObject(i);

                                // Parse JSON into Cars object
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

                                // Parse nested user object
                                JSONObject userObject = carObject.getJSONObject("users");
                                String userID = userObject.getString("id_user");
                                String firstName = userObject.getString("firstName");
                                String lastName = userObject.getString("lastName");
                                String email = userObject.getString("email");
                                String cin = userObject.getString("cin");
                                String phone = userObject.getString("phone");
                                String userName = userObject.getString("userName");
                                String passWord = userObject.getString("passWord");
                                String status_user = userObject.getString("status");

                                Users users = new Users(userID,firstName,lastName,email,cin,phone,userName,passWord, UserType.valueOf(status_user));

                                car.setUsers(users);

                                carList.add(car); // Add to the list
                            }
                            adapter.notifyDataSetChanged(); // Notify adapter about data changes
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Toast.makeText(requireActivity(), "Error parsing data", Toast.LENGTH_SHORT).show();
                            StyleableToast.makeText(requireActivity(), "Error parsing data", Toast.LENGTH_SHORT, R.style.mytoasterror).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error: " + error.getMessage());
                        // Toast.makeText(requireActivity(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(requireActivity(), "Failed to fetch data", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    }
                });

        queue.add(jsonArrayRequest);

        return rootView;
    }
}