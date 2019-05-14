package com.nikvay.daily_work;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.daily_work.fragment.AttendanceFragment;
import com.nikvay.daily_work.fragment.DailyWorkFragment;
import com.nikvay.daily_work.fragment.LeaveFragment;
import com.nikvay.daily_work.activity.LoginActivity;
import com.nikvay.daily_work.network.ApiClient;
import com.nikvay.daily_work.network.ApiInterface;
import com.nikvay.daily_work.shared_pref.SharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.tv_title_name)
    TextView tv_title_name;

     TextView tv_user_name;

    ImageView iv_log_out;
    private boolean doubleBackToExitPressedOnce = false;

    //======Interface Declaration=========
    String TAG = getClass().getSimpleName();
    ApiInterface apiInterface;
    ProgressDialog pd;
    String uName;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static String MyPREFERENCES = "Daily Work";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        uName = sharedpreferences.getString(SharedPreference.USER_FULL_NAME, "");
        String loginOrNot = sharedpreferences.getString(SharedPreference.IS_LOGIN, "");


        //loading the default fragment
        tv_title_name.setText("Attendance");
        loadFragment(new AttendanceFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @OnClick(R.id.iv_log_out)
    public void logOut() {
        openDialog();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_attendance:
                tv_title_name.setText("Attendance");
                fragment = new AttendanceFragment();
                break;

            case R.id.navigation_daily_task:
                tv_title_name.setText("Daily Task List");
                fragment = new DailyWorkFragment();
                break;

            case R.id.navigation_leave:
                tv_title_name.setText("Leave List");
                fragment = new LeaveFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_logout, null);
        builder.setView(dialogView);
        builder.setCancelable(true);

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

         tv_user_name = dialogView.findViewById(R.id.tv_user_name);
        Button btn_no = dialogView.findViewById(R.id.btn_no);
        Button btn_yes = dialogView.findViewById(R.id.btn_yes);

        tv_user_name.setText(uName);

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(SharedPreference.IS_LOGIN, "false");
                editor.apply();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
//                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                alertDialog.dismiss();

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        doubleBackPressLogic();
    }

    // ============ End Double tab back press logic =================
    private void doubleBackPressLogic() {
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toasty.info(this, "Please double click to exit !!", Toast.LENGTH_SHORT, true).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1000);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}



