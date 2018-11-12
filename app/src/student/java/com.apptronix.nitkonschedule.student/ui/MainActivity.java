package com.apptronix.nitkonschedule.student.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apptronix.nitkonschedule.R;
import com.apptronix.nitkonschedule.model.User;
import com.apptronix.nitkonschedule.student.service.DbSyncService;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , ScheduleFragment.OnFragmentInteractionListener, AssignmentsFragment.OnFragmentInteractionListener,
        CoursesFragment.OnFragmentInteractionListener, TestsFragment.OnFragmentInteractionListener, AttendanceFragment.OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener{

    User user;
    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){

        Timber.i(event.getMessage());

        switch (event.getMessage()){
            case "ServerUnreachable": {

                Toast.makeText(this, R.string.server_unreachable_msg,Toast.LENGTH_LONG).show();
                break;

            }
            case "TokenUpdateRefused":{

                signOut();
                Toast.makeText(this, R.string.requestlogin,Toast.LENGTH_LONG).show();
                break;
            }
            case "Course enrolled":{

                Toast.makeText(this, "Course enrolled",Toast.LENGTH_LONG).show();
                syncDB();
                break;
            }
            default:{
                Toast.makeText(this,event.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        user= new User(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

                           ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View hView) {

                Timber.i("drawer %s",user.getEmail());
                final ImageView imageView = (ImageView)hView.findViewById(R.id.profilePicView);
                Picasso.with(getBaseContext())
                        .load(user.getPicture())
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                imageView.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError() {

                            }
                        });

                TextView name =(TextView)hView.findViewById(R.id.nameTextView);
                name.setText(user.getUserName());
                TextView email=(TextView)hView.findViewById(R.id.emailTextView);
                email.setText(user.getEmail());

                super.onDrawerOpened(hView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            ScheduleFragment ttFragment = new ScheduleFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, ttFragment).commit();
        }

        syncDB();
    }

    public void syncDB(){

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(DbSyncService.class) // the JobService that will be called
                .setTag("my-unique-tag")        // uniquely identifies the job
                .build();

        dispatcher.mustSchedule(myJob);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_signout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_time_table) {
            ScheduleFragment ttFragment = new ScheduleFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, ttFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_tests) {
            TestsFragment testsFragment = new TestsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, testsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_attendance) {

            AttendanceFragment attendancePercentageFragment = new AttendanceFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, attendancePercentageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_courses) {
            CoursesFragment coursesFragment = new CoursesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, coursesFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_assignments) {
            AssignmentsFragment assgnFragment = new AssignmentsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, assgnFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(String title) {
        setTitle(title);
    }

    private void signOut() {

        user.signOutUser(this);
        startActivity(new Intent(this,LoginActivity.class));
        finish();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this,R.string.connection_failed,Toast.LENGTH_LONG).show();

    }

    public static class MessageEvent{

        public String message;

        public  MessageEvent(String message){
            this.message=message;
        }

        public String getMessage(){
            return message;
        }

    }
}
