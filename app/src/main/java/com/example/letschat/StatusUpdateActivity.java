package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StatusUpdateActivity extends AppCompatActivity {

    private static final String TAG = "StatusUpdateActivity";

    private Toolbar mToolbar;
    private TextInputLayout mtextInputLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button mUpdateButton;
    private ProgressDialog mProgressDiaglog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_update);
        mtextInputLayout = (TextInputLayout) findViewById(R.id.status_update_editText);
        mToolbar = (Toolbar) findViewById(R.id.status_update_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Status Update");

        mUpdateButton = (Button) findViewById(R.id.status_update_button);

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newStatus = mtextInputLayout.getEditText().getText().toString();
                if(newStatus == null || newStatus.isEmpty()) {
                    Toast.makeText(StatusUpdateActivity.this, "Empty Status is not allowed", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressDiaglog = new ProgressDialog(StatusUpdateActivity.this);
                mProgressDiaglog.setTitle("Updating Status");
                mProgressDiaglog.setMessage("Please wait while we update your status");
                mProgressDiaglog.setCanceledOnTouchOutside(false);
                mProgressDiaglog.show();
                mAuth = FirebaseAuth.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("status");

                mDatabase.setValue(newStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDiaglog.dismiss();
                            Log.d(TAG, "Status Updated Successfully");
                            Toast.makeText(StatusUpdateActivity.this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "Unable to Update Status");
                            mProgressDiaglog.hide();
                            Toast.makeText(StatusUpdateActivity.this, "Unable to Update Status, Please try again later", Toast.LENGTH_LONG).show();
                        }
                    }
                });


            }
        });
    }
}
