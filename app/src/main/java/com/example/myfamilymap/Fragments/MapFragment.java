package com.example.myfamilymap.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.myfamilymap.Activities.PersonActivity;
import com.example.myfamilymap.R;
import com.example.myfamilymap.Activities.SearchActivity;
import com.example.myfamilymap.Activities.SettingsActivity;
import com.example.myfamilymap.Singleton.DataCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import Models.Event;
import Models.Person;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private View view;
    private ArrayList<Marker> markers;
    private ArrayList<Event> mapEvents;
    private Float width1 = 30f;

    private GoogleMap map;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
     //TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        DataCache saved = DataCache.getInstance();

        inflater.inflate(R.menu.map_menu, menu);

        if(!saved.mapMenuActivity) {
            menu.findItem(R.id.settings).setVisible(false);
            menu.findItem(R.id.search).setVisible(false);
        }

        //listens for settings button to be clicked and opens Settings Activity
        menu.findItem(R.id.settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });

        //listens for search button to be clicked and opens Search Activity
        menu.findItem(R.id.search).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        });

        //listens for person details to be clicked and opens Person Activity
        TextView personDetails = (TextView) view.findViewById(R.id.mapTextView);
        personDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonActivity.class);
                startActivity(intent);
            }
        });

        //Default for person details when no marker is selected at the opening of the app
        if(saved.mapMenuActivity) {
            ImageView defaultImage = (ImageView) view.findViewById(R.id.mapPersonIcon);
            String infoDisplayTop = "Click on a marker to see event details";

            personDetails.setText(infoDisplayTop);

            Drawable iconImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_android).color(Color.GREEN).sizeDp(50);
            defaultImage.setImageDrawable(iconImage);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        DataCache saved = DataCache.getInstance();
        saved.mapInfo = this;

        startMap();

        //adds gets the event marker selected ID and fills the map and person details
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerID = "";

                for(Marker m: markers){
                    if(m.getTag().equals(marker.getTag())){
                        markerID = m.getTag().toString();
                    }
                }

                fillMap();
                markerInformation(markerID);
                return true;
            }
        });

        if(!saved.mapMenuActivity) {
            markerInformation(saved.currentMapEvent.getEventID());
        }
    }
    private void startMap(){
        //fills the map with markers
        fillMap();
    }
    public void clearLines(){ map.clear();}

    public void fillMap() {

        map.clear();
        markers = new ArrayList<>();
        mapEvents = new ArrayList<>();
        DataCache saved = DataCache.getInstance();
        mapEvents = saved.getEvents();

        //adds markers to the app based on the filtering
        for(Event event: mapEvents){
            LatLng location = new LatLng(event.getLatitude(), event.getLongitude());
            Marker mark;

            Float color = saved.colorTypes.get(event.getEventType().toLowerCase());

            mark = map.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.defaultMarker(color)).title(event.getEventType()));
            mark.setTag(event.getEventID());
            markers.add(mark);
        }
    }

    //gets the event and the person associated with the marker selected
    private View markerInformation(String markerID){
        Event selected = new Event();
        Person person = new Person();
        DataCache save = DataCache.getInstance();

        TextView textInfo = (TextView) view.findViewById(R.id.mapTextView);
        ImageView genderIcon = (ImageView) view.findViewById(R.id.mapPersonIcon);

        for(Event event: mapEvents){
            if(event.getEventID().equals(markerID)){
                selected = event;
            }
        }
        for(Person p: DataCache.family.getData()){
            if(p.getPersonID().equals(selected.getPersonID())){
                person = p;
            }
        }

        //color male / female icon with the right color
        if(person.getGender().toLowerCase().equals("m")){
            Drawable iconImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).color(Color.BLUE).sizeDp(50);
            genderIcon.setImageDrawable(iconImage);
        }
        else{
            Drawable iconImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).color(Color.MAGENTA).sizeDp(50);
            genderIcon.setImageDrawable(iconImage);
        }

        //gets person details to display
        String infoDisplay = person.getFirstName() + " " + person.getLastName() + "\n" + selected.getEventType()
        + ": " + selected.getCity() + ", " + selected.getCountry() + " (" + selected.getYear() + ")";
        textInfo.setText(infoDisplay);

        save.currentMapPerson = person;
        save.currentMapEvent = selected;

        LatLng latLng = new LatLng(selected.getLatitude(), selected.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        drawMapLines(person, selected);
        return view;
    }

    //Checks for which lines to be drawn on map based on filtering
    public void drawMapLines(Person personInfo, Event selectedEvent){
        DataCache savedData = DataCache.getInstance();

        if(personInfo != null && selectedEvent != null) {
            if (savedData.storyLines) {
                DrawStoryLines(personInfo, selectedEvent);
            }
            if (savedData.familyTreeLines) {
                width1 = 30f;
                DrawFamilyLines(personInfo, selectedEvent, width1);
            }
            if (savedData.spouseLines) {
                if (personInfo.getSpouseID() != null) {
                    DrawSpouseLines(personInfo, selectedEvent);
                }
            }
        }
    }

    //Draws story lines
    private void DrawStoryLines(Person personInfo, Event selectedEvent){

        ArrayList<Event> lifeEvents = new ArrayList<>();
        ArrayList<Event> usedEvents = new ArrayList<>();
        Polyline polylineTmp;
        Float width = 30f;

        for (Event e: mapEvents) {
            if (personInfo.getPersonID().equals(e.getPersonID())) {
                lifeEvents.add(e);
            }
        }

        for(Event ev: lifeEvents){
            int least = 10000;
            Event event = new Event();
            for(Event e: lifeEvents){
                if(e.getYear() < least && !usedEvents.contains(e)){
                    least = e.getYear();
                    event = e;
                }
            }

            if(!usedEvents.contains(event)){
                usedEvents.add(event);
            }
        }

        if(usedEvents.size() > 0) {
            LatLng location = new LatLng(usedEvents.get(0).getLatitude(), usedEvents.get(0).getLongitude());

            for (int i = 0; i < usedEvents.size(); i++) {
                if(width <= 0){
                    width = 5f;
                }
                LatLng storyLoc = new LatLng(usedEvents.get(i).getLatitude(), usedEvents.get(i).getLongitude());

                Integer blue = 0xff0000ff;
                polylineTmp = map.addPolyline(new PolylineOptions()
                        .width(width)
                        .clickable(false)
                        .add(location, storyLoc)
                        .color(blue));
                if (width != 0) {
                    width = width - 5;
                }

                location = storyLoc;
            }
            usedEvents.clear();
        }
    }

    //Draws spouse line
    private void DrawSpouseLines(Person personInfo, Event selectedEvent){

        LatLng location = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());
        Person spouse = new Person();
        Event event = new Event();

        Integer year = 2020;

        for (Person p : DataCache.family.getData()) {
            if (personInfo.getSpouseID().equals(p.getPersonID())) {
                spouse = p;
            }
        }

        for (Event e : mapEvents) {
            if (e.getPersonID().equals(spouse.getPersonID())) {
                if (e.getEventType().equals("birth")) {
                    event = e;
                }
            }
        }

        if(event.getEventID() == null){
            for (Event e : DataCache.events.getData()) {
                if (e.getPersonID().equals(spouse.getPersonID())) {
                    if (e.getYear() < year) {
                        year = e.getYear();
                    }
                }
            }
            for (Event e : mapEvents) {
                if (e.getPersonID().equals(spouse.getPersonID())) {
                    if (e.getYear() == year) {
                        event = e;
                    }
                }
            }
        }

        if (event.getEventID() != null && spouse.getPersonID() != null) {
            LatLng spouseLoc = new LatLng(event.getLatitude(), event.getLongitude());

            Integer red = 0xffff0000;
            Polyline polylineTmp = map.addPolyline(new PolylineOptions()
                    .clickable(false)
                    .add(location, spouseLoc)
                    .color(red));
        }
    }

    //Draws family tree lines recursively and decreasing thickness on polylines
    private void DrawFamilyLines(Person personInfo, Event selectedEvent, Float thickness){
        ArrayList<Event> temp = new ArrayList<>();
        Person parent = new Person();
        Event event = new Event();
        Integer year = 2020;

        if(thickness <= 0){
            thickness = 5f;
        }

        boolean foundBirth = false;
        LatLng location = new LatLng(selectedEvent.getLatitude(), selectedEvent.getLongitude());

        if(personInfo.getFatherID() != null) {
            for (Event e : mapEvents) {
                if (e.getPersonID().equals(personInfo.getFatherID())) {
                    if (e.getEventType().toLowerCase().equals("birth")) {
                        event = e;
                        LatLng ancestorLoc = new LatLng(e.getLatitude(), e.getLongitude());

                        Integer orange = 0xff800000;
                        Polyline polylineTmp = map.addPolyline(new PolylineOptions()
                                .width(thickness)
                                .clickable(false)
                                .add(location, ancestorLoc)
                                .color(orange));

                        for (Person p : DataCache.family.getData()) {
                            if (p.getPersonID().equals(personInfo.getFatherID())) {
                                parent = p;
                            }
                        }

                        foundBirth = true;
                        DrawFamilyLines(parent, e, thickness - 10);
                    }
                }
            }

            if (!foundBirth) {
                for (Event ev : mapEvents) {
                    if (ev.getPersonID().equals(personInfo.getFatherID())) {
                        temp.add(ev);
                    }
                }

                for (Event ev : temp) {
                    if (ev.getYear() < year) {
                        year = ev.getYear();
                    }
                }

                for (Event ev : temp) {
                    if (ev.getYear().equals(year)) {
                        LatLng ancestorLoc = new LatLng(ev.getLatitude(), ev.getLongitude());

                        Integer orange = 0xff800000;
                        Polyline polylineTmp = map.addPolyline(new PolylineOptions()
                                .width(thickness)
                                .clickable(false)
                                .add(location, ancestorLoc)
                                .color(orange));

                        for (Person p : DataCache.family.getData()) {
                            if (p.getPersonID().equals(personInfo.getFatherID())) {
                                parent = p;
                            }
                        }
                        DrawFamilyLines(parent, ev, thickness - 10);
                        break;
                    }
                }
            }
        }

        //reset variables
        foundBirth = false;
        temp.clear();
        year = 2020;

        if(personInfo.getMotherID() != null) {
            Integer black = 0xff000000;
            for (Event e : mapEvents) {
                if(e.getPersonID().equals(personInfo.getMotherID())){
                    if(e.getEventType().toLowerCase().equals("birth")){
                        event = e;
                        LatLng ancestorLoc = new LatLng(e.getLatitude(), e.getLongitude());

                        Polyline polylineTmp = map.addPolyline(new PolylineOptions()
                                .width(thickness)
                                .clickable(false)
                                .add(location, ancestorLoc)
                                .color(black));

                        for(Person p: DataCache.family.getData()){
                            if(p.getPersonID().equals(personInfo.getMotherID())){
                                parent = p;
                            }
                        }
                        foundBirth = true;
                        DrawFamilyLines(parent, e, thickness - 10);
                    }
                }
            }

            if(!foundBirth){
                for(Event e: mapEvents){
                    if(e.getPersonID().equals(personInfo.getMotherID())){
                        temp.add(e);
                    }
                }

                for(Event e: temp){
                    if(e.getYear() < year){
                        year = e.getYear();
                    }
                }

                for(Event e: temp){
                    if(e.getYear().equals(year)){
                        LatLng ancestorLoc = new LatLng(e.getLatitude(), e.getLongitude());

                        Polyline polylineTmp = map.addPolyline(new PolylineOptions()
                                .width(thickness)
                                .clickable(false)
                                .add(location, ancestorLoc)
                                .color(black));

                        for(Person p: DataCache.family.getData()){
                            if(p.getPersonID().equals(personInfo.getMotherID())){
                                parent = p;
                            }
                        }
                        DrawFamilyLines(parent, e, thickness - 10);
                        break;
                    }
                }
            }
        }
        temp.clear();
    }

    //This section is if you need to wait before getting your map event details
    @Override
    public void onMapLoaded() {

    }
}