package com.nikvay.daily_work.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nikvay.daily_work.R;
import com.nikvay.daily_work.activity.DailyTaskActivity;
import com.nikvay.daily_work.adapter.AttendanceAdapter;
import com.nikvay.daily_work.adapter.WorkAdapter;
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.module.WorkModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashSet;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class DailyWorkFragment extends Fragment {
    Context mContext;
    ImageView iv_empty_list;
    FloatingActionButton fab_add_daily_work;

    RecyclerView recycler_view_daily_work;
    ArrayList<WorkModule> workModuleArrayList = new ArrayList<>();
    WorkAdapter workAdapter;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String token, isSelectUser;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";


    Spinner spinner_filter_daily_work;
    ArrayList<String> filter_name = new ArrayList<>();
    ArrayList<String> filter_name_duplicate_name;

    public DailyWorkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_work, container, false);
        mContext = getActivity();

        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String uId = SharedPreference.getUserID(mContext);
        String uType = SharedPreference.getUserType(mContext);

      /*  if (uType.equals("0")) {
            spinner_filter_daily_work.setVisibility(View.GONE);
        }*/

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_daily_work.setLayoutManager(linearLayoutManager);
        recycler_view_daily_work.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext))
            workList(uId, uType);
        else
            NetworkUtils.isNetworkNotAvailable(mContext);


        events();

        return view;
    }//========== End onCreate () ==============

    private void find_All_IDs(View view) {
        recycler_view_daily_work = view.findViewById(R.id.recycler_view_daily_work);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
        fab_add_daily_work = view.findViewById(R.id.fab_add_daily_work);
        spinner_filter_daily_work=view.findViewById(R.id.spinner_filter_daily_work);
    }

    private void events() {
        fab_add_daily_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DailyTaskActivity.class);
                startActivity(intent);
            }
        });


        spinner_filter_daily_work.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                workAdapter = new WorkAdapter(mContext, workModuleArrayList);
                recycler_view_daily_work.setAdapter(workAdapter);
                workAdapter.notifyDataSetChanged();

                String selected_name = spinner_filter_daily_work.getSelectedItem().toString();
                workAdapter.getFilter().filter(selected_name);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void workList(String uId, String uType) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.taskListCall(uId, uType);

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
                                workModuleArrayList = loginModule.getWorkModuleArrayList();


                                filter_name.add("All");
                                for (int i=0;i<workModuleArrayList.size();i++)
                                {
                                    filter_name.add(workModuleArrayList.get(i).getName());
                                }

                                HashSet<String> listToSet = new HashSet<String>(filter_name);
                                filter_name_duplicate_name = new ArrayList<>(listToSet);

                                ArrayAdapter aa = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, filter_name_duplicate_name);
                                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_filter_daily_work.setAdapter(aa);


                                if (workModuleArrayList.size() != 0) {
                                    recycler_view_daily_work.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_daily_work.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

                                workAdapterClick(); // adapter on click

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
                pd.dismiss();
                Toasty.error(mContext, "" + t.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

    }

    private void workAdapterClick() {
        workAdapter.setOnItemClickListener(new WorkAdapter.OnItemClickListener() {
            @Override
            public void onAdapterClick(WorkModule workModule, int position) {
                String title = workModule.getMst_projectname();
                String desc = workModule.getMst_projecttask();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_dialog_work, null);
                builder.setView(dialogView);
                builder.setCancelable(true);

                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();

                TextView tv_word_dia_title = dialogView.findViewById(R.id.tv_word_dia_title);
                TextView tv_word_dia_desc = dialogView.findViewById(R.id.tv_word_dia_desc);

                tv_word_dia_title.setText(title);
                tv_word_dia_desc.setText(desc);
            }
        });
    }




}
