package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

public class StatusUpdateActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mtextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);
        mtextInputLayout = (TextInputLayout) findViewById(R.id.status_update_editText);
        mToolbar = (Toolbar) findViewById(R.id.status_update_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Status Update");
     }
}
