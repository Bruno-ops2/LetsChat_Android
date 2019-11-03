package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Struct;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView profileImage;
    private TextView name;
    private TextView status;
    private Button sendRequestBtn;
    private String UID;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private ProgressDialog mProgressDialog;
    private FirebaseUser mCurrentUser;

    private String profileUserState = "notFriends";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UID = getIntent().getExtras().getString("UserId");
        Log.d(TAG, "onCreate: UID : " + UID);

        //making status bar trasparent
        Window w = getWindow();
        w.setFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        //progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while we load the user data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        Log.d(TAG, "onCreate: " + mFriendReqDatabase.toString());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //binding views
        profileImage = findViewById(R.id.profileActivity_profile_imageview);
        name = findViewById(R.id.profileActivity_name_textview);
        status = findViewById(R.id.profileActivity_status_textview);
        sendRequestBtn = findViewById(R.id.profileActivity_send_reques_button);

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendRequestBtn.setEnabled(false);
                Log.d(TAG, "onClick: button disables");
                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("Sending Request");
                progressDialog.setMessage("Please wait while the request is being sent");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                //--------------------------NOT FRIENDS SECTION---------------------------------

                if(profileUserState.equals("notFriends")) {

                    Log.d(TAG, "onClick: inside not friends");

                    mFriendReqDatabase
                            .child(mCurrentUser.getUid())
                            .child(UID)
                            .child("request_type")
                            .setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {
                                        mFriendReqDatabase
                                                .child(UID)
                                                .child(mCurrentUser.getUid())
                                                .child("request_type")
                                                .setValue("received");

                                        profileUserState = "req_sent";
                                        //renamed the button to cancel the request
                                        sendRequestBtn.setText("Cancel Friend Request");
                                        progressDialog.dismiss();

                                    }

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this,
                                                "Unable to send request, Try Agin",
                                                Toast.LENGTH_SHORT).show();
                                        sendRequestBtn.setEnabled(true);
                                        profileUserState = "not_friends";
                                        progressDialog.hide();
                                    }
                                }
                            });

                }
            }
        });

    }
}
