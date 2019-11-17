package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class AllUsersActivity extends AppCompatActivity implements AllUsersRecyclerViewAdaptor.OnItemClick {

    private Toolbar mToolbar;
    private boolean done = false;
    private static String TAG = "AllUsersActivity";
    private ArrayList<Users> usersDatabaseResponse;
    private AllUsersRecyclerViewAdaptor allUsersRecyclerViewAdaptor;
    private Query query;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        Log.d(TAG, "OnCreate started");

        //progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading All Users");
        mProgressDialog.setMessage("Please wait while we fetch all users data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        usersDatabaseResponse = new ArrayList<>();
        Log.d(TAG, "onCreate: current size of arrayList" + usersDatabaseResponse.size());
        fetchData();

        //toolbar
        mToolbar = findViewById(R.id.all_users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d(TAG, "Toolbar set");

        RecyclerView recyclerView = findViewById(R.id.all_users_recyclerView);
        allUsersRecyclerViewAdaptor = new AllUsersRecyclerViewAdaptor(usersDatabaseResponse, this);
        allUsersRecyclerViewAdaptor.setOnClick(AllUsersActivity.this);
        recyclerView.setAdapter(allUsersRecyclerViewAdaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchData() {
        query = FirebaseDatabase.getInstance().getReference().child("Users");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onData started: " + done);
                if(done)
                    return;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d(TAG, dataSnapshot1.toString());
                    Log.d(TAG, "onDataChange: user key : " + dataSnapshot1.getKey().toString());

                    //if the key is equal to the user itself dont add that data snapshot to the array
                    if(dataSnapshot1.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        continue;

                    String uid = dataSnapshot.getKey();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String profile_image = dataSnapshot.child("thumbnail_image").getValue().toString();

                    Users user = new Users(uid, name, profile_image, status);

                    usersDatabaseResponse.add(user);
                    Log.d(TAG, "Item Count : " + usersDatabaseResponse.size());
                    allUsersRecyclerViewAdaptor.notifyDataSetChanged();
                    done = true;
                    Log.d(TAG, "onDataChange: " + done);
                    //Log.d(TAG, usersDatabaseResponse.get(getItemCount() - 1).child("name").getValue().toString());
                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mProgressDialog.hide();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent profileActivity = new Intent(AllUsersActivity.this, ProfileActivity.class);
        //sending userID to the intent
        profileActivity.putExtra("UserId", usersDatabaseResponse.get(position).getUid());
        startActivity(profileActivity);
    }
}