package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    //Firebase
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private FirebaseUser mCurrentUser;

    private TextView mStatus;
    private TextView mName;
    private CircleImageView mProfileImage;
    private Button mStatusUpdateBtn;
    private Button mChangeDpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mStatus = findViewById(R.id.settings_status_textView);
        mName = findViewById(R.id.settings_name_textView);
        mStatusUpdateBtn = findViewById(R.id.settings_change_status_button);
        mChangeDpBtn = findViewById(R.id.settings_change_profile_button);
        mProfileImage = findViewById(R.id.settings_profile_circleImageView);

        mChangeDpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });

        mStatusUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statusUpdateIntent = new Intent(SettingsActivity.this, StatusUpdateActivity.class);
                startActivity(statusUpdateIntent);
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mName.setText(dataSnapshot.child("name").getValue().toString());
                Log.d(TAG, "onDataChange: name fetched");
                mStatus.setText(dataSnapshot.child("status").getValue().toString());
                Log.d(TAG, "onDataChange: data fetched");
                String imageUri = dataSnapshot.child("profile_image").getValue().toString();
                Log.d(TAG, "onDataChange: image url fetched : " + imageUri);
                Picasso.get().load(imageUri).placeholder(R.drawable.avatar).into(mProfileImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult Started");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                Bitmap thumbnail = null;
                try {
                    thumbnail = new Compressor(this)
                            .setQuality(75)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .compressToBitmap(new File(resultUri.getPath()));
                } catch (Exception e) {
                    Log.d(TAG, "onActivityResult: " + e);
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                final byte[] thumbnailByteArray = stream.toByteArray();

                Log.d(TAG, "got the URI: " + resultUri.toString());
                final StorageReference dpRef = mStorage.child("profile_images/" + mCurrentUser.getUid() + ".jpg");
                final StorageReference thumbRef = mStorage.child("profile_images/thumbs/" + mCurrentUser.getUid() + ".jpg");

                UploadTask uploadTask = dpRef.putFile(resultUri);


                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, "Unable to Upload, Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Unable to Upload, Check Your Internet Connection");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UploadTask thumbUploadTask = thumbRef.putBytes(thumbnailByteArray);
                        thumbUploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                dpRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d(TAG, "new Photo uploaded : " + uri);
                                        mDatabase.child("profile_image").setValue(uri.toString());
                                        thumbRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                mDatabase.child("thumbnail_image").setValue(uri.toString());
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}


