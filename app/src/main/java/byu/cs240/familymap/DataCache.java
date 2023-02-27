package byu.cs240.familymap;
// a singleton class, globally accessible and only has one instance
// store all the family tree data that we retrieve from the server

import model.*;
import result.PersonResult;
import result.EventResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

public class DataCache {

    private static DataCache instance;

    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache() {
        // prevent creating additional instances
    }

    private Person userPerson = null;
    private List<Event> userEvents = new ArrayList<>();

    public Person getUserPerson() {
        return userPerson;
    }

    public List<Event> getUserEvents() {
        return userEvents;
    }

    // PersonID -> Person
    private Map<String, Person> people = new HashMap<>();

    // EventID -> Event
    private Map<String, Event> events = new HashMap<>();

    // for each person, PersonID -> a list of events for that person
    private Map<String, List<Event>> peopleEvents = new HashMap<>();
    // set of PersonIDs for the paternal side and maternal side
    private Set<String> paternalAncestors;
    private Set<String> maternalAncestors;

//    Settings settings;

    public Person getPersonById (String personID) {
        Person foundPerson = this.people.get(personID);
        return foundPerson;
    }

    public Event getEventById (String eventID) {
        Event foundEvent = this.events.get(eventID);
        return foundEvent;
    }

    public List<Event> getPersonEvents (String personID) {
        List<Event> foundPersonEvents = this.peopleEvents.get(personID);
        return foundPersonEvents;
    }


    public void cachePeople(PersonResult result, String userPersonID) {
        Person[] data = result.getData();
        for (Person person : data) {
            this.people.put(person.getPersonID(), person);
            // if this person object belongs to the root user
            if (person.getPersonID().equals(userPersonID)) {
                this.userPerson = person;
            }
        }
    }

    public void cacheEvents(EventResult result, String userPersonID) {
        Event[] data = result.getData();
        for (Event event : data) {
            // eventID -> event object
            this.events.put(event.getEventID(), event);

            // personID -> a List of event objects
            String personID = event.getPersonID();
            List<Event> associatedEvents = this.peopleEvents.get(personID);
            if (associatedEvents == null) {
                associatedEvents = new ArrayList<>();
            }
            associatedEvents.add(event);
            this.peopleEvents.put(personID, associatedEvents);

            // if this event belongs to the root user
            if (personID.equals(userPersonID)) {
                this.userEvents.add(event);
            }
        }
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public Map<String, Event> getEvents() {
        return events;
    }

    public Map<String, List<Event>> getPeopleEvents() {
        return peopleEvents;
    }
}
