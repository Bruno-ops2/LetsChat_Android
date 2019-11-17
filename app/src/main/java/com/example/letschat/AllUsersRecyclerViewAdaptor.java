package com.example.letschat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AllUsersRecyclerViewAdaptor extends RecyclerView.Adapter<AllUsersRecyclerViewAdaptor.ViewHolder> {

    private static final String TAG = "AllUsersRecyclerViewAda";
    private ArrayList<Users> usersDatabaseResponse;
    private Context mContext;
    private Query query;

    //interface for OnItemClick
    public interface OnItemClick {
        void onItemClick(int position);
    }

    private OnItemClick onClick;

    public AllUsersRecyclerViewAdaptor(final ArrayList<Users> usersDatabaseResponse, Context mContext) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder called");
        String name = usersDatabaseResponse.get(position).getName();
        String status = usersDatabaseResponse.get(position).getStatus();
        String profile_image = usersDatabaseResponse.get(position).getProfile_image();
        Log.d(TAG, "view holder name : " + name);
        Log.d(TAG, "view holder status : " + status);
        holder.name.setText(name);
        holder.status.setText(status);
        Picasso.get().load(profile_image).placeholder(R.drawable.avatar).into(holder.profileImage);
        holder.profileImage.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersDatabaseResponse.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView status;
        ImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.single_user_name);
            status = itemView.findViewById(R.id.single_user_status);
            profileImage = itemView.findViewById(R.id.single_user_profile);
        }

    }

    public void setOnClick(OnItemClick onClick) {
        this.onClick = onClick;
    }
}
