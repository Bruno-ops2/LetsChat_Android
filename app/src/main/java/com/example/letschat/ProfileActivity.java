package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView profileImage;
    private TextView name;
    private TextView status;
    private Button sendRequestBtn;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UID = getIntent().getExtras().getString("UserId");
        Log.d(TAG, "onCreate: UID : " + UID);

        //binding views
        profileImage = findViewById(R.id.profileActivity_profile_imageview);
        name = findViewById(R.id.profileActivity_name_textview);
        status = findViewById(R.id.profileActivity_status_textview);
        sendRequestBtn = findViewById(R.id.profileActivity_send_reques_button);

        //making status bar trasparent
        Window w = getWindow();
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);




    }
}
