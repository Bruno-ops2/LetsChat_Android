package com.example.letschat

import android.drm.DrmStore
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_app_bar.*

class ChatActivity : AppCompatActivity() {

    private lateinit var mChatToolBar : Toolbar
    private var mChatUserName : String? = null
    private var mChatUserId : String? = null
    private lateinit var mRootRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mRootRef = FirebaseDatabase.getInstance().reference

        mChatUserName = intent.getStringExtra("UserName")
        mChatUserId = intent.getStringExtra("UserId")

        setSupportActionBar(chatApp_bar as Toolbar)
        val actionBarView = layoutInflater.inflate(R.layout.chat_app_bar, null)
        val actionbar = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowCustomEnabled(true)
            setCustomView(actionBarView)
        }

        //custom action bar items

        custom_bar_title.setText(mChatUserName)

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

    }
}
