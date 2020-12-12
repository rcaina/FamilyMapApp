package com.example.myfamilymap.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfamilymap.R;
import com.example.myfamilymap.Singleton.DataCache;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import Models.Event;
import Models.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;

    private List<Event> searchedEvents = new ArrayList<>();
    private List<Person> searchedFamily = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        EditText wordSearch = findViewById(R.id.searchText);


        Drawable iconImage = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_search).color(Color.GRAY).sizeDp(10);
        ImageView searchImage = (ImageView) findViewById(R.id.searchImage);
        searchImage.setImageDrawable(iconImage);

        Drawable iconImage2 = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_times).color(Color.GRAY).sizeDp(10);
        ImageView clearImage = (ImageView) findViewById(R.id.clearImage);
        clearImage.setImageDrawable(iconImage2);

        wordSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(wordSearch.getText().length() > 0){

                    searchedFamily = SearchFamily(wordSearch.getText());

                    searchedEvents = SearchEvents(wordSearch.getText());
                }
                SearchAdapter adapter = new SearchAdapter(searchedFamily, searchedEvents);
                recyclerView.setAdapter(adapter);
            }
        });

        SearchAdapter adapter = new SearchAdapter(searchedFamily, searchedEvents);
        recyclerView.setAdapter(adapter);
    }

    private List<Person> SearchFamily(Editable text) {
        DataCache saved = DataCache.getInstance();
        List<Person> searchedFamily = new ArrayList<>();

        for(Person p: saved.family.getData()){
            if(p.getFirstName().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedFamily.add(p);
            }
            else if(p.getLastName().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedFamily.add(p);
            }
        }

        return searchedFamily;
    }

    private List<Event> SearchEvents(Editable text) {
        List<Event> searchedEvents = new ArrayList<>();
        DataCache saved = DataCache.getInstance();

        for(Event e: saved.allCurrentMapEvents){
            if(e.getEventType().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedEvents.add(e);
            }
            else if(e.getCity().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedEvents.add(e);
            }
            else if(e.getCountry().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedEvents.add(e);
            }
            else if(e.getAssociatedUsername().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedEvents.add(e);
            }
            else if(e.getYear().toString().toLowerCase().trim().contains(text.toString().toLowerCase())){
                searchedEvents.add(e);
            }
        }
        return searchedEvents;
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapterViewHolder>{

        private final List<Person> people;
        private final List<Event> events;

        SearchAdapter(List<Person> people, List<Event> events){
            this.people = people;
            this.events = events;
        }

        @Override
        public int getItemViewType(int position){
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE: EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View view;

            if(viewType == PERSON_ITEM_VIEW_TYPE){
                view = getLayoutInflater().inflate(R.layout.person_child, parent, false);
            }
            else{
                view = getLayoutInflater().inflate(R.layout.event_child, parent, false);
            }

            return new SearchAdapterViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchAdapterViewHolder holder, int position) {
            if(position < people.size()){
                holder.bind(people.get(position));
            }
            else{
                holder.bind(events.get(position - people.size()));
            }
        }

        @Override
        public int getItemCount() { return people.size() + events.size(); }
    }

    private class SearchAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView itemImage;
        private final TextView itemHeading;
        private final TextView itemSubHeading;

        private final int viewType;
        private Person person;
        private Event event;

        SearchAdapterViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            itemImage = itemView.findViewById(R.id.personActivityIcon);
            itemHeading = itemView.findViewById(R.id.expandableChildTitle);
            itemSubHeading = itemView.findViewById(R.id.expandableChildSubtitle);

        }
        private void bind(Person p){
            this.person = p;
            if(p.getGender().toLowerCase().equals("m")){
                Drawable iconImage = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_male).color(Color.BLUE).sizeDp(25);
                itemImage.setImageDrawable(iconImage);
                itemSubHeading.setText("Male");
            }
            else{
                Drawable iconImage = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_female).color(Color.MAGENTA).sizeDp(25);
                itemImage.setImageDrawable(iconImage);
                itemSubHeading.setText("Female");
            }

            String name = p.getFirstName() + " " + p.getLastName();
            itemHeading.setText(name);
        }
        private void bind(Event event){
            this.event = event;

            Drawable eventMarker = new IconDrawable(getApplicationContext(), FontAwesomeIcons.fa_map_marker).color(Color.BLACK).sizeDp(25);
            itemImage.setImageDrawable(eventMarker);

            String eventDetails = event.getEventType().toUpperCase() + " " + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";
            itemHeading.setText(eventDetails);

            for(Person p: DataCache.family.getData()){
                if(p.getPersonID().equals(event.getPersonID())){
                    String name = p.getFirstName() + " " + p.getLastName();
                    itemSubHeading.setText(name);
                    break;
                }
            }
        }

        @Override
        public void onClick(View v) {

            DataCache saved = DataCache.getInstance();
            if(viewType == PERSON_ITEM_VIEW_TYPE){
                saved.currentMapPerson = person;
                Intent intent = new Intent(getApplicationContext(), PersonActivity.class);
                startActivity(intent);
            }
            else{
                saved.currentMapEvent = event;
                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }
}