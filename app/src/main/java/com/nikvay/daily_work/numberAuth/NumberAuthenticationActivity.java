package com.nikvay.daily_work.numberAuth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.nikvay.daily_work.R;
import com.nikvay.daily_work.utils.NetworkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NumberAuthenticationActivity extends AppCompatActivity {
    @BindView(R.id.editTextMobile)
    EditText editTextMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_authentication);

        ButterKnife.bind(this);

    }


    @OnClick(R.id.buttonContinue)
    public void getNumber() {
        doValidation();
    }

    private void doValidation() {
        String mobile = editTextMobile.getText().toString().trim();

        if (mobile.isEmpty() || mobile.length() < 10) {
            editTextMobile.setError("Enter a valid mobile");
            editTextMobile.requestFocus();
        } else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                Intent intent = new Intent(NumberAuthenticationActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("mobile", mobile);
                startActivity(intent);
            }else
                NetworkUtils.isNetworkNotAvailable(this);

        }
    }

}
