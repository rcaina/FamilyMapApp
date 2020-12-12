package com.example.myfamilymap.Singleton;

import com.example.myfamilymap.Fragments.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.HashMap;

import Models.Event;
import Models.Person;
import Response.EventResponse;
import Response.LoginResponse;
import Response.PersonResponse;
import Response.RegisterResponse;

public class DataCache {

    private static DataCache instance;
    public static LoginResponse person;
    public static RegisterResponse newPerson;
    public static PersonResponse family;
    public static EventResponse events;

    public boolean storyLines;
    public boolean familyTreeLines;
    public boolean spouseLines;
    public boolean fatherEvents;
    public boolean motherEvents;
    public boolean maleEvents;
    public boolean femaleEvents;
    public boolean mapMenuActivity;

    public Person currentMapPerson;
    private Person userPerson;
    public Event currentMapEvent;
    public ArrayList<Event> allCurrentMapEvents = new ArrayList<>();
    ArrayList<String> eventTypes = new ArrayList<>();
    public MapFragment mapInfo;


    private HashMap<String, Person> people;
    private HashMap<String, Person> fatherSideMale;
    private HashMap<String, Person> fatherSideFemale;
    private HashMap<String, Person> motherSideMale;
    private HashMap<String, Person> motherSideFemale;
    public HashMap<String, Float> colorTypes;
    private ArrayList<Float> colors = new ArrayList<>();

    //might want to use synchronized (between private and static)
    public static DataCache getInstance(){
        if(instance == null){
            instance = new DataCache();
        }
        return instance;
    }
    private DataCache(){

        people = new HashMap<>();
        fatherSideMale = new HashMap<>();
        fatherSideFemale = new HashMap<>();
        motherSideMale = new HashMap<>();
        motherSideFemale = new HashMap<>();
        colorTypes = new HashMap<>();

        fatherEvents = true;
        motherEvents = true;
        maleEvents = true;
        femaleEvents = true;
        storyLines = true;
        spouseLines = true;
        familyTreeLines = true;
        mapMenuActivity = true;

        addColors();
    }

    private void addColors(){
        colors.add(BitmapDescriptorFactory.HUE_GREEN);
        colors.add(BitmapDescriptorFactory.HUE_BLUE);
        colors.add(BitmapDescriptorFactory.HUE_ORANGE);
        colors.add(BitmapDescriptorFactory.HUE_VIOLET);
        colors.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colors.add(BitmapDescriptorFactory.HUE_CYAN);
        colors.add(BitmapDescriptorFactory.HUE_YELLOW);
        colors.add(BitmapDescriptorFactory.HUE_ROSE);
        colors.add(BitmapDescriptorFactory.HUE_RED);
        colors.add(BitmapDescriptorFactory.HUE_AZURE);
    }


    public void setEventColors(){
        ArrayList<Event> allEvents = events.getData();

        for(Event event: allEvents){
            if(!eventTypes.contains(event.getEventType().toLowerCase())){
                eventTypes.add(event.getEventType().toLowerCase());
            }
        }

        int j = 0;

        for(String event: eventTypes){

            Integer colorSize = 10;
            if(j == colorSize - 1){
                j = 0;
            }

            colorTypes.put(event, colors.get(j));

            j++;
        }
    }

    public void setPeople(PersonResponse persons){

        for(Person p: persons.getData()){
            people.put(p.getPersonID(), p);
        }

        if(person != null){
            userPerson = people.get(person.getPersonID1());
        }
        else{
            userPerson = people.get(newPerson.getPersonID());
        }

        if(userPerson.getFatherID() != null){
            sortFatherSide(people.get(userPerson.getFatherID()));
        }
        if(userPerson.getMotherID() != null){
            sortMotherSide(people.get(userPerson.getMotherID()));
        }
    }

    private void sortFatherSide(Person currentPerson){
        if(currentPerson.getGender().toLowerCase().equals("m")){
            fatherSideMale.put(currentPerson.getPersonID(), currentPerson);
        }
        else{
            fatherSideFemale.put(currentPerson.getPersonID(), currentPerson);
        }

        if(currentPerson.getFatherID() != null){
            sortFatherSide(people.get(currentPerson.getFatherID()));
        }
        if(currentPerson.getMotherID() != null){
            sortFatherSide(people.get(currentPerson.getMotherID()));
        }
    }

    private void sortMotherSide(Person currentPerson){
        if(currentPerson.getGender().toLowerCase().equals("m")){
            motherSideMale.put(currentPerson.getPersonID(), currentPerson);
        }
        else{
            motherSideFemale.put(currentPerson.getPersonID(), currentPerson);
        }

        if(currentPerson.getFatherID() != null){
            sortMotherSide(people.get(currentPerson.getFatherID()));
        }
        if(currentPerson.getMotherID() != null){
            sortMotherSide(people.get(currentPerson.getMotherID()));
        }
    }

    public ArrayList<Event> getEvents(){

        allCurrentMapEvents.clear();

        if(userPerson.getGender().toLowerCase().equals("m") && maleEvents){
            for(Event e: DataCache.events.getData()){
                if(userPerson.getPersonID().equals(e.getPersonID())){
                    allCurrentMapEvents.add(e);
                }
                if(femaleEvents && userPerson.getSpouseID() != null){
                    if (userPerson.getSpouseID().equals(e.getPersonID())) {
                        allCurrentMapEvents.add(e);
                    }
                }
            }
        }
        else if(userPerson.getGender().toLowerCase().equals("f") && femaleEvents){
            for(Event e: DataCache.events.getData()){
                if(userPerson.getPersonID().equals(e.getPersonID())){
                    allCurrentMapEvents.add(e);
                }
                if(maleEvents && userPerson.getSpouseID() != null){
                    if (userPerson.getSpouseID().equals(e.getPersonID())) {
                        allCurrentMapEvents.add(e);
                    }
                }
            }
        }

        if(fatherEvents){
            if(maleEvents){
                for(Person p: fatherSideMale.values()){
                    for(Event e: events.getData()){
                        if(p.getPersonID().equals(e.getPersonID())){
                            allCurrentMapEvents.add(e);
                        }
                    }
                }
            }
            if(femaleEvents){
                for(Person p: fatherSideFemale.values()){
                    for(Event e: events.getData()){
                        if(p.getPersonID().equals(e.getPersonID())){
                            allCurrentMapEvents.add(e);
                        }
                    }
                }
            }
        }
        if(motherEvents){
            if(maleEvents){
                for(Person p: motherSideMale.values()){
                    for(Event e: events.getData()){
                        if(p.getPersonID().equals(e.getPersonID())){
                            allCurrentMapEvents.add(e);
                        }
                    }
                }
            }
            if(femaleEvents){
                for(Person p: motherSideFemale.values()){
                    for(Event e: events.getData()){
                        if(p.getPersonID().equals(e.getPersonID())){
                            allCurrentMapEvents.add(e);
                        }
                    }
                }
            }
        }

        return  allCurrentMapEvents;
    }

    public void destroy(){

        person = null;
        newPerson = null;
        family = null;
        events = null;


        currentMapPerson = null;
        userPerson = null;
        currentMapEvent = null;
        allCurrentMapEvents  = null;
        mapInfo = null;

        people.clear();
        fatherSideMale.clear();
        fatherSideFemale.clear();
        motherSideMale.clear();
        motherSideFemale.clear();
    }
}
