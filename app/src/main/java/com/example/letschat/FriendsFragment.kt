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
        Log.d(TAG, "onCreateView started")
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Log.d(TAG, "onViewCreated started")
        fetchData()*/

        friendsRecyclerView.apply {

            layoutManager = LinearLayoutManager(activity)

            this.adapter = adaptor
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")
        Log.d(TAG, "fetching data")
        fetchData()
        adaptor = FriendsRecyclerViewAdaptor(friendsDatabaseResponse, context ?: throw NullPointerException())
        retainInstance = true
    }

    private fun fetchData() {
        Log.d(TAG, "starting to fetch")
        var query : Query = FirebaseDatabase.getInstance().reference.child("Friends").child(currentUser?.uid ?: throw NullPointerException())
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "fetch data query listener started")
                val children = dataSnapshot.children
                Log.d(TAG, "listener : ${children.toString()}")
                children.forEach {
                    Log.d(TAG, "for each : $it")
                    var userQuery : Query = FirebaseDatabase.getInstance().reference.child("Users").child(it.key ?: throw NullPointerException())
                    Log.d(TAG, it.toString())
                    userQuery.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var friend = Friends(it.key.toString(),
                                                dataSnapshot.child("name").value.toString(),
                                                dataSnapshot.child("lastSeen").value.toString(),
                                                dataSnapshot.child("thumbnail_image").value.toString()
                                                )
                            Log.d(TAG, "friend added : $friend")
                            if(!friendsDatabaseResponse.contains(friend))
                                friendsDatabaseResponse.add(friend)
                            else {
                                var index = friendsDatabaseResponse.indexOf(friend)
                                friendsDatabaseResponse[index] = friend
                            }
                            adaptor.notifyDataSetChanged()

                            Log.d(TAG, "Item count : ${friendsDatabaseResponse.size}")
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}