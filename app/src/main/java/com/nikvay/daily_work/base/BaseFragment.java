package com.nikvay.daily_work.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.nikvay.daily_work.R;

import java.net.UnknownHostException;

import retrofit2.HttpException;

public abstract class BaseFragment extends Fragment {
//    protected CompositeDisposable disposables = new CompositeDisposable();
    private ProgressDialog progressDialog;

//    @Inject
//    Navigator navigator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        AndroidSupportInjection.inject(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        disposables.dispose();

        if (progressDialog != null)
            progressDialog.dismiss();
    }

    protected void showLoading() {
        if (getActivity() != null) {
            progressDialog = ProgressDialog.show(getActivity(), "", getActivity().getResources().getString(R.string.loading));
        }
    }

    protected void dismissLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected void showError(Throwable e) {
        if (getActivity() != null) {
            if (e instanceof UnknownHostException) {
                Toast.makeText(getActivity(), "Internet connection not available\nPlease check", Toast.LENGTH_SHORT).show();
                return;
            } else if (e instanceof HttpException) {
                HttpException exception = (HttpException) e;
                switch (exception.code()) {
                    case 400:
//                        navigator.navigateToLoginActivity(getActivity());
                        return;
                    default:
                        break;
                }
            }

            new AlertDialog.Builder(getActivity())
                    .setMessage(e.getLocalizedMessage())
                    .setTitle("Error")
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false)
                    .create()
                    .show();
        }
    }
}