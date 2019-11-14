package com.example.letschat

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import androidx.core.os.HandlerCompat.postDelayed
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar


class LetsChat : Application(), Application.ActivityLifecycleCallbacks {

    private var currentActivityReference : WeakReference<Activity>? = null
    private var mUserDatabase : DatabaseReference? = null
    private val applicationBackgrounded = AtomicBoolean(true)
    private val INTERVAL_BACKGROUND_STATE_CHANGE = 750L

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(this)

        var mAuth = FirebaseAuth.getInstance()

        try {
            mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(mAuth.currentUser?.uid ?: throw NullPointerException())
        } catch (e : Exception) {
            mUserDatabase = null
        }


        /*mUserDatabase?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mUserDatabase?.child("lastSeen")?.onDisconnect()?.setValue(false.toString())
                *//*mUserDatabase.child("lastSeen").setValue(true)*//*

            }
        })*/

    }

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = java.text.SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = java.util.Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        return formatter.format(calendar.getTime())
    }

    fun onEnterForeground() {
        //This is where you'll handle logic you want to execute when your application enters the foreground
        Log.d(this.javaClass.name, "activity is in foreground" )
        if(mUserDatabase != null)
            mUserDatabase?.child("lastSeen")?.setValue("true")

    }

    fun onEnterBackground() {
        //This is where you'll handle logic you want to execute when your application enters the background
        Log.d(this.javaClass.name, "activity is in background" )
        if(mUserDatabase != null)
            mUserDatabase?.child("lastSeen")?.setValue(getDate(System.currentTimeMillis(), "dd/MM/yyyy hh.mm aa"))

    }

    private fun determineForegroundStatus() {
        if (applicationBackgrounded.get()) {
            onEnterForeground()
            applicationBackgrounded.set(false)
        }
    }

    private fun determineBackgroundStatus() {
        Handler().postDelayed({
            if (!applicationBackgrounded.get() && currentActivityReference == null) {

                applicationBackgrounded.set(true)
                onEnterBackground()
            }
        }, INTERVAL_BACKGROUND_STATE_CHANGE)
    }




    //interface methods

    override fun onActivityPaused(activity: Activity) {
        currentActivityReference = null
        determineBackgroundStatus()
    }

    override fun onActivityStarted(activity: Activity) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityDestroyed(activityp0: Activity) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityStopped(activity: Activity) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
         //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResumed(activity: Activity) {

        currentActivityReference = WeakReference(activity)
        determineForegroundStatus()

    }
}