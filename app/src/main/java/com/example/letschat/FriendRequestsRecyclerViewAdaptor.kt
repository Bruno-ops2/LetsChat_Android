package com.example.letschat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendRequestsRecyclerViewAdaptor(
        val friendRequestsDatabaseResponse: ArrayList<Users>,
        val mContext: Context
    ) : RecyclerView.Adapter<FriendRequestsRecyclerViewAdaptor.ViewHolder>() {

    private val TAG : String = "FRRecyclerAdaptor"
    private lateinit var onClick : OnItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder called")
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.single_user, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder called")
        val name = friendRequestsDatabaseResponse[position].name
        val profileImage = friendRequestsDatabaseResponse[position].profile_image
        val status = friendRequestsDatabaseResponse[position].status
        Log.d(TAG, "view holder name ; $name")
        Log.d(TAG, "view holder status ; $status")
        Log.d(TAG, "view holder image ; $profileImage")

        holder.name.text = name
        holder.status.text = status
        Picasso.get().load(profileImage).placeholder(R.drawable.avatar).into(holder.profileImage)
        holder.profileImage.rootView.setOnClickListener{
            onClick.onItemClick(position, friendRequestsDatabaseResponse[position])
        }
    }

    override fun getItemCount(): Int {
        return friendRequestsDatabaseResponse.size
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val name : TextView = view.findViewById(R.id.single_user_name)
        val status : TextView = view.findViewById(R.id.single_user_status)
        val profileImage : CircleImageView = view.findViewById(R.id.single_user_profile)
    }

    interface OnItemClick { fun onItemClick(position : Int, user: Users) }

    fun setonClick (onClick: OnItemClick) {
        this.onClick = onClick
    }
}