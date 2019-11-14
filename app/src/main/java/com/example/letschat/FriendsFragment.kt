package com.example.letschat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_friends.*
import java.lang.NullPointerException

class FriendsFragment(val friendsDatabaseResponse : ArrayList<Friends>) : Fragment() {

    private var currentUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private val TAG : String = "FriendsFragment"
    private lateinit var adaptor : FriendsRecyclerViewAdaptor


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        friendsRecyclerView.apply {

            layoutManager = LinearLayoutManager(activity)

            this.adapter = adaptor
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adaptor = FriendsRecyclerViewAdaptor(friendsDatabaseResponse, context ?: throw NullPointerException())
        fetchData()
        retainInstance = true
    }

    private fun fetchData() {
        var query : Query = FirebaseDatabase.getInstance().reference.child("Friends").child(currentUser?.uid ?: throw NullPointerException())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val children = dataSnapshot.children
                children.forEach {
                    var userQuery : Query = FirebaseDatabase.getInstance().reference.child("Users").child(it.key ?: throw NullPointerException())
                    Log.d(TAG, it.toString())
                    userQuery.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var friend = Friends(dataSnapshot.child("name").value.toString(),
                                                null,
                                                dataSnapshot.child("thumbnail_image").value.toString()
                                                )
                            friendsDatabaseResponse.add(friend)
                            Log.d(TAG, "Item count : ${friendsDatabaseResponse.size}")
                        }
                    })
                }
                adaptor.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}