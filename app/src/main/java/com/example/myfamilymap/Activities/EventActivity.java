package com.example.myfamilymap.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.myfamilymap.Fragments.MapFragment;
import com.example.myfamilymap.R;
import com.example.myfamilymap.Singleton.DataCache;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataCache saved = DataCache.getInstance();
        saved.mapMenuActivity = false;

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = new MapFragment();
        fm.beginTransaction().add(R.id.eventActivity, mapFragment).commit();
    }
}