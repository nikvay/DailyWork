package com.nikvay.daily_work.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.daily_work.HomeActivity;
import com.nikvay.daily_work.R;
import com.nikvay.daily_work.base.BaseActivity;
import com.nikvay.daily_work.module.LoginModule;
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.edit_username)
    EditText edit_username;

    @BindView(R.id.edit_password)
    EditText edit_password;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";

    ArrayList<LoginModule> loginModuleArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);


        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_login)
    public void submit() {
        doValidation();
    }

    private void doValidation() {
        String userName = edit_username.getText().toString().trim();
        String userPassword = edit_password.getText().toString().trim();

        if (userName.equals("")) {
            edit_username.setError("Please enter user name !!");
            edit_username.requestFocus();
        } else if (userName.length() < 4) {
            edit_username.setError("User name too short !!");
            edit_username.requestFocus();
        } else if (userPassword.equals("")) {
            edit_password.setError("Please enter user password !!");
            edit_password.requestFocus();
        } else if (userPassword.length() < 4) {
            edit_password.setError("User password too short !!");
            edit_password.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this))
                loginCall(userName, userPassword);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void loginCall(String userName, String userPassword) {
        showLoading();
        Call<SuccessModule> call = apiInterface.loginCall(userName, userPassword);

        call.enqueue(new Callback<SuccessModule>() {
            @Override
            public void onResponse(Call<SuccessModule> call, Response<SuccessModule> response) {
                dismissLoading();
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
                                loginModuleArrayList = loginModule.getLoginModuleArrayList();

                                for (LoginModule loginModule1 : loginModuleArrayList) {
                                    String userId = loginModule1.getEmp_id();
                                    String userType = loginModule1.getType();
                                    String fullName = loginModule1.getFullname();
                                    String userName = loginModule1.getName();
                                    String userEmail = loginModule1.getEmail();
                                    String contactNumber = loginModule1.getPhone();
                                    String password = loginModule1.getPassword();

                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(SharedPreference.IS_LOGIN, "true");
                                    editor.putString(SharedPreference.USER_FULL_NAME, fullName);
                                    editor.putString(SharedPreference.USER_NAME, userName);
                                    editor.putString(SharedPreference.USER_PASSWORD, password);
                                    editor.putString(SharedPreference.USER_EMAIL, userEmail);
                                    editor.putString(SharedPreference.USER_CONTACT, contactNumber);
                                    editor.apply();

                                    SharedPreference.putUserID(LoginActivity.this, userId);
                                    SharedPreference.putUserType(LoginActivity.this, userType);

                                    Toasty.success(LoginActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT, true).show();

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toasty.warning(LoginActivity.this, "User Not Register !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.warning(LoginActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                dismissLoading();
                Toasty.error(LoginActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}
