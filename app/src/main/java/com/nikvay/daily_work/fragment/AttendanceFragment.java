package com.nikvay.daily_work.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.daily_work.R;
import com.nikvay.daily_work.activity.AttendanceActivity;
import com.nikvay.daily_work.adapter.AttendanceAdapter;
import com.nikvay.daily_work.base.BaseFragment;
import com.nikvay.daily_work.module.AttendanceModule;
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AttendanceFragment extends BaseFragment {

    Context mContext;

    @BindView(R.id.iv_empty_list)
    ImageView iv_empty_list;

    @BindView(R.id.recycler_view_attendance)
    RecyclerView recycler_view_attendance;

    @BindView(R.id.spinner_filter_attendance)
    Spinner spinner_filter_attendance;

    ArrayList<AttendanceModule> attendanceModuleArrayList = new ArrayList<>();
    AttendanceAdapter attendanceAdapter;
    ArrayList<String> filter_name = new ArrayList<>();
    ArrayList<String> filter_name_duplicate_name;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";

    public AttendanceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);
        mContext = getActivity();

        ButterKnife.bind(this, view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String uId = SharedPreference.getUserID(mContext);
        String uType = SharedPreference.getUserType(mContext);

       /* if (uType.equals("0")) {
            spinner_filter_attendance.setVisibility(View.GONE);
        }
*/
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_attendance.setLayoutManager(linearLayoutManager);
        recycler_view_attendance.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext))
            attendanceList(uId, uType);
        else
            NetworkUtils.isNetworkNotAvailable(mContext);

        events();

        return view;
    }//========== End onCreate () ==============

    private void events() {
        spinner_filter_attendance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                attendanceAdapter = new AttendanceAdapter(mContext, attendanceModuleArrayList);
                recycler_view_attendance.setAdapter(attendanceAdapter);
                attendanceAdapter.notifyDataSetChanged();

                String selected_name = spinner_filter_attendance.getSelectedItem().toString();
                attendanceAdapter.getFilter().filter(selected_name);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.fab_add_attendance)
    public void fabAttendance() {
        Intent intent = new Intent(mContext, AttendanceActivity.class);
        startActivity(intent);
    }

    private void attendanceList(String uId, String uType) {
        showLoading();
        Call<SuccessModule> call = apiInterface.attendanceListCall(uId, uType);

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
                                attendanceModuleArrayList = loginModule.getAttendanceModuleArrayList();

                                filter_name.add("All");
                                for (int i = 0; i < attendanceModuleArrayList.size(); i++) {
                                    filter_name.add(attendanceModuleArrayList.get(i).getName());
                                }

                                HashSet<String> listToSet = new HashSet<String>(filter_name);
                                filter_name_duplicate_name = new ArrayList<>(listToSet);

                                ArrayAdapter aa = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, filter_name_duplicate_name);
                                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_filter_attendance.setAdapter(aa);

                                if (attendanceModuleArrayList.size() != 0) {
                                    recycler_view_attendance.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_attendance.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toasty.warning(mContext, "Missing List !!", Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    } else {
                        Toasty.warning(mContext, "Response Null !!", Toast.LENGTH_SHORT, true).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModule> call, Throwable t) {
                dismissLoading();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });
    }
}
