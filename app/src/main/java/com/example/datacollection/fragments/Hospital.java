package com.example.datacollection.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.datacollection.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;

public class Hospital extends Fragment {

    private static final int RESULT_OK = -1;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageButton getLocation;
    Button camera;
    TextView latitude;
    TextView longitude;
    ImageView photo;

    String URL = "https://daktarlagbe.com/public/api/doctor";
    EditText name,b_name, qualifications,b_qualifications,designation,b_designation, expertise,b_expertise,chamber,b_chamber,other_chamber,b_other_chamber,address,b_address,phone,visiting_hours_start,visiting_hours_end,wekend;
    Button submitBtn, clearBtn, cameraBtn;

    private RequestQueue rq;
    private Bitmap bitmap;
    String strImage;
    TimePickerDialog timePickerDialog;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_camera";
    String path;

    //service, test, surgery add option
    LinearLayout serviceLayoutList,testLayoutList,surgeryLayoutList;
    Button button_service_add,button_test_add,button_surgery_add;
    EditText service,surgeryName,testName;
    Button close;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_hospital, container, false);

        //service, test, surgery add option
        serviceLayoutList = root.findViewById(R.id.serviceLayoutList);
        testLayoutList = root.findViewById(R.id.testLayoutList);
        surgeryLayoutList = root.findViewById(R.id.surgeryLayoutList);
        button_service_add = root.findViewById(R.id.button_service_add);
        button_test_add = root.findViewById(R.id.button_test_add);
        button_surgery_add = root.findViewById(R.id.button_surgery_add);


        button_service_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceAddView();
            }
        });
        button_test_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAddView();
            }
        });
        button_surgery_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surgeryAddView();
            }
        });

        //map
        getLocation = root.findViewById(R.id.getLocation);
        latitude = root.findViewById(R.id.latitude);
        longitude = root.findViewById(R.id.longitude);
        camera = root.findViewById(R.id.camera);
        photo = root.findViewById(R.id.picture);
        //camera start
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //captureImage();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);*/
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation(); //check whether location service is enable or not in your  phone
            }
        });

        return root;
    }

    private void surgeryAddView() {
        final View surgeyView = getLayoutInflater().inflate(R.layout.surgery_list,null,false);

        surgeryName = surgeyView.findViewById(R.id.surgeryName);
        close = surgeyView.findViewById(R.id.cancel);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(surgeyView);
            }
        });

        surgeryLayoutList.addView(surgeyView);
    }

    private void testAddView() {
        final View testView = getLayoutInflater().inflate(R.layout.test_facility_list,null,false);

        testName = testView.findViewById(R.id.testName);
        close = testView.findViewById(R.id.cancel);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(testView);
            }
        });

        testLayoutList.addView(testView);
    }

    private void serviceAddView() {
        final View serviceView = getLayoutInflater().inflate(R.layout.service_list,null,false);

        service = serviceView.findViewById(R.id.service);
        close = serviceView.findViewById(R.id.cancel);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(serviceView);
            }
        });

        serviceLayoutList.addView(serviceView);
    }

    private void removeView(View view) {
        serviceLayoutList.removeView(view);
        testLayoutList.removeView(view);
        surgeryLayoutList.removeView(view);
    }


    //camera start
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(imageBitmap);

            path = saveImage(imageBitmap);
        }
    }

    private String saveImage(Bitmap myBitmap) {
        if(myBitmap != null)
        {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            byte[] imgBytes = bytes.toByteArray();
            return(Base64.encodeToString(imgBytes,Base64.DEFAULT));
        }
        return null;

    }

    //Map start
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude.setText(location.getLatitude()+"");
                                    longitude.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude.setText(mLastLocation.getLatitude()+"");
            longitude.setText(mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                (Activity) getContext(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }
}