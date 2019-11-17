package com.example.letschat

import android.drm.DrmStore
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_app_bar.*
import java.util.*
import kotlin.collections.HashMap
import com.google.firebase.database.ServerValue
import com.google.firebase.database.DatabaseReference
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ChatActivity : AppCompatActivity() {

    private lateinit var mChatToolBar : Toolbar
    private var mChatUserName : String? = null
    private var mChatUserId : String? = null
    private var mCurrentUserId : String? = null
    private lateinit var mRootRef : DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mRootRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        mCurrentUserId = mAuth.currentUser?.uid

        mChatUserName = intent.getStringExtra("UserName")
        mChatUserId = intent.getStringExtra("UserId")

        setSupportActionBar(chatApp_bar as Toolbar)
        val actionBarView = layoutInflater.inflate(R.layout.chat_app_bar, null)
        val actionbar = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(true)
            customView = actionBarView
        }

        //custom action bar items

        custom_bar_title.text = mChatUserName

        mRootRef.child("Users").child(mChatUserId as String).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                var lastSeen = when(p0.child("lastSeen").value.toString()) {
                    "true" -> "Online"
                    else -> p0.child("lastSeen").value.toString()
                }
                custom_bar_seen.setText(lastSeen)

                var imageURL = p0.child("thumbnail_image").value.toString()

                Picasso.get().load(imageURL).placeholder(R.drawable.avatar).into(chat_bar_image)
            }
        })

        mRootRef.child("Chat").child(mCurrentUserId as String).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.hasChild(mChatUserId as String)) {

                    var chatAddMap = HashMap<String, Any>()
                    chatAddMap.put("seen", true)
                    chatAddMap.put("timeStamp", ServerValue.TIMESTAMP)

                    var mChatUserMap = HashMap<String, Any>()
                    mChatUserMap.put("Chat/$mCurrentUserId/$mChatUserId", chatAddMap)
                    mChatUserMap.put("Chat/$mChatUserId/$mCurrentUserId", chatAddMap)


                    mRootRef.updateChildren(mChatUserMap, object : DatabaseReference.CompletionListener{
                        override fun onComplete(p0: DatabaseError?, p1: DatabaseReference) {

                            if(p0 != null)
                                Log.d("Chat LOG:", p0.message)
                        }
                    })
                }
            }
        })

        chat_send_btn.setOnClickListener {
            val message : String = chat_message_view.text.toString()

            if(!TextUtils.isEmpty(message)) {

                val chatUserRef = "messages/$mChatUserId/$mCurrentUserId"
                val currentUserRef = "messages/$mCurrentUserId/$mChatUserId"

                val user_message_push = mRootRef.child("messages")
                        .child(mCurrentUserId as String).child(mChatUserId as String).push()

                val push_id = user_message_push.key

                val messageMap = HashMap<String, Any>()
                messageMap.put("message", message)
                messageMap.put("seen", false)
                messageMap.put("type", "text")
                messageMap.put("time", ServerValue.TIMESTAMP)
                messageMap.put("from", mCurrentUserId as String)

                val messageUserMap = HashMap<String, Any>()
                messageUserMap.put("$currentUserRef/$push_id", messageMap)
                messageUserMap.put("$chatUserRef/$push_id", messageMap)

                mRootRef.updateChildren(messageUserMap) { databaseError, databaseReference ->
                    if(databaseError != null) {
                        Log.d("CHAT_LOG", databaseError.details)
                    }

                    chat_message_view.setText("")
                }

            }
        }


    }

    override fun onStop() {
        super.onStop()
    }
}
