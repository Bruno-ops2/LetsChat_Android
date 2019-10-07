package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Toolbar mToolbar;
    private Button mSigninBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (TextInputLayout) findViewById(R.id.login_email_editText);
        mPassword = (TextInputLayout) findViewById(R.id.login_password_editText);
        mSigninBtn = (Button) findViewById(R.id.login_signin_btn);

        mToolbar = (Toolbar) findViewById(R.id.login_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "SignInWithEmail:success");
                                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();
                            }
                        });
            }
        });
    }
}
