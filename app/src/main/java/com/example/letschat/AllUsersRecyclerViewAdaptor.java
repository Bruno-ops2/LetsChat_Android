package com.example.letschat;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class AllUsersRecyclerViewAdaptor extends RecyclerView.Adapter<AllUsersRecyclerViewAdaptor.ViewHolder> {

    private static final String TAG = "AllUsersRecyclerViewAda";
    private ArrayList<DataSnapshot> usersDatabaseResponse;
    private Context mContext;
    private Query query;

    public AllUsersRecyclerViewAdaptor(final ArrayList<DataSnapshot> usersDatabaseResponse, Context mContext) {
        this.usersDatabaseResponse = usersDatabaseResponse;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "OnCreateViewHolder called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called");
        String name = usersDatabaseResponse.get(position).child("name").getValue().toString();
        String status = usersDatabaseResponse.get(position).child("status").getValue().toString();
        Log.d(TAG, "view holder name : " + name);
        Log.d(TAG, "view holder status : " + status);
        holder.name.setText(name);
        holder.status.setText(status);
    }

    @Override
    public int getItemCount() {
        return usersDatabaseResponse.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.single_user_name);
            status = itemView.findViewById(R.id.single_user_status);
        }

    }
}
