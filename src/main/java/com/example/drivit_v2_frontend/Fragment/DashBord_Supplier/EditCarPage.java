package com.example.drivit_v2_frontend.Fragment.DashBord_Supplier;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.drivit_v2_frontend.Cloudinary.MediaManagerState;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_ADMIN;
import com.example.drivit_v2_frontend.DashBoards.DashBoard_SUPPLIER;
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

public class EditCarPage extends AppCompatActivity {

    TextView image_text;
    TextInputLayout carName, carModel, carMatricul, carPrix;
    Button btn_update_car,btn_goback;
    ImageView carImage;
    private static Integer IMAGE_REQ = 1;
    private Uri Image_Path;
    public String url_image;
    Map config = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_car_page);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Cars car = (Cars) bundle.getSerializable("car");

        SessionManager sessionManager = new SessionManager(EditCarPage.this);
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

        carName = findViewById(R.id.carName);
        carModel = findViewById(R.id.carModel);
        carMatricul = findViewById(R.id.carMatricul);
        carPrix = findViewById(R.id.carPrix);
        image_text = findViewById(R.id.image_text);
        btn_goback = findViewById(R.id.btn_goback);
        btn_update_car = findViewById(R.id.btn_update_car);
        carImage = findViewById(R.id.carImage);

        carName.getEditText().setText(car.getCarName().toString());
        carModel.getEditText().setText(car.getCarModel().toString());
        carMatricul.getEditText().setText(car.getCarMatricul().toString());
        carPrix.getEditText().setText(car.getCarPrix().toString());
        image_text.setText("");
        Image_Path = Uri.parse(car.getCarImage());
        Glide.with(this).load(car.getCarImage()).into(carImage);

        carImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btn_goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(users.getStatus() == UserType.SUPPLIER){
                    Intent intent = new Intent(EditCarPage.this, DashBoard_SUPPLIER.class);
                    startActivity(intent);
                    finish();
                } else if (users.getStatus() == UserType.ADMIN) {
                    Intent intent = new Intent(EditCarPage.this, DashBoard_ADMIN.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Cloudinary Config
        initCloudinary();

        btn_update_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateCarName() || !validateCarModel() || !validateCarMatricul() || !validateCarPrix() || !validateCarImage()) {
                    StyleableToast.makeText(EditCarPage.this, "Field cannot be empty", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                } else {
                    // Check if the image path is the same as the existing URL
                    if (Image_Path.toString().equals(car.getCarImage())) {
                        url_image = car.getCarImage(); // Assign the old URL
                        addCarToBackend(users, car);
                    } else {
                        // Upload the new image to Cloudinary
                        MediaManager.get().upload(Image_Path).callback(new UploadCallback() {
                            @Override
                            public void onStart(String requestId) {}

                            @Override
                            public void onProgress(String requestId, long bytes, long totalBytes) {}

                            @Override
                            public void onSuccess(String requestId, Map resultData) {
                                String secureUrl = (String) resultData.get("secure_url");
                                if (secureUrl != null && !secureUrl.isEmpty()) {
                                    url_image = secureUrl; // Assign the new URL
                                    addCarToBackend(users, car);
                                } else {
                                    Log.e("EditCarPage", "Failed to retrieve secure_url");
                                    StyleableToast.makeText(EditCarPage.this, "Image upload failed", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                                }
                            }

                            @Override
                            public void onError(String requestId, ErrorInfo error) {
                                Log.d("EditCarPage", "Upload failed");
                                StyleableToast.makeText(EditCarPage.this, "Image upload failed", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                            }

                            @Override
                            public void onReschedule(String requestId, ErrorInfo error) {
                                // Optional: Handle rescheduled uploads
                            }
                        }).dispatch();
                    }
                }
            }
        });

    }
    private void addCarToBackend(Users users,Cars cars) {
        String _carName = carName.getEditText().getText().toString();
        String _carModel = carModel.getEditText().getText().toString();
        String _carMatricul = carMatricul.getEditText().getText().toString();
        String _carPrix = carPrix.getEditText().getText().toString();

        JSONObject carJson = new JSONObject();
        Gson gson = new Gson();

        try {
            carJson.put("id_car", cars.getId_car());
            carJson.put("users", new JSONObject(gson.toJson(users)));
            carJson.put("id_user", users.getUserID());
            carJson.put("carName", _carName);
            carJson.put("carModel", _carModel);
            carJson.put("carMatricul", _carMatricul);
            carJson.put("carImage", url_image); // Use the updated URL
            carJson.put("carPrix", _carPrix);
            carJson.put("statusDipo", cars.getStatusDipo().toString());
            carJson.put("statusAdd", Status_add.PENDING.toString());

            // Send the request
            sendCarRequest(carJson,users);

        } catch (JSONException e) {
            e.printStackTrace();
            StyleableToast.makeText(EditCarPage.this, "Error creating JSON", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
        }
    }

    private void sendCarRequest(JSONObject carJson,Users users) {
        // BackEnd Data
        final String port = "8888";
        final String ip_address = getString(R.string.ip_address);
        final String baseUrl = "http://" + ip_address + ":" + port + "/CAR-SERVICES";

        String url = baseUrl + "/cars/updatecar";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, carJson,
                response -> {
                    try {
                        String message = response.getString("msg");
                        int status = response.getInt("status");

                        if (status == 201) { // Success
                            StyleableToast.makeText(EditCarPage.this, message, Toast.LENGTH_SHORT, R.style.mytoastdone).show();
                            if(users.getStatus() == UserType.SUPPLIER){
                                Intent intent = new Intent(EditCarPage.this, DashBoard_SUPPLIER.class);
                                startActivity(intent);
                            } else if (users.getStatus() == UserType.ADMIN) {
                                Intent intent = new Intent(EditCarPage.this, DashBoard_ADMIN.class);
                                startActivity(intent);
                            }
                        } else { // Other statuses
                            StyleableToast.makeText(EditCarPage.this, "Error: " + message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        StyleableToast.makeText(EditCarPage.this, "Unexpected response from server", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        String responseBody = new String(error.networkResponse.data);// Convert byte array to string

                        try {

                            JSONObject errorResponse = new JSONObject(responseBody);
                            String message = errorResponse.getString("msg"); // Get the "msg" field from the error response

                            StyleableToast.makeText(EditCarPage.this, message, Toast.LENGTH_SHORT, R.style.mytoasterror).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            StyleableToast.makeText(EditCarPage.this, "Error parsing error response", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                        }

                    } else {
                        // If no network response, it's likely a network error
                        StyleableToast.makeText(EditCarPage.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
                    }
                });

        Volley.newRequestQueue(EditCarPage.this).add(request);
    }

    private void initCloudinary() {
        if (!MediaManagerState.isInitialized()) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", getString(R.string.cloud_name));
            config.put("api_key", getString(R.string.api_key));
            config.put("api_secret", getString(R.string.api_secret));

            MediaManager.init(EditCarPage.this, config);
            MediaManagerState.initialize();
        }
    }


    // TO SEE HOW TO USE THE REQUEST PERMISSION FUNCTION FOR KNOW IT WORKING WITH OUT PERMISSION
    /*  private void requestPermission() {
        if (ContextCompat.checkSelfPermission(EditCarPage.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectImage();
        } else {
            ActivityCompat.requestPermissions(EditCarPage.this, new String[]{
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
                        Glide.with(EditCarPage.this).load(Image_Path).into(carImage);
                    }
                }
            });

    private boolean validateCarName() {
        String value = carName.getEditText() != null ? carName.getEditText().getText().toString().trim() : "";
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
        String value = carModel.getEditText() != null ? carModel.getEditText().getText().toString().trim() : "";
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
        String value = carMatricul.getEditText() != null ? carMatricul.getEditText().getText().toString().trim() : "";
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
        String value = carPrix.getEditText() != null ? carPrix.getEditText().getText().toString().trim() : "";
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
            StyleableToast.makeText(EditCarPage.this, "Need To Add An Image", Toast.LENGTH_SHORT, R.style.mytoasterror).show();
            return false;
        } else {
            return true;
        }
    }
}