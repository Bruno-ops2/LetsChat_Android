package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private ImageView profileImage;
    private TextView name;
    private TextView status;
    private Button sendRequestBtn;
    private Button rejectRequestBtn;
    private String UID;
    private DatabaseReference mDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabase;
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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //binding views
        profileImage = findViewById(R.id.profileActivity_profile_imageview);
        name = findViewById(R.id.profileActivity_name_textview);
        status = findViewById(R.id.profileActivity_status_textview);
        sendRequestBtn = findViewById(R.id.profileActivity_send_request_button);
        rejectRequestBtn = findViewById(R.id.profileActivity_reject_request_button);
        rejectRequestBtn.setVisibility(View.INVISIBLE);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue().toString());
                status.setText(dataSnapshot.child("status").getValue().toString());
                Picasso.get().load(dataSnapshot.child("profile_image").getValue().toString()).placeholder(R.drawable.avatar).into(profileImage);
                mFriendReqDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String type;
                        if(dataSnapshot.child(mCurrentUser.getUid()).hasChild(UID)) {
                            type = dataSnapshot.child(mCurrentUser.getUid()).child(UID).child("request_type").getValue().toString();
                            if(type.equals("sent")) {
                                sendRequestBtn.setText("cancel friend request");
                                profileUserState = "req_sent";
                                sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.NoRed));
                            } else if (type.equals("received")) {
                                sendRequestBtn.setText("Accept Friend Request");
                                sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.yesGreen));
                                rejectRequestBtn.setVisibility(View.VISIBLE);
                                profileUserState = "received";
                            }
                        } else {
                            mFriendsDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.child(mCurrentUser.getUid()).hasChild(UID)) {
                                        sendRequestBtn.setText("UNFRIEND");
                                        sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.NoRed));
                                        profileUserState = "Friends";
                                    } else {
                                        sendRequestBtn.setText("Send Friend Request");
                                        sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                                        profileUserState = "notFriends";
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

                Map requestMap = new HashMap();
                requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + UID + "/request_type", null);
                requestMap.put("Friend_Request/" + UID + "/" + mCurrentUser.getUid() + "/request_type", null);

                mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                        sendRequestBtn.setEnabled(true);

                        if(databaseError != null) {
                            Toast.makeText(ProfileActivity.this, "Unable to reject request, please try again", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                        } else {
                            profileUserState = "notFriends";
                            sendRequestBtn.setText("Send Friend Request");
                            sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                            rejectRequestBtn.setVisibility(View.INVISIBLE);
                            progressDialog.dismiss();
                        }
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

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrentUser.getUid());
                    notificationData.put("type", "request");

                    DatabaseReference mNotificationDatabase = mDatabase.child("notifications").push();
                    String newNotificationID = mNotificationDatabase.getKey();

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + UID + "/request_type", "sent");
                    requestMap.put("Friend_Request/" + UID + "/" + mCurrentUser.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + UID + "/" + newNotificationID + "/from", mCurrentUser.getUid());
                    requestMap.put("notifications/" + UID + "/" + newNotificationID + "/type", "request");

                    mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            sendRequestBtn.setEnabled(true);
                            profileUserState = "req_sent";

                            if(databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "Unable to send request", Toast.LENGTH_SHORT).show();
                                profileUserState = "notFriends";
                                progressDialog.hide();
                            } else {
                                //renamed the button to cancel the request
                                sendRequestBtn.setText("Cancel Friend Request");
                                sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.NoRed));
                                progressDialog.dismiss();
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

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + UID + "/request_type", null);
                    requestMap.put("Friend_Request/" + UID + "/" + mCurrentUser.getUid() + "/request_type", null);

                    mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            sendRequestBtn.setEnabled(true);
                            profileUserState = "notFriends";

                            if(databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "Unable to cancel request", Toast.LENGTH_SHORT).show();
                                profileUserState = "req_sent";
                                progressDialog.hide();
                            } else {
                                sendRequestBtn.setText("Send Friend Request");
                                sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                                progressDialog.dismiss();
                            }
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

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + UID + "/request_type", null);
                    requestMap.put("Friend_Request/" + UID + "/" + mCurrentUser.getUid() + "/request_type", null);
                    requestMap.put("Friends/" + mCurrentUser.getUid() + "/" + UID, "Friends");
                    requestMap.put("Friends/" + UID + "/" + mCurrentUser.getUid(), "Friends");

                    mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "Unable to Accept Request", Toast.LENGTH_SHORT).show();
                                sendRequestBtn.setEnabled(true);
                                progressDialog.hide();
                            } else {
                                rejectRequestBtn.setVisibility(View.INVISIBLE);
                                profileUserState = "Friends";
                                sendRequestBtn.setText("UNFRIEND");
                                sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.NoRed));
                                sendRequestBtn.setEnabled(true);
                                progressDialog.dismiss();
                            }
                        }
                    });

                }

                //--------------------------FRIENDS SECTION---------------------------------

                if(profileUserState.equals("Friends")) {

                    final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setTitle("Un-Friending");
                    progressDialog.setMessage("Please wait while the user is being Un-Friended");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Map requestMap = new HashMap();
                    requestMap.put("Friends/" + mCurrentUser.getUid() + "/" + UID, null);
                    requestMap.put("Friends/" + UID + "/" + mCurrentUser.getUid(), null);

                    mDatabase.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            sendRequestBtn.setEnabled(true);

                            if(databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "Unable to Unfriend, please try again", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            } else {
                                profileUserState = "notFriends";
                                sendRequestBtn.setText("Send Friend Request");
                                sendRequestBtn.setBackgroundTintList(getColorStateList(R.color.colorAccent));
                                progressDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });

    }
}
