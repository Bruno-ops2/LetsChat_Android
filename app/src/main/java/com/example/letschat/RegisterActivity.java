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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mSignupBtn;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name_editText);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email_editText);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password_editText);
        mSignupBtn = (Button) findViewById(R.id.reg_signup_btn);
        mAuth = FirebaseAuth.getInstance();
        mProgressDialog = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.reg_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getEditText().getText().toString();
                final String password = mPassword.getEditText().getText().toString();
                final String name = mDisplayName.getEditText().getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mProgressDialog.setTitle("Registering User");
                    mProgressDialog.setMessage("Please wait while we create your account");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                        String uid = mAuth.getCurrentUser().getUid().toString();
                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                        HashMap<String, String> regUser = new HashMap<>();
                                        regUser.put("name", name);
                                        regUser.put("status", "Hey there! I'm using Lets Chat");
                                        regUser.put("profile_image", "default");
                                        regUser.put("thumbnail_image", "default_image");
                                        regUser.put("device_token", deviceToken);
                                        mDatabase.setValue(regUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                    Log.d(TAG, "createUserWithEmail:success");
                                                    Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                    Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                                                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(mainActivity);
                                                    finish();
                                                }
                                            }
                                        });

                                    } else {
                                        mProgressDialog.hide();
                                        Log.e(TAG, "createUserWithEmail:failure");
                                        Toast.makeText(RegisterActivity.this, "Failed, Authentication Problem", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill the provided fields properly", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
