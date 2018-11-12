package com.apptronix.nitkonschedule.teacher.ui

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.apptronix.nitkonschedule.R
import com.apptronix.nitkonschedule.model.User
import com.apptronix.nitkonschedule.teacher.service.DbSyncService
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.teacher.app_bar_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ScheduleFragment.OnFragmentInteractionListener, AssignmentsFragment.OnFragmentInteractionListener, AttendanceFragment.OnFragmentInteractionListener, TestsFragment.OnFragmentInteractionListener, CoursesFragment.OnFragmentInteractionListener, GoogleApiClient.OnConnectionFailedListener {


    lateinit var mNotifyManager: NotificationManager
    lateinit var mBuilder: Notification.Builder

    private var user: User? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {

        Timber.i(event.message)

        when (event.message) {
            "Uploading" -> {

                Toast.makeText(this, R.string.uploading, Toast.LENGTH_LONG).show()

            }
            "UploadFailed" -> {

                Toast.makeText(this, R.string.upload_failed, Toast.LENGTH_LONG).show()

            }
            "UploadSuccess" -> {

                mNotifyManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mBuilder.setContentText("Upload Complete")
                        .setProgress(0, 0, false)
                mNotifyManager.notify(28, mBuilder.build())

                Toast.makeText(this, R.string.uploadsuccesful, Toast.LENGTH_LONG).show()

            }
            "ServerUnreachable" -> {

                Toast.makeText(this, R.string.server_unreachable_msg, Toast.LENGTH_LONG).show()

            }
            "TokenUpdateRefused" -> {

                signOut()
                Toast.makeText(this, R.string.requestlogin, Toast.LENGTH_LONG).show()
            }
            "testTitleExists" -> {

                signOut()
                Toast.makeText(this, "Error creating test. Title already exists", Toast.LENGTH_LONG).show()
            }
            else -> {

                Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.i("main activity started")
        setSupportActionBar(toolbar)

        user = User(this)
        if (user!!.refreshToken==null) {
            signOut()
        }

        nav_view.setNavigationItemSelectedListener(this)

        val toggle = object : ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(hView: View) {

                initializeDrawer(hView)

                super.onDrawerOpened(hView)
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        if (fragment_container != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            val ttFragment = ScheduleFragment()

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, ttFragment).commit()
        }


    }


    private fun initializeDrawer(hView: View?) {

        Timber.i("drawer %s", user!!.email)

        Picasso.with(baseContext)
                .load(user!!.picture)
                .into(profilePicView, object : Callback {
                    override fun onSuccess() {
                        val imageBitmap = (profilePicView.drawable as BitmapDrawable).bitmap
                        val imageDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap)
                        imageDrawable.isCircular = true
                        imageDrawable.cornerRadius = Math.max(imageBitmap.width, imageBitmap.height) / 2.0f
                        profilePicView.setImageDrawable(imageDrawable)
                    }

                    override fun onError() {

                    }
                })


        nameTextView.text = user!!.userName
        emailTextView.text = user!!.email

    }

    override fun onResume() {


        if (user!!.refreshToken == null) {
            signOut()
        }
        val intent = Intent(this,DbSyncService::class.java)
        startService(intent)

        super.onResume()
    }

    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_settings) {
            return true
        } else if (id == R.id.action_signout) {
            signOut()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        val transaction = supportFragmentManager.beginTransaction()

        if (id == R.id.nav_time_table) {
            val ttFragment = ScheduleFragment()
            transaction.replace(R.id.fragment_container, ttFragment)
        } else if (id == R.id.nav_tests) {
            val testsFragment = TestsFragment()
            transaction.replace(R.id.fragment_container, testsFragment)
        } else if (id == R.id.nav_attendance) {
            val attendancePercentageFragment = AttendanceFragment()
            transaction.replace(R.id.fragment_container, attendancePercentageFragment)
        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_assignments) {
            val assgnFragment = AssignmentsFragment()
            transaction.replace(R.id.fragment_container, assgnFragment)
        } else if (id == R.id.nav_courses) {
            val courseFragment = CoursesFragment()
            transaction.replace(R.id.fragment_container, courseFragment)
        }
        transaction.addToBackStack(null)
        transaction.commit()

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(title: String) {
        setTitle(title)
    }

    override fun onFragmentChange(code: String, title: String) {

        setTitle(title)
        val transaction = supportFragmentManager.beginTransaction()
        val arguments = Bundle()
        arguments.putString("code", code)
        when(title){
            "Test" -> {
                val fragment = TestsFragment()
                fragment.arguments=arguments
                transaction.replace(R.id.fragment_container, fragment)
            }
            "Assignment" -> {
                val fragment = AssignmentsFragment()
                fragment.arguments=arguments
                transaction.replace(R.id.fragment_container, fragment)
            }
        }

        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun signOut() {

        user!!.signOutUser(this)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show()

    }

    class MessageEvent(var message: String)


    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}
