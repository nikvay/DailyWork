package com.nikvay.daily_work.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.daily_work.HomeActivity;
import com.nikvay.daily_work.R;
import com.nikvay.daily_work.base.BaseActivity;
import com.nikvay.daily_work.module.LeaveModule;
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.Const;
import com.nikvay.daily_work.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaveDetailsActivity extends BaseActivity {

    @BindView(R.id.edit_remark)
    EditText edit_remark;

    @BindView(R.id.tv_leave_details_name)
    TextView tv_leave_details_name;

    @BindView(R.id.tv_leave_details_title)
    TextView tv_leave_details_title;

    @BindView(R.id.tv_leave_details_desc)
    TextView tv_leave_details_desc;

    @BindView(R.id.tv_leave_details_status)
    TextView tv_leave_details_status;

    @BindView(R.id.ll_leave_details_status)
    LinearLayout ll_leave_details_status;

    @BindView(R.id.edit_leave_details_sDate)
    EditText edit_leave_details_sDate;

    @BindView(R.id.edit_leave_details_eDate)
    EditText edit_leave_details_eDate;

    @BindView(R.id.ll_admin_show_leave)
    LinearLayout ll_admin_show_leave;

    @BindView(R.id.ll_leave_details_remark)
    LinearLayout ll_leave_details_remark;

    @BindView(R.id.tv_leave_details_remark)
    TextView tv_leave_details_remark;


    @BindView(R.id.spinner_lave_status)
    Spinner spinner_lave_status;

    ArrayList<String> selectLeaveStatus = new ArrayList<>();
    String sltStatus, selectStatus, leaveId;
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
        setContentView(R.layout.activity_leave_details);

        ButterKnife.bind(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        uId = SharedPreference.getUserID(this);
        uType = SharedPreference.getUserType(this);


        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                LeaveModule leaveModule = bundle.getParcelable("LEAVE_MODULE");
                assert leaveModule != null;
                leaveId = leaveModule.getMst_leaveid();
                String name = leaveModule.getName();
                String title = leaveModule.getTitle();
                String desc = leaveModule.getDesc();
                String startDate = leaveModule.getStartDate();
                String endDate = leaveModule.getEndDate();
                String status = leaveModule.getStatus();
                String remark = leaveModule.getRemark();

                if (uType.equals("1")) {
//                    ll_leave_details_status.setVisibility(View.GONE);
                    ll_admin_show_leave.setVisibility(View.VISIBLE);

                    if (status.equals("Pending")) {
                        ll_admin_show_leave.setVisibility(View.VISIBLE);
                    } else {
                        ll_admin_show_leave.setVisibility(View.GONE);
                    }
                } else {
//                    ll_leave_details_status.setVisibility(View.VISIBLE);
                    ll_admin_show_leave.setVisibility(View.GONE);
                }

                if (remark.equals("")) {
                    ll_leave_details_remark.setVisibility(View.GONE);
                } else {
                    ll_leave_details_remark.setVisibility(View.VISIBLE);
                }

                tv_leave_details_name.setText(name);
                tv_leave_details_title.setText(title);
                tv_leave_details_desc.setText(desc);
                tv_leave_details_status.setText(status);
                edit_leave_details_sDate.setText(startDate);
                edit_leave_details_eDate.setText(endDate);
                tv_leave_details_remark.setText(remark);
            }
        }

        spinnerSelect();

        events();
    }

    private void events() {
        edit_leave_details_sDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                CharSequence strDate = null;
                                Time chosenDate = new Time();
                                chosenDate.set(dayOfMonth, monthOfYear, year);

                                long dateAttendance = chosenDate.toMillis(true);
                                strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                                edit_leave_details_sDate.setText(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        edit_leave_details_eDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(LeaveDetailsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                CharSequence strDate = null;
                                Time chosenDate = new Time();
                                chosenDate.set(dayOfMonth, monthOfYear, year);

                                long dateAttendance = chosenDate.toMillis(true);
                                strDate = DateFormat.format("dd-MM-yyyy", dateAttendance);

                                edit_leave_details_eDate.setText(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }


    @OnClick(R.id.ll_remark)
    public void remark() {
        edit_remark.requestFocus();
        Const.openKeyBord(LeaveDetailsActivity.this);
    }

    private void spinnerSelect() {
        selectLeaveStatus.add(0, "Pending");
        selectLeaveStatus.add(1, "Approval");
        selectLeaveStatus.add(2, "Cancel");

        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, selectLeaveStatus);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_lave_status.setAdapter(aa);

        spinner_lave_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sltStatus = parent.getItemAtPosition(position).toString();
                selectStatus = spinner_lave_status.getSelectedItem().toString();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.btn_update_leave)
    public void updateLeave() {

        doValidation();
    }

    private void doValidation() {
        String sDate = edit_leave_details_sDate.getText().toString().trim();
        String eDate = edit_leave_details_eDate.getText().toString().trim();
        String remark = edit_remark.getText().toString().trim();

        if (sDate.equals("")) {
            Toast.makeText(this, "Please fill up date !!", Toast.LENGTH_SHORT).show();
        } else if (eDate.equals("")) {
            Toast.makeText(this, "Please fill up date !!", Toast.LENGTH_SHORT).show();
        } else if (selectStatus.equals("")) {
            Toast.makeText(this, "Please select status !!", Toast.LENGTH_SHORT).show();
        } else if (remark.equals("")) {
            edit_remark.setError("Please enter remark !!");
            edit_remark.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this))
                leaveUpdateCall(sDate, eDate, remark);
            else
                NetworkUtils.isNetworkNotAvailable(this);
        }
    }

    private void leaveUpdateCall(String sDate, String eDate, String remark) {
        showLoading();
        Call<SuccessModule> call = apiInterface.leaveUpdateCall(uId, leaveId, sDate, eDate, selectStatus, remark);

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

                                Toasty.success(LeaveDetailsActivity.this, "Update Leave Successful !!", Toast.LENGTH_SHORT, true).show();

                                Intent intent = new Intent(LeaveDetailsActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toasty.warning(LeaveDetailsActivity.this, "Parameter Missing !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.warning(LeaveDetailsActivity.this, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                dismissLoading();
                Toasty.error(LeaveDetailsActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}
