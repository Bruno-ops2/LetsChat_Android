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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView profileImage;
    private TextView name;
    private TextView status;
    private Button sendRequestBtn;
    private Button rejectRequestBtn;
    private String UID;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
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
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //binding views
        profileImage = findViewById(R.id.profileActivity_profile_imageview);
        name = findViewById(R.id.profileActivity_name_textview);
        status = findViewById(R.id.profileActivity_status_textview);
        sendRequestBtn = findViewById(R.id.profileActivity_send_request_button);
        rejectRequestBtn = findViewById(R.id.profileActivity_reject_request_button);
        rejectRequestBtn.setVisibility(View.INVISIBLE);

        mUsersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
                Picasso.get().load(dataSnapshot.child("profile_image").getValue().toString()).placeholder(R.drawable.avatar).into(profileImage);
                mFriendReqDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String type;
                        if(dataSnapshot.child(mCurrentUser.getUid()).hasChild(UID)) {
                            type = dataSnapshot.child(mCurrentUser.getUid()).child(UID).child("request_type").getValue().toString();
                            if(type.equals("sent")) {
                                sendRequestBtn.setText("cancel friend request");
                                profileUserState = "req_sent";
                                sendRequestBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
                            } else if (type.equals("received")) {
                                sendRequestBtn.setText("Accept Friend Request");
                                sendRequestBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));
                                rejectRequestBtn.setVisibility(View.VISIBLE);
                                profileUserState = "received";
                            }
                        } else {
                            mFriendsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(mCurrentUser.getUid()).hasChild(UID)) {
                                        sendRequestBtn.setText("UNFRIEND");
                                        sendRequestBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
                                        profileUserState = "Friends";
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        mProgressDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Unable to fetch User Data", Toast.LENGTH_SHORT).show();
                mProgressDialog.hide();

            }
        });

        rejectRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setTitle("Rejecting Request");
                progressDialog.setMessage("Please wait while the request is being rejected");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mFriendReqDatabase
                        .child(mCurrentUser.getUid())
                        .child(UID).child("request_type")
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mFriendReqDatabase
                                        .child(UID)
                                        .child(mCurrentUser.getUid())
                                        .child("request_type")
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                profileUserState = "notFriends";
                                                sendRequestBtn.setText("Send Friend Request");
                                                sendRequestBtn.setEnabled(true);
                                                rejectRequestBtn.setVisibility(View.INVISIBLE);
                                                progressDialog.dismiss();
                                            }
                                        });
                            }
                        });


            }
        });

        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendRequestBtn.setEnabled(false);
                Log.d(TAG, "onClick: button disables");

                //--------------------------NOT FRIENDS SECTION---------------------------------

                if(profileUserState.equals("notFriends")) {

                    Log.d(TAG, "onClick: inside not friends");
                    final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setTitle("Sending Request");
                    progressDialog.setMessage("Please wait while the request is being sent");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

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
                                        sendRequestBtn.setEnabled(true);
                                        sendRequestBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
                                        progressDialog.dismiss();

                                    }

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this,
                                                "Unable to send request, Try Agin",
                                                Toast.LENGTH_SHORT).show();
                                        sendRequestBtn.setEnabled(true);
                                        profileUserState = "notFriends";
                                        progressDialog.hide();
                                    }
                                }
                            });

                }

                //--------------------------REQUEST SENT SECTION---------------------------------

                if(profileUserState.equals("req_sent")) {

                    final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setTitle("Canceling Request");
                    progressDialog.setMessage("Please wait while the request is being canceled");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mFriendReqDatabase
                            .child(mCurrentUser.getUid())
                            .child(UID).child("request_type")
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendReqDatabase
                                            .child(UID)
                                            .child(mCurrentUser.getUid())
                                            .child("request_type")
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    profileUserState = "notFriends";
                                                    sendRequestBtn.setText("Send Friend Request");
                                                    sendRequestBtn.setEnabled(true);
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            });

                }

                //--------------------------REQUEST RECEIVED SECTION---------------------------------

                if(profileUserState.equals("received")) {

                    final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setTitle("Accepting Request");
                    progressDialog.setMessage("Please wait while the request is being Accepted");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mFriendReqDatabase
                            .child(mCurrentUser.getUid())
                            .child(UID)
                            .child("request_type")
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    profileUserState = "Friends";
                                    mFriendReqDatabase
                                            .child(UID)
                                            .child(mCurrentUser.getUid())
                                            .child("request_type")
                                            .removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mFriendsDatabase
                                                            .child(mCurrentUser.getUid())
                                                            .child(UID)
                                                            .setValue("Friends")
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mFriendsDatabase
                                                                            .child(UID)
                                                                            .child(mCurrentUser.getUid())
                                                                            .setValue("Friends")
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    rejectRequestBtn.setVisibility(View.INVISIBLE);
                                                                                    sendRequestBtn.setText("UNFRIEND");
                                                                                    sendRequestBtn.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
                                                                                    sendRequestBtn.setEnabled(false);
                                                                                    progressDialog.dismiss();
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });

                }

            }
        });

    }
}
