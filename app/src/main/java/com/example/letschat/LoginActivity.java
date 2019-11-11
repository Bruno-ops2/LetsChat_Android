package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Toolbar mToolbar;
    private Button mSigninBtn;
    private ProgressDialog mProgressDiaglog;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (TextInputLayout) findViewById(R.id.login_email_editText);
        mPassword = (TextInputLayout) findViewById(R.id.login_password_editText);
        mSigninBtn = (Button) findViewById(R.id.login_signin_btn);
        mProgressDiaglog = new ProgressDialog(this);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolbar = (Toolbar) findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mProgressDiaglog.setTitle("Logging in");
                    mProgressDiaglog.setMessage("Please wait while we check your Email and Password");
                    mProgressDiaglog.setCanceledOnTouchOutside(false);
                    mProgressDiaglog.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        String currentUserId = mAuth.getCurrentUser().getUid();

                                        mUsersDatabase
                                                .child(currentUserId)
                                                .child("device_token")
                                                .setValue(deviceToken)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mProgressDiaglog.dismiss();
                                                        Log.d(TAG, "SignInWithEmail:success");
                                                        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                                        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(mainActivity);
                                                        finish();
                                                    }
                                                });

                                    } else {
                                        mProgressDiaglog.hide();
                                        Log.d(TAG, "SignInWithEmail:failure");
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill the provided fields properly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
