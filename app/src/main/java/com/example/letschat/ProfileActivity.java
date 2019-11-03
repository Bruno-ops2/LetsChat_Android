package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView profileImage;
    private TextView name;
    private TextView status;
    private Button sendRequestBtn;
    private String UID;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UID = getIntent().getExtras().getString("UserId");
        Log.d(TAG, "onCreate: UID : " + UID);

        //progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

        //binding views
        profileImage = findViewById(R.id.profileActivity_profile_imageview);
        name = findViewById(R.id.profileActivity_name_textview);
        status = findViewById(R.id.profileActivity_status_textview);
        sendRequestBtn = findViewById(R.id.profileActivity_send_reques_button);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
                Picasso.get().load(dataSnapshot.child("profile_image").getValue().toString()).placeholder(R.drawable.avatar).into(profileImage);
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Unable to fetch User Data", Toast.LENGTH_SHORT).show();
                mProgressDialog.hide();

            }
        });

        //making status bar trasparent
        Window w = getWindow();
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

    }
}
