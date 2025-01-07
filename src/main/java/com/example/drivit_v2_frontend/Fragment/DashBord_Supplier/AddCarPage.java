package com.example.drivit_v2_frontend.Fragment.DashBord_Supplier;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.drivit_v2_frontend.Cloudinary.MediaManagerState;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_USER;
import com.example.drivit_v2_frontend.R;
import com.example.drivit_v2_frontend.Sessions.SessionManager;
import com.example.drivit_v2_frontend.enums.Status_add;
import com.example.drivit_v2_frontend.enums.Status_dispo;
import com.example.drivit_v2_frontend.enums.UserType;
import com.example.drivit_v2_frontend.models.Cars;
import com.example.drivit_v2_frontend.models.Users;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddCarPage extends Fragment {
    TextView image_text;
    TextInputLayout carName, carModel, carMatricul, carPrix;
    Button btn_add_car;
    ImageView carImage;
    private static Integer IMAGE_REQ = 1;
    private Uri Image_Path;
    public String url_image;
    Map config = new HashMap();

    public AddCarPage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_add_car_page, container, false);

        SessionManager sessionManager = new SessionManager(requireActivity());
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();

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

        carName = rootview.findViewById(R.id.carName);
        carModel = rootview.findViewById(R.id.carModel);
        carMatricul = rootview.findViewById(R.id.carMatricul);
        carPrix = rootview.findViewById(R.id.carPrix);
        image_text = rootview.findViewById(R.id.image_text);

        btn_add_car = rootview.findViewById(R.id.btn_add_car);

        carImage = rootview.findViewById(R.id.carImage);

        carImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        // Cloudinary Config
        initCloudinary();

        btn_add_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateCarName() || !validateCarModel() || !validateCarMatricul() || !validateCarPrix() || !validateCarImage()) {
                    StyleableToast.makeText(requireActivity(), "Field cannot be empty", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                } else {
                    // Cloudinary Code
                    MediaManager.get().upload(Image_Path).callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {

                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {

                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String secureUrl = (String) resultData.get("secure_url");
                            if (secureUrl != null && !secureUrl.isEmpty()) {
                                url_image = secureUrl; // Assign the URL

                                // Proceed with the car data submission after the image is uploaded
                                addCarToBackend(users);
                            } else {
                                Log.e("AddCarPage", "Failed to retrieve secure_url");
                                StyleableToast.makeText(requireActivity(), "Image upload failed", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                            }
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Log.d("Cloudinary Quickstart", "Upload failed");
                            StyleableToast.makeText(requireActivity(), "Image upload failed", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }
                    }).dispatch();
                }
            }
        });
        return rootview;
    }

    private void addCarToBackend(Users users) {
        String _carName = carName.getEditText().getText().toString();
        String _carModel = carModel.getEditText().getText().toString();
        String _carMatricul = carMatricul.getEditText().getText().toString();
        String _carPrix = carPrix.getEditText().getText().toString();

        JSONObject carJson = new JSONObject();
        Gson gson = new Gson();

        try {
            carJson.put("users", new JSONObject(gson.toJson(users)));
            carJson.put("id_user", users.getUserID());
            carJson.put("carName", _carName);
            carJson.put("carModel", _carModel);
            carJson.put("carMatricul", _carMatricul);
            carJson.put("carImage", url_image); // Use the updated URL
            carJson.put("carPrix", _carPrix);
            carJson.put("statusDipo", Status_dispo.AVAILABLE.toString());
            carJson.put("statusAdd", Status_add.ACCEPTED.toString());

            // Send the request
            sendCarRequest(carJson);

        } catch (JSONException e) {
            e.printStackTrace();
            StyleableToast.makeText(requireActivity(), "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
        }
    }

    private void sendCarRequest(JSONObject carJson) {
        // BackEnd Data
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/CAR-SERVICES";

        String url = baseUrl + "/cars/addCar";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, carJson,
                response -> {
                    try {
                        String message = response.getString("msg");
                        int status = response.getInt("status");

                        if (status == 201) { // Success
                            StyleableToast.makeText(requireActivity(), message, Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                            Intent intent = new Intent(requireActivity(), DashBoard_USER.class);
                            startActivity(intent);
                        } else { // Other statuses
                            StyleableToast.makeText(requireActivity(), "Error: " + message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        StyleableToast.makeText(requireActivity(), "Unexpected response from server", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    }
                },
                error -> {
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

    private void initCongif() {
            config.put("cloud_name", getString(R.string.cloud_name));
            config.put("api_key", getString(R.string.api_key));
            config.put("api_secret", getString(R.string.api_secret));
            MediaManager.init(requireActivity(), config);
    }

    private void initCloudinary() {
        if (!MediaManagerState.isInitialized()) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", getString(R.string.cloud_name));
            config.put("api_key", getString(R.string.api_key));
            config.put("api_secret", getString(R.string.api_secret));

            MediaManager.init(requireActivity(), config);
            MediaManagerState.initialize();
        }
    }


    // TO SEE HOW TO USE THE REQUEST PERMISSION FUNCTION FOR KNOW IT WORKING WITH OUT PERMISSION
    /*  private void requestPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, IMAGE_REQ);
        }

    }*/

    private void selectImage() {
        Log.d("AddCarPage", "Image clicked");
        // requestPermission();
        Intent intent = new Intent();
        intent.setType("image/*");// if you want to you can use pdf/gif/video
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Image_Path = data.getData();
                        image_text.setText("");
                        Glide.with(requireActivity()).load(Image_Path).into(carImage);
                    }
                }
            });


    private boolean validateCarName() {
        String value = carName.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            carName.setError("Field cannot be empty");
            return false;
        } else {
            carName.setError(null);
            carName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCarModel() {
        String value = carModel.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            carModel.setError("Field cannot be empty");
            return false;
        } else {
            carModel.setError(null);
            carModel.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCarMatricul() {
        String value = carMatricul.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            carMatricul.setError("Field cannot be empty");
            return false;
        } else {
            carMatricul.setError(null);
            carMatricul.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCarPrix() {
        String value = carPrix.getEditText().getText().toString().trim();
        if (value.isEmpty()) {
            carPrix.setError("Field cannot be empty");
            return false;
        } else {
            carPrix.setError(null);
            carPrix.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCarImage() {
        if (Image_Path == null) {
            StyleableToast.makeText(requireActivity(), "Need To Add An Image", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
            return false;
        } else {
            return true;
        }
    }
}