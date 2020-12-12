package com.example.myfamilymap.Main;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myfamilymap.Fragments.LoginFragment;
import com.example.myfamilymap.Fragments.MapFragment;
import com.example.myfamilymap.R;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule());

        FragmentManager fm = getSupportFragmentManager();

        LoginFragment loginFragment = new LoginFragment();

        fm.beginTransaction().add(R.id.mainActivity, loginFragment).commit();
    }

    public void mapFragment(){
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = new MapFragment();
        fm.beginTransaction().replace(R.id.mainActivity, mapFragment).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
