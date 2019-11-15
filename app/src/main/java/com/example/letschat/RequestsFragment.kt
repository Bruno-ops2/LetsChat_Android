package com.example.letschat;


import android.os.Bundle;
import android.util.Log

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.android.synthetic.main.fragment_requests.*
import java.lang.NullPointerException

class RequestsFragment(val friendRequestsDatabaseResponse: ArrayList<Users>) : Fragment() {

    private val TAG : String = "RequestFragment"
    private var currentUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var adaptor: FriendRequestsRecyclerViewAdaptor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView started")
        return inflater.inflate(R.layout.fragment_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestsRecyclerView.apply {

            layoutManager = LinearLayoutManager(activity)
            this.adapter = adaptor
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        Log.d(TAG, "fetching data")
        fetchData()
        adaptor = FriendRequestsRecyclerViewAdaptor(friendRequestsDatabaseResponse, context ?: throw NullPointerException())
        retainInstance = true

    }

    private fun fetchData() {
        Log.d(TAG, "starting to fetch")
        var query : Query = FirebaseDatabase.getInstance().reference.child("Friend_Request").child(currentUser?.uid ?: throw NullPointerException())
        Log.d(TAG, "query : $query")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "ds value : ${dataSnapshot.value}")
                if(dataSnapshot.value == null) {
                    friendRequestsDatabaseResponse.clear()
                    adaptor.notifyDataSetChanged()
                    return
                }
                Log.d(TAG, "fetch data query listener started")
                val children = dataSnapshot.children
                Log.d(TAG, "listener : ${children.toString()}")
                children.forEach {
                    friendRequestsDatabaseResponse.clear()
                    if (it.child("request_type").value.toString() == ("received")) {
                        Log.d(TAG, "for each : $it")
                        val key : String = it.key.toString()
                        Log.d(TAG, "for each it key : $key")
                        var userQuery: Query = FirebaseDatabase.getInstance().reference.child("Users").child(key);
                        Log.d(TAG, it.toString())
                        userQuery.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                var user = Users(it.key.toString(),
                                        dataSnapshot.child("name").value.toString(),
                                        dataSnapshot.child("thumbnail_image").value.toString(),
                                        dataSnapshot.child("status").value.toString()
                                )
                                Log.d(TAG, "friend added : $user")
                                if (!friendRequestsDatabaseResponse.contains(user))
                                    friendRequestsDatabaseResponse.add(user)
                                else {
                                    var index = friendRequestsDatabaseResponse.indexOf(user)
                                    friendRequestsDatabaseResponse[index] = user
                                }
                                adaptor.notifyDataSetChanged()

                                Log.d(TAG, "Item count : ${friendRequestsDatabaseResponse.size}")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}