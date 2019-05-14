package com.nikvay.daily_work.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.nikvay.daily_work.HomeActivity;
import com.nikvay.daily_work.R;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.tbruyelle.rxpermissions.RxPermissions;

import io.fabric.sdk.android.Fabric;
import rx.functions.Action1;

public class SplashActivity extends AppCompatActivity {

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    String loginOrNot;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Daily Work";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //=============== hide status bar ===============
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        loginOrNot = sharedpreferences.getString(SharedPreference.IS_LOGIN, "");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CheckPermissions();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent;
                    assert loginOrNot != null;
                    if (loginOrNot.equalsIgnoreCase("true")) {
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
//                        intent = new Intent(SplashActivity.this, NumberAuthenticationActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    }
                    finish();
                }
            }, 3000);
        }
    }

    private void CheckPermissions() {
        RxPermissions.getInstance(SplashActivity.this)
                .request(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        android.Manifest.permission.CAMERA,
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        initialize(aBoolean);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                });
    }

    private void initialize(Boolean isAppInitialized) {
        if (isAppInitialized) {
            Thread background = new Thread() {
                public void run() {

                    try {
                        sleep(2 * 1500);//4000 (4 Sec)
                        Intent intent;
                        assert loginOrNot != null;
                        if (loginOrNot.equalsIgnoreCase("true")) {
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
//                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        } else {
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
//                            intent = new Intent(SplashActivity.this, NumberAuthenticationActivity.class);
                            startActivity(intent);
//                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        }
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            background.start();
        } else {
            /* If one Of above permission not grant show alert (force to grant permission)*/
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("Alert");
            builder.setMessage("All permissions necessary");

            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CheckPermissions();
                }
            });

            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }
    }
}
