package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mLoginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });

        mLoginBtn = (Button) findViewById(R.id.start_login_btn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginActivity = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(loginActivity);
            }
        });
    }


}
