package com.nikvay.daily_work.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveActivity extends BaseActivity {


    ImageView iv_back;
    EditText edit_leave_sub, edt_leave_sDate, edt_leave_eDate, edit_leave_desc;
    Button btn_submit_leave;
    LinearLayout ll_leave_desc;

    private int mYear, mMonth, mDay; // datePicker

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    String uId, uType;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);


        find_All_IDs();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        uId = SharedPreference.getUserID(this);

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        edt_leave_sDate.setText(date);
        edt_leave_eDate.setText(date);

        events();
    }

    private void find_All_IDs() {
        iv_back = findViewById(R.id.iv_back);
        edit_leave_sub = findViewById(R.id.edit_leave_sub);
        edt_leave_sDate = findViewById(R.id.edt_leave_sDate);
        edt_leave_eDate = findViewById(R.id.edt_leave_eDate);
        edit_leave_desc = findViewById(R.id.edit_leave_desc);
        btn_submit_leave = findViewById(R.id.btn_submit_leave);
        ll_leave_desc = findViewById(R.id.ll_leave_desc);

    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_submit_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doValidation();
            }
        });

        edt_leave_sDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                CharSequence strDate = null;
                                Time chosenDate = new Time();
                                chosenDate.set(dayOfMonth, monthOfYear, year);

                                long dateAttendance = chosenDate.toMillis(true);
                                strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                                edt_leave_sDate.setText(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        edt_leave_eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                CharSequence strDate = null;
                                Time chosenDate = new Time();
                                chosenDate.set(dayOfMonth, monthOfYear, year);

                                long dateAttendance = chosenDate.toMillis(true);
                                strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                                edt_leave_eDate.setText(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        ll_leave_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_leave_desc.requestFocus();
                Const.openKeyBord(LeaveActivity.this);
            }
        });

    }

    private void doValidation() {
        String leaveSub = edit_leave_sub.getText().toString().trim();
        String sDate = edt_leave_sDate.getText().toString().trim();
        String eDate = edt_leave_eDate.getText().toString().trim();
        String desc = edit_leave_desc.getText().toString().trim();

        if (leaveSub.equals("")) {
            edit_leave_sub.setError("Please enter subject !!");
            edit_leave_sub.requestFocus();
        } else if (sDate.equals("")) {
            Toast.makeText(this, "Please fill up date !!", Toast.LENGTH_SHORT).show();
        } else if (eDate.equals("")) {
            Toast.makeText(this, "Please fill up date !!", Toast.LENGTH_SHORT).show();
        } else if (desc.equals("")) {
            edit_leave_desc.setError("Please enter description !!");
            edit_leave_desc.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this))
                leaveCall(leaveSub, desc, sDate, eDate);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void leaveCall(String leaveSub, String desc, String sDate, String eDate) {
        showLoading();
        Call<SuccessModule> call = apiInterface.leaveAddCall(uId, leaveSub, desc, sDate, eDate, "Pending", "");

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

                                Toasty.success(LeaveActivity.this, "Leave Add Successful !!", Toast.LENGTH_SHORT,true).show();

                                Intent intent = new Intent(LeaveActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toasty.warning(LeaveActivity.this, "Parameter Missing !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.warning(LeaveActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                dismissLoading();
                Toasty.error(LeaveActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
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
