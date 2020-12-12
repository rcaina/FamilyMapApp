package com.example.myfamilymap.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myfamilymap.Fragments.MapFragment;
import com.example.myfamilymap.Main.MainActivity;
import com.example.myfamilymap.R;
import com.example.myfamilymap.Singleton.DataCache;

public class SettingsActivity extends AppCompatActivity {

    private Switch storyLines;
    private Switch familyTreeLines;
    private Switch spouseLines;
    private Switch fatherSide;
    private Switch motherSide;
    private Switch maleEvents;
    private Switch femaleEvents;
    private TextView logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataCache save = DataCache.getInstance();
        MapFragment map = save.mapInfo;

        storyLines = (Switch) findViewById(R.id.storySwitch);
        if(save.storyLines){
            storyLines.setChecked(true);
        }
        else{
            storyLines.setChecked(false);
        }

        storyLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.storyLines = true;
                } else {
                    save.storyLines = false;
                }
                map.clearLines();
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        familyTreeLines = (Switch) findViewById(R.id.treeSwitch);

        if(save.familyTreeLines){
            familyTreeLines.setChecked(true);
        }
        else{
            familyTreeLines.setChecked(false);
        }

        familyTreeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.familyTreeLines = true;
                } else {
                    save.familyTreeLines = false;
                }
                map.clearLines();
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        spouseLines = (Switch) findViewById(R.id.spouseSwitch);
        if(save.spouseLines){
            spouseLines.setChecked(true);
        }
        else {
            spouseLines.setChecked(false);
        }

        spouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.spouseLines = true;
                } else {
                    save.spouseLines = false;
                }
                map.clearLines();
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        fatherSide = (Switch) findViewById(R.id.fatherSwitch);
        if(save.fatherEvents){
            fatherSide.setChecked(true);
        }
        else{
            fatherSide.setChecked(false);
        }

        fatherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.fatherEvents = true;
                } else {
                    save.fatherEvents = false;
                }
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        motherSide = (Switch) findViewById(R.id.motherSwitch);
        if(save.motherEvents){
            motherSide.setChecked(true);
        }
        else{
            motherSide.setChecked(false);
        }

        motherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.motherEvents = true;
                } else {
                    save.motherEvents = false;
                }
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        maleEvents = (Switch) findViewById(R.id.maleSwitch);
        if(save.maleEvents){
            maleEvents.setChecked(true);
        }
        else{
            maleEvents.setChecked(false);
        }

        maleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.maleEvents = true;
                } else {
                    save.maleEvents = false;
                }
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        femaleEvents = (Switch) findViewById(R.id.femaleSwitch);
        if(save.femaleEvents){
            femaleEvents.setChecked(true);
        }
        else{
            femaleEvents.setChecked(false);
        }

        femaleEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    save.femaleEvents = true;
                } else {
                    save.femaleEvents = false;
                }
                map.fillMap();
                map.drawMapLines(save.currentMapPerson, save.currentMapEvent);
            }
        });

        logout = (TextView) findViewById(R.id.logoutTitle);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache saved = DataCache.getInstance();
                saved.destroy();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}