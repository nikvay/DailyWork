package com.nikvay.daily_work.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.nikvay.daily_work.activity.LeaveActivity;
import com.nikvay.daily_work.adapter.LeaveAdapter;
import com.nikvay.daily_work.adapter.WorkAdapter;
import com.nikvay.daily_work.module.LeaveModule;
import com.nikvay.daily_work.module.SuccessModule;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;
import com.nikvay.daily_work.utils.NetworkUtils;
import com.nikvay.daily_work.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.HashSet;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LeaveFragment extends Fragment {

    Context mContext;
    ImageView iv_empty_list;
    FloatingActionButton fab_add_leave;

    RecyclerView recycler_view_leave;
    ArrayList<LeaveModule> leaveModuleArrayList = new ArrayList<>();
    LeaveAdapter leaveAdapter;
    View view;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    SharedPreferences sharedpreferences;
    public static String MyPREFERENCES = "Daily Work";

    Spinner spinner_filter_leave;
    ArrayList<String> filter_name = new ArrayList<>();
    ArrayList<String> filter_name_duplicate_name;


    public LeaveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_leave, container, false);
        mContext = getActivity();


        find_All_IDs(view);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String uId = SharedPreference.getUserID(mContext);
        String uType = SharedPreference.getUserType(mContext);

       /* if (uType.equals("0")) {
            spinner_filter_leave.setVisibility(View.GONE);
        }*/


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycler_view_leave.setLayoutManager(linearLayoutManager);
        recycler_view_leave.setHasFixedSize(true);

        if (NetworkUtils.isNetworkAvailable(mContext))
            leaveList(uId, uType);
        else
            NetworkUtils.isNetworkNotAvailable(mContext);


        events();

        return view;
    }//========== End onCreate () ==============


    private void find_All_IDs(View view) {
        recycler_view_leave = view.findViewById(R.id.recycler_view_leave);
        iv_empty_list = view.findViewById(R.id.iv_empty_list);
        fab_add_leave = view.findViewById(R.id.fab_add_leave);
        spinner_filter_leave=view.findViewById(R.id.spinner_filter_leave);
    }

    private void events() {
        fab_add_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LeaveActivity.class);
                startActivity(intent);
            }
        });



        spinner_filter_leave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                leaveAdapter = new LeaveAdapter(mContext, leaveModuleArrayList);
                recycler_view_leave.setAdapter(leaveAdapter);
                leaveAdapter.notifyDataSetChanged();

                String selected_name = spinner_filter_leave.getSelectedItem().toString();
                leaveAdapter.getFilter().filter(selected_name);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void leaveList(String uId, String uType) {
        pd = new ProgressDialog(mContext);
        pd.setMessage("Loading Please Wait...");
        pd.setCancelable(false);
        pd.show();

        Call<SuccessModule> call = apiInterface.leaveListCall(uId, uType);

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
                                leaveModuleArrayList = loginModule.getLeaveModuleArrayList();

                                filter_name.add("All");
                                for (int i=0;i<leaveModuleArrayList.size();i++)
                                {
                                    filter_name.add(leaveModuleArrayList.get(i).getName());
                                }

                                HashSet<String> listToSet = new HashSet<String>(filter_name);
                                filter_name_duplicate_name = new ArrayList<>(listToSet);

                                ArrayAdapter aa = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, filter_name_duplicate_name);
                                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner_filter_leave.setAdapter(aa);


                                if (leaveModuleArrayList.size() != 0) {
                                    recycler_view_leave.setVisibility(View.VISIBLE);
                                    iv_empty_list.setVisibility(View.GONE);
                                } else {
                                    recycler_view_leave.setVisibility(View.GONE);
                                    iv_empty_list.setVisibility(View.VISIBLE);
                                }

//                                enableSwipeToDeleteAndUndo(); // adapter on swipe

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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(mContext) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final LeaveModule item = leaveAdapter.getData().get(position);

                leaveAdapter.removeItem(position);


                Snackbar snackbar = Snackbar.make(view, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        leaveAdapter.restoreItem(item, position);
                        recycler_view_leave.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recycler_view_leave);
    }


}
