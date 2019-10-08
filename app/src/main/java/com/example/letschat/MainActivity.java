package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mTabPager;
    private TabLayout mTabLayout;
    private TabsAdaptor mTabsAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mTabPager = (ViewPager) findViewById(R.id.main_tabpager);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabsAdaptor = new TabsAdaptor(getSupportFragmentManager());
        mTabPager.setAdapter(mTabsAdaptor);
        mTabLayout.setupWithViewPager(mTabPager);

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lets Chat");
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent startActivity = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startActivity);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_menu_item_logout) {
            mAuth.signOut();
            sendToStart();
        } else if (item.getItemId() ==  R.id.main_manu_item_settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return true;
    }
}
