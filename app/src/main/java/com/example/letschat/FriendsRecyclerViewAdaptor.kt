package com.example.letschat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendsRecyclerViewAdaptor(
        val friendsDatabaseResponse: ArrayList<Friends>,
        val mContext: Context
    ) : RecyclerView.Adapter<FriendsRecyclerViewAdaptor.ViewHolder>() {

    val TAG: String = "FriendsAdaptor"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.single_friend, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder called")
        val name = friendsDatabaseResponse[position].name
        val profileImage = friendsDatabaseResponse[position].profileImage
        Log.d(TAG, "view holder name ; $name")
        Log.d(TAG, "view holder name ; $profileImage")

        holder.name.text = name
        Picasso.get().load(profileImage).placeholder(R.drawable.avatar).into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return friendsDatabaseResponse.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.single_friend_name)
        val lastSeen: TextView = view.findViewById(R.id.single_friend_lastSeen);
        val profileImage: CircleImageView = view.findViewById(R.id.single_friend_profileImage);

    }
}