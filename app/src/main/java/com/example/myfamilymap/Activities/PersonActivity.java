package com.example.myfamilymap.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfamilymap.R;
import com.example.myfamilymap.Singleton.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Models.Event;
import Models.Person;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataCache save = DataCache.getInstance();

        TextView firstNameSubtitle = (TextView) findViewById(R.id.firstNameSubtitle);
        TextView lastNameSubtitle = (TextView) findViewById(R.id.lastNameSubtitle);
        TextView genderSubtitle = (TextView) findViewById(R.id.genderSubtitle);
        TextView lastName = (TextView) findViewById(R.id.personLastName);
        TextView firstName = (TextView) findViewById(R.id.personName);
        TextView gender = (TextView) findViewById(R.id.personGender);

        firstName.setText(save.currentMapPerson.getFirstName());
        lastName.setText(save.currentMapPerson.getLastName());

        firstNameSubtitle.setText("First Name");
        lastNameSubtitle.setText("Last Name");

        if(save.currentMapPerson.getGender().toLowerCase().equals("m")){
            gender.setText("Male");
        }
        else{
            gender.setText("Female");
        }

        genderSubtitle.setText("Gender");

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        List<Person> personFamily = new ArrayList<>();
        List<Event> personEvents = new ArrayList<>();
        List<Event> holdEvents = new ArrayList<>();

        for(Event e: save.allCurrentMapEvents){
            if(e.getPersonID().equals(save.currentMapPerson.getPersonID())){
                holdEvents.add(e);
            }
        }

        for(Event e: holdEvents){
            if(e.getEventType().toLowerCase().equals("birth")){
                personEvents.add(e);
                holdEvents.remove(e);
                break;
            }
        }

        for(int i = 0; i < holdEvents.size(); i++){
            int least = 10000;
            Event event = new Event();
            for(Event e: holdEvents){
                if(e.getYear() < least && !personEvents.contains(e)){
                    least = e.getYear();
                    event = e;
                }
            }

            if(!personEvents.contains(event)){
                personEvents.add(event);
            }
        }

        for(Person p: DataCache.family.getData()) {
            if (save.currentMapPerson.getFatherID() != null) {
                if (save.currentMapPerson.getFatherID().equals(p.getPersonID())) {
                    personFamily.add(p);
                }
            }
            if(save.currentMapPerson.getMotherID() != null) {
                if (save.currentMapPerson.getMotherID().equals(p.getPersonID())) {
                    personFamily.add(p);
                }
            }
            if(save.currentMapPerson.getSpouseID() != null ) {
                if (save.currentMapPerson.getSpouseID().equals(p.getPersonID())) {
                    personFamily.add(p);
                }
            }
            if(p.getFatherID() != null && p.getMotherID() != null) {
                if (p.getFatherID().equals(save.currentMapPerson.getPersonID()) || p.getMotherID().equals(save.currentMapPerson.getPersonID())) {
                    personFamily.add(p);
                }
            }
        }

        expandableListView.setAdapter(new ExpandableListAdapter(personEvents, personFamily));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter{

        private static final int PERSON_EVENTS_GROUP_POSITION = 0;
        private static final int PERSON_FAMILY_GROUP_POSITION = 1;
        private static final int TOTAL_NUMBER_OF_GROUPS = 2;

        private List<Event> personEvents;
        private List<Person> personFamily;

        ExpandableListAdapter(List<Event> personEvents, List<Person> personFamily){
            this.personEvents = personEvents;
            this.personFamily = personFamily;
        }
        @Override
        public int getGroupCount() {
            return TOTAL_NUMBER_OF_GROUPS;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition){
                case PERSON_EVENTS_GROUP_POSITION:
                    return personEvents.size();
                case PERSON_FAMILY_GROUP_POSITION:
                    return personFamily.size();
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position: + " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition){
                case PERSON_EVENTS_GROUP_POSITION:
                    return "Life Events";
                case PERSON_FAMILY_GROUP_POSITION:
                    return "Family";
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position: + " + groupPosition);

            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
           switch (groupPosition){
               case PERSON_EVENTS_GROUP_POSITION:
                   return personEvents.get(childPosition);
               case PERSON_FAMILY_GROUP_POSITION:
                   return personFamily.get(childPosition);
               default:
                   throw new IllegalArgumentException("Unrecognized Group Position: + " + groupPosition);
           }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition){
                case PERSON_EVENTS_GROUP_POSITION:
                    titleView.setText("Life Events");
                    break;
                case PERSON_FAMILY_GROUP_POSITION:
                    titleView.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized Group Position: + " + groupPosition);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition){
                case PERSON_EVENTS_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_child, parent, false);
                    initializeEventsView(itemView, childPosition);
                    break;
                case PERSON_FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_child, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:

                    throw new IllegalArgumentException("Unrecognized Group Position: + " + groupPosition);
            }
            return itemView;
        }

        private void initializeEventsView(View eventItemView, int childPosition) {
            TextView eventDetailView = eventItemView.findViewById(R.id.expandableChildTitle);
            TextView eventOwnerName = eventItemView.findViewById(R.id.expandableChildSubtitle);
            ImageView eventIcon = eventItemView.findViewById(R.id.personActivityIcon);
            DataCache saved = DataCache.getInstance();
            Drawable eventMarker = null;
            String eventDetails = "";

            String eventOwner = saved.currentMapPerson.getFirstName() + " " + saved.currentMapPerson.getLastName();

            eventDetails = personEvents.get(childPosition).getEventType().toUpperCase() + ": " + personEvents.get(childPosition).getCity() + ", "
                    + personEvents.get(childPosition).getCountry() + "(" + personEvents.get(childPosition).getYear() + ")";
            eventMarker = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).color(Color.BLACK).sizeDp(25);

            eventDetailView.setText(eventDetails);
            eventOwnerName.setText(eventOwner);
            eventIcon.setImageDrawable(eventMarker);
            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PersonActivity.this, "Selected Event", Toast.LENGTH_SHORT).show();
                    saved.currentMapEvent = personEvents.get(childPosition);
                    Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        private void initializeFamilyView(View familyItemView, int childPosition) {

            TextView personNameView = familyItemView.findViewById(R.id.expandableChildTitle);
            TextView person = familyItemView.findViewById(R.id.expandableChildSubtitle); //personNameView.findViewById...
            ImageView personIcon = familyItemView.findViewById(R.id.personActivityIcon);
            DataCache saved = DataCache.getInstance();
            Drawable personDrawable;

            String personName = personFamily.get(childPosition).getFirstName() + " " + personFamily.get(childPosition).getLastName();

            if(personFamily.get(childPosition).getGender().toLowerCase().equals("m")) {
                personDrawable = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).color(Color.BLUE).sizeDp(35);
            }
            else{
                personDrawable = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).color(Color.MAGENTA).sizeDp(35);
            }
            String personInfo = "";
            Person child = new Person();

            if(personFamily.get(childPosition).getPersonID().equals(saved.currentMapPerson.getFatherID())){
                personInfo = "Father";
            }
            else if(personFamily.get(childPosition).getPersonID().equals(saved.currentMapPerson.getMotherID())){
                personInfo = "Mother";
            }
            else if(personFamily.get(childPosition).getPersonID().equals(saved.currentMapPerson.getSpouseID())){
                personInfo = "Spouse";
            }
            else {
                for (Person p : DataCache.family.getData()) {
                    if (p.getPersonID() == personFamily.get(childPosition).getPersonID()) {
                        child = p;
                    }
                }
                if(child.getPersonID() != null){
                    personInfo = "Child";
                }
            }

            personNameView.setText(personName);
            person.setText(personInfo);
            personIcon.setImageDrawable(personDrawable);

            familyItemView.setOnClickListener(v -> {
                Toast.makeText(getApplicationContext(), "Hello Family Member", Toast.LENGTH_SHORT).show();
                saved.currentMapPerson = personFamily.get(childPosition);
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                startActivity(intent);
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
