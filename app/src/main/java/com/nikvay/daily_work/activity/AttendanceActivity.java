package com.nikvay.daily_work.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.nikvay.daily_work.HomeActivity;
import com.nikvay.daily_work.R;
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity implements LocationListener {

    @BindView(R.id.tv_attendance_date)
    TextView tv_attendance_date;

    @BindView(R.id.tv_attendance_time)
    TextView tv_attendance_time;

    @BindView(R.id.tv_attendance_location)
    TextView tv_attendance_location;

    String date, time, inTime, outTime, location, type;
    boolean isSelect = true;

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient;

    String address;
    LocationManager locationManager;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uId;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ButterKnife.bind(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        uId = SharedPreference.getUserID(this);

        googleApiClient = getAPIClientInstance();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }

        time = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(new Date());
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tv_attendance_date.setText(date);
        tv_attendance_time.setText(time);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.clearCheck();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (rb.getText().equals("Attendance In Time")) {
                        type = "0";
                        inTime = time;
                        outTime = "";
                        isSelect = false;
                    } else {
                        type = "1";
                        inTime = "";
                        outTime = time;
                        isSelect = false;
                    }
//                    Toast.makeText(AttendanceActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @OnClick(R.id.iv_back)
    public void ivBack() {
        onBackPressed();
    }

    @OnClick(R.id.btn_submit_attendance)
    public void submitAttendance() {
        doValidation();
    }

    private GoogleApiClient getAPIClientInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300, 0, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void doValidation() {
        tv_attendance_location.setText(address);
        String finalLocation = tv_attendance_location.getText().toString().trim();

        if (date.equals("")) {
            Toasty.warning(this, "Get Current Date !!", Toast.LENGTH_SHORT,true).show();
        } else if (finalLocation.equals("")) {
            Toast.makeText(this, "Wait For 10 Sec... !!", Toast.LENGTH_SHORT).show();
        } else if (isSelect) {
            Toasty.warning(this, "Please Select Attendance Time !!", Toast.LENGTH_SHORT,true).show();
        } else {
            if (NetworkUtils.isNetworkAvailable(this))
                attendanceCall(finalLocation);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void attendanceCall(String finalLocation) {
        pd = new ProgressDialog(this);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.attendanceAddCall(uId, finalLocation, inTime, outTime, date, type);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                pd.dismiss();
                String str_response = new Gson().toJson(response.body());
                Log.e("" + TAG, "Response >>>>" + str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModule loginModule = response.body();

                        String message = null, errorCode = null;
                        if (loginModule != null) {
                            message = loginModule.getMsg();
                            errorCode = loginModule.getError_code();

                            if (errorCode.equalsIgnoreCase("1")) {

                                Toasty.success(AttendanceActivity.this, "Attendance Add Successful !!", Toast.LENGTH_SHORT,true).show();

                                Intent intent = new Intent(AttendanceActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (errorCode.equalsIgnoreCase("2")) {
                                Toasty.warning(AttendanceActivity.this, "Already Upload Attendance !!", Toast.LENGTH_SHORT, true).show();
                            } else {
                                Toasty.warning(AttendanceActivity.this, "Parameter Missing !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.warning(AttendanceActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                pd.dismiss();
                Toasty.error(AttendanceActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Log.d(TAG, String.valueOf(addresses.get(0)));
            address = addresses.get(0).getAddressLine(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(AttendanceActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    private void requestGPSSettings() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("", "All location settings are satisfied.");
//                        Toast.makeText(getApplication(), "Wait for 10 sec...", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings ");
                        try {
                            status.startResolutionForResult(AttendanceActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e("Applicationsett", e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("", "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created.");
                        Toast.makeText(getApplication(), "Location settings are inadequate, and cannot be fixed here", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        requestGPSSettings();
        getLocation();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }
}