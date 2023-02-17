package service;

// generate fake family trees
// shared by RegisterService and FillService
import model.*;
import dataaccess.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GenTreeService {
    private User user;
    private final PersonDAO pDao;
    private final EventDAO eDao;
    private SeedService sd = new SeedService();

    public GenTreeService(User user, PersonDAO pDao, EventDAO eDao) {
        this.user = user;
        this.pDao = pDao;
        this.eDao = eDao;
    }

    public void generate(int generation) {

        // Extract necessary info from the user
        String username = this.user.getUsername();
        String firstName = this.user.getFirstName();
        String lastName = this.user.getLastName();
        String gender = this.user.getGender();
        String personID = this.user.getPersonID();

        // Creates a new person object (DO NOT insert into the database)
        Person person = new Person(personID, username, firstName, lastName, gender, null, null, null);


        // Each user has Person objects that represent everyone in that user's family tree.
        // These Person objects share an identical associatedUsername and will each have a unique personID.
        List<Event> events = genEvents(person);
        // set year for the root person (year field not null)
        events.get(0).setYear(1997);
        events.get(1).setYear(2027);
        events.get(2).setYear(2080);
        genFamily(person, generation, events);

        // Each Person object may also have Events associated with them.
        // These Event objects share an identical associatedUsername and will each have a unique eventID.
        // They also have a personID that matches the person they are associated with.
    }

    /**
     * Generate given number of generations of family for the root person
     * @param root: A Person object, the root of the family tree
     * @param generation: the number of generations to be generated
     */
    public void genFamily(Person root, int generation, List<Event> events) {

        Random rand = new Random();

        // The exit of the recursion
        if (generation == 0) {
            try {
                pDao.insertPerson(root);
                eDao.insertEvents(events);
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            return;
        }

        // generate the mother and father Person objects for the current root person
        String[] motherName = sd.getRandomName("f").split(" ");
        String[] fatherName = sd.getRandomName("m").split(" ");

        Person mother = new Person(UUID.randomUUID().toString(), this.user.getUsername(),
                motherName[0], motherName[1], "f", null, null, null);
        Person father = new Person(UUID.randomUUID().toString(), this.user.getUsername(),
                fatherName[0], fatherName[1], "m", null, null, null);
        father.setSpouseID(mother.getPersonID());
        mother.setSpouseID(father.getPersonID());

        // generate the mother and father default Event[] objects (without years)
        List<Event> motherEvents = genEvents(mother);
        Event motherMarriage = motherEvents.get(1);
        List<Event> fatherEvents = genEvents(father);
        Event fatherMarriage = fatherEvents.get(1);
        fatherMarriage.setCity(motherMarriage.getCity());
        fatherMarriage.setCountry(motherMarriage.getCountry());
        fatherMarriage.setLatitude(motherMarriage.getLatitude());
        fatherMarriage.setLongitude(motherMarriage.getLongitude());

        // matching the mother and father events with the root person's events
        Integer birthYear = events.get(0).getYear();
        Integer[] parentYears = genParentYears(birthYear);
        motherEvents.get(0).setYear(parentYears[0]);
        fatherEvents.get(0).setYear(parentYears[0]);
        motherMarriage.setYear(parentYears[1]);
        fatherMarriage.setYear(parentYears[1]);
        motherEvents.get(2).setYear(parentYears[2]);
        fatherEvents.get(2).setYear(parentYears[2]);

        // update information for the root person
        root.setMotherID(mother.getPersonID());
        root.setFatherID(father.getPersonID());

        // insert the root person into the database
        try {
            pDao.insertPerson(root);
            eDao.insertEvents(events);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }

        // recurse on the mother and father of the current person
        genFamily(mother, generation - 1, motherEvents);
        genFamily(father, generation - 1, fatherEvents);
    }

    /**
     * Generate three basic events for the given person.
     * @param person: the person the generated events belong to.
     * @return: the list of newly generated events.
     */
    public List<Event> genEvents(Person person) {

        List<Event> events = new ArrayList<Event>();

        String personID = person.getPersonID();
        String[] eventTypes = {"birth", "marriage", "death"};

        for (String type : eventTypes) {
            SeedService.Location location = sd.getRandomLocation();
            Event event = new Event(UUID.randomUUID().toString(), this.user.getUsername(), personID,
                    location.getLatitude(), location.getLongitude(), location.getCountry(), location.getCity(),
                    type, null);
            events.add(event);
        }

        return events;
    }

    private Integer[] genParentYears(Integer childBirthYear) {

        Integer[] parentYears = new Integer[3];
        // parent birth year (born at least 13 years before their children, not give birth when older than 50)
        parentYears[0] = ThreadLocalRandom.current().nextInt(childBirthYear - 50, childBirthYear - 14);
        // parent marriage year (at least 13 years old when they are married)
        parentYears[1] = ThreadLocalRandom.current().nextInt(parentYears[0] + 13, childBirthYear);
        // parent death year (live no longer than 120, not die before their child is born)
        parentYears[2] = ThreadLocalRandom.current().nextInt(childBirthYear, parentYears[0] + 120);

        return parentYears;
    }

    public void clearFamily() throws DataAccessException {
        eDao.clearUserEvents(this.user.getUsername());
        pDao.clearUserPersons(this.user.getUsername());
    }
}
