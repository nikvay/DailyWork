package com.nikvay.daily_work.base;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nikvay.daily_work.R;

import java.net.UnknownHostException;

import retrofit2.HttpException;

public abstract class BaseActivity extends AppCompatActivity {
//    protected CompositeDisposable disposables = new CompositeDisposable();
    private ProgressDialog progressDialog;

//    @Inject
//    Navigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        disposables.dispose();

        if (progressDialog != null)
            progressDialog.dismiss();
    }

    protected void showLoading() {
        progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.loading));
    }

    protected void dismissLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected Boolean isLoaderShowing() {
        return progressDialog != null && progressDialog.isShowing();
    }

    protected void showError(Throwable e) {
        if (e instanceof UnknownHostException) {
            Toast.makeText(this, "Internet connection not available\nPlease check", Toast.LENGTH_SHORT).show();
            return;
        } else if (e instanceof HttpException) {
            HttpException exception = (HttpException) e;
            switch (exception.code()) {
                case 400:
//                    navigator.navigateToLoginActivity(this);
                    return;
                default:
                    break;
            }
        }
        new AlertDialog.Builder(this)
                .setMessage(e.getLocalizedMessage())
                .setTitle("Error")
                .setPositiveButton(android.R.string.ok, null)
                .setCancelable(false)
                .create()
                .show();
    }
}