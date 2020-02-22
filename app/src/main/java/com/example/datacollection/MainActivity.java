package com.example.datacollection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity{

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    Button getLocation;

    String URL = "https://daktarlagbe.com/public/api/doctor";
    EditText name,b_name, qualifications,b_qualifications,designation,b_designation, expertise,b_expertise,chamber,b_chamber,other_chamber,b_other_chamber,address,b_address,phone,visiting_hours_start,visiting_hours_end,wekend;
    Button submitBtn, clearBtn, cameraBtn;
    ImageView picture;

    private RequestQueue rq;
    private Bitmap bitmap;
    String strImage;
    TimePickerDialog timePickerDialog;
    private static final String IMAGE_DIRECTORY = "/demonuts_upload_camera";
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        getLocation = findViewById(R.id.getLocationBtn);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLastLocation(); //check whether location service is enable or not in your  phone
            }
        });


        //for insert data
        name = (EditText)findViewById(R.id.name);
        b_name = findViewById(R.id.b_name);
        qualifications = findViewById(R.id.qualifications);
        b_qualifications = findViewById(R.id.b_qualifications);
        designation = findViewById(R.id.designation);
        b_designation = findViewById(R.id.b_designation);
        expertise = findViewById(R.id.expertise);
        b_expertise = findViewById(R.id.b_expertise);
        chamber = findViewById(R.id.chamber);
        b_chamber = findViewById(R.id.b_chamber);
        other_chamber = findViewById(R.id.other_chamber);
        b_other_chamber = findViewById(R.id.b_other_chamber);
        address = findViewById(R.id.address);
        b_address = findViewById(R.id.b_address);
        phone = findViewById(R.id.phone);
        visiting_hours_start = findViewById(R.id.visiting_hours_start);
        visiting_hours_end = findViewById(R.id.visiting_hours_end);
        wekend = findViewById(R.id.wekend);
        picture = findViewById(R.id.picture);

        visiting_hours_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        visiting_hours_start.setText(hourOfDay+":"+minute);
                    }
                },12,0,false);
                timePickerDialog.show();*/

                //////////////
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        int hour = hourOfDay;
                        int minutes = minute;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12){
                            timeSet = "PM";
                        }else{
                            timeSet = "AM";
                        }

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes ;
                        else
                            min = String.valueOf(minutes);

                        String mTime = new StringBuilder().append(hour).append(':')
                                .append(min ).append(" ").append(timeSet).toString();
                        visiting_hours_start.setText(mTime);
                    }
                },0,0,false);
                timePickerDialog.show();

                ///////////////
            }
        });
        visiting_hours_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        visiting_hours_end.setText(hourOfDay+":"+minute);
                    }
                },0,0,false);
                timePickerDialog.show();*/

               ////////////////
                timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // TODO Auto-generated method stub
                        int hour = hourOfDay;
                        int minutes = minute;
                        String timeSet = "";
                        if (hour > 12) {
                            hour -= 12;
                            timeSet = "PM";
                        } else if (hour == 0) {
                            hour += 12;
                            timeSet = "AM";
                        } else if (hour == 12){
                            timeSet = "PM";
                        }else{
                            timeSet = "AM";
                        }

                        String min = "";
                        if (minutes < 10)
                            min = "0" + minutes ;
                        else
                            min = String.valueOf(minutes);

                        String mTime = new StringBuilder().append(hour).append(':')
                                .append(min ).append(" ").append(timeSet).toString();
                        visiting_hours_end.setText(mTime);
                    }
                },0,0,false);
                timePickerDialog.show();


                ///////////////
            }
        });

        submitBtn = (Button)findViewById(R.id.button);
        cameraBtn = findViewById(R.id.CameraBtn);
        //camera start
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //captureImage();
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);*/
            }
        });


        //camera end
        clearBtn = findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText(null);
                b_name.setText(null);
                qualifications.setText(null);
                b_qualifications.setText(null);
                designation.setText(null);
                b_designation.setText(null);
                expertise.setText(null);
                b_expertise.setText(null);
                chamber.setText(null);
                b_chamber.setText(null);
                other_chamber.setText(null);
                b_other_chamber.setText(null);
                address.setText(null);
                b_address.setText(null);
                phone.setText(null);
                visiting_hours_start.setText(null);
                visiting_hours_end.setText(null);
                wekend.setText(null);
            }
        });

        rq = Volley.newRequestQueue(this);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String  strLat, strLong, strName, strBName, strQualifications, strBQualifications, strDesignation,strBDesignation, strExpertise, strBExpertise,
                strChamber, strBChamber, strOtherChamber,strBOtherChamber, strAddress, strBAddress, strPhone, strVisitingHourStart, strVisitingHourEnd, strWekend, strImaage;

                strName = name.getText().toString();
                strBName = b_name.getText().toString();
                strQualifications = qualifications.getText().toString();
                strBQualifications = b_qualifications.getText().toString();
                strDesignation = designation.getText().toString();
                strBDesignation = b_designation.getText().toString();
                strExpertise = expertise.getText().toString();
                strBExpertise = b_expertise.getText().toString();
                strChamber = chamber.getText().toString();
                strBChamber = b_chamber.getText().toString();
                strOtherChamber = other_chamber.getText().toString();
                strBOtherChamber = b_other_chamber.getText().toString();
                strAddress = address.getText().toString();
                strBAddress = b_address.getText().toString();
                strPhone = phone.getText().toString();
                strVisitingHourStart = visiting_hours_start.getText().toString();
                strVisitingHourEnd = visiting_hours_end.getText().toString();
                strWekend = wekend.getText().toString();

                strLat = latTextView.getText().toString();
                strLong = lonTextView.getText().toString();
                //upload();

                strImage = path;

                sendToServer(strLat,strLong,strName,strBName,strQualifications,strBQualifications,strDesignation,strBDesignation,strExpertise,strBExpertise,strChamber,strBChamber,strOtherChamber,strBOtherChamber,strAddress,strBAddress,strPhone,strVisitingHourStart,strVisitingHourEnd,strWekend,strImage);
               // System.out.println(ba1);
                //camera
               // uploadImage();

            }
        });
    }

 /*   private void upload() {

        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath);
        if(bm!=null){
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] ba = bao.toByteArray();
            strImage = Base64.encodeToString(ba,Base64.DEFAULT);
        }

        // Upload image to server
       // new uploadToServer().execute();
    }*/

  /*  private void captureImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
           startActivityForResult(takePictureIntent, 100);
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            picture.setImageBitmap(imageBitmap);

            path = saveImage(imageBitmap);

         /*   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] imgBytes = byteArrayOutputStream.toByteArray();
            strImage = Base64.encodeToString(imgBytes,Base64.DEFAULT);*/


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

       /* File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpeg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";*/
    }



   //camera end

    private void sendToServer(final String strLat, final String strLong, final String strName, final String strBName, final String strQualifications, final String strBQualifications, final String strDesignation, final String strBDesignation, final String strExpertise, final String strBExpertise, final String strChamber, final String strBChamber, final String strOtherChamber, final String strBOtherChamber, final String strAddress, final String strBAddress, final String strPhone, final String strVisitingHourStart, final String strVisitingHourEnd, final String strWekend, final String strImage) {
        StringRequest uploadData = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MainActivity.this, "Success !", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
               // String image = imageToString(photo);
                //String strImage = imageToString(bitmap);

                Map<String,String> map = new HashMap<>();
                map.put("lat",strLat);
                map.put("long",strLong);
                map.put("name",strName);
                map.put("b_name",strBName);
                map.put("qualifications",strQualifications);
                map.put("b_qualifications",strBQualifications);
                map.put("designation",strDesignation);
                map.put("b_designation",strBDesignation);
                map.put("expertise",strExpertise);
                map.put("b_expertise",strBExpertise);

                map.put("chamber",strChamber);
                map.put("b_chamber",strBChamber);
                map.put("other_chamber",strOtherChamber);
                map.put("b_other_chamber",strBOtherChamber);
                map.put("address",strAddress);
                map.put("b_address",strBAddress);
                map.put("phone",strPhone);
                map.put("visiting_hours_start",strVisitingHourStart);
                map.put("visiting_hours_end",strVisitingHourEnd);
                map.put("weekdays",strWekend);
                map.put("image", strImage);

                return map;

            }
        };
        rq.add(uploadData);
    }

    //Map

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
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

    //insert data

}