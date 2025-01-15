package com.example.drivit_v2_frontend.Fragment.DashBord_Admin;

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
import com.android.volley.toolbox.Volley;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.RecyclerViews.RecylerViewAdapterAllUsers;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.muddz.styleabletoast.StyleableToast;

public class AllUsersPage extends Fragment {

    private RecyclerView recyclerView;
    private RecylerViewAdapterAllUsers adapter;
    ArrayList<Users> usersList;

    public AllUsersPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_all_users_page, container, false);

        recyclerView=rootView.findViewById(R.id.rv_all_users);
        usersList = new ArrayList<Users>();
        adapter = new RecylerViewAdapterAllUsers(requireActivity(), usersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);

        // BackEnd
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/USERS-SERVICES";

        String url = baseUrl + "/users";

        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            usersList.clear(); // Clear existing data
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);

                                // Parse nested user object
                                String userID = userObject.getString("id_user");
                                String firstName = userObject.getString("firstName");
                                String lastName = userObject.getString("lastName");
                                String userName = userObject.getString("userName");
                                String passWord = userObject.getString("passWord");
                                String cin = userObject.getString("cin");
                                String email = userObject.getString("email");
                                String phone = userObject.getString("phone");
                                String status_user = userObject.getString("status");
                                Users user = new Users(userID,firstName,lastName,userName,passWord,cin,email,phone, UserType.valueOf(status_user));

                                usersList.add(user); // Add to the list
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