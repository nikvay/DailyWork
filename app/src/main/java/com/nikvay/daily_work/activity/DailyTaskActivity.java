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
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.Const;
import com.nikvay.daily_work.utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyTaskActivity extends BaseActivity {

    @BindView(R.id.edit_project_name)
    EditText edit_project_name;

    @BindView(R.id.edit_project_task)
    EditText edit_project_task;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    String uId, dateTime;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        ButterKnife.bind(this);

        dateTime = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(new Date());

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        uId = SharedPreference.getUserID(this);
    }

    @OnClick(R.id.iv_back)
    public void backTask() {
        onBackPressed();
    }

    @OnClick(R.id.btn_submit_task)
    public void submitTask() {
        doValidation();
    }

    @OnClick(R.id.ll_task)
    public void llTask() {
        edit_project_task.requestFocus();
        Const.openKeyBord(DailyTaskActivity.this);
    }

    private void doValidation() {
        String name = edit_project_name.getText().toString().trim();
        String task = edit_project_task.getText().toString().trim();

        if (name.equals("")) {
            edit_project_name.setError("Please enter project name !!");
            edit_project_name.requestFocus();
        } else if (task.equals("")) {
            edit_project_task.setError("Please dill up task !!");
            edit_project_task.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this))
                taskCall(name, task);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void taskCall(String name, String task) {
        showLoading();
        Call<SuccessModule> call = apiInterface.taskAddCall(uId, name, task, dateTime);

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

                                Toasty.success(DailyTaskActivity.this, "Task Add Successful !!", Toast.LENGTH_SHORT,true).show();

                                Intent intent = new Intent(DailyTaskActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else if (errorCode.equalsIgnoreCase("2")) {
                                Toasty.warning(DailyTaskActivity.this, "Today Already Save Record !!", Toast.LENGTH_SHORT,true).show();

                            } else {
                                Toasty.warning(DailyTaskActivity.this, "Parameter Missing !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.warning(DailyTaskActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                dismissLoading();
                Toasty.error(DailyTaskActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }


}
