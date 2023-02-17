package dataaccess;

import model.Event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventDAOTest {

    private DatabaseManager db;
    private Event firstEvent;
    private Event secondEvent;
    private EventDAO eDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Here we can set up any classes or variables we will need for each test
        // lets create a new instance of the Database class
        db = new DatabaseManager();
        // and a new event with random data
        firstEvent = new Event("Biking_123A", "Gale", "Gale123A",
                35.9f, 140.1f, "Japan", "Ushiku",
                "Biking_Around", 2016);

        secondEvent = new Event("BYU_CS240", "JoySong", "jsong233",
                12.8f, 200.4f, "United States", "Provo",
                "Taking_Class",2023);

        // Here, we'll open the connection in preparation for the test case to use it
        Connection conn = db.getConnection();
        //Then we pass that connection to the EventDAO, so it can access the database.
        eDao = new EventDAO(conn);
        //Let's clearEvents the database as well so any lingering data doesn't affect our tests
        eDao.clearEvents();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        // Here we close the connection to the database file, so it can be opened again later.
        // We will set commit to false because we do not want to save the changes to the database
        // between test cases.
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        // Start by inserting an event into the database.
        eDao.insertEvent(firstEvent);
        // Let's use a retrieveEvent method to get the event that we just put in back out.
        Event compareTest = eDao.retrieveEvent(firstEvent.getEventID());
        // First lets see if our retrieveEvent method found anything at all. If it did then we know that we got
        // something back from our database.
        assertNotNull(compareTest);
        // Now lets make sure that what we put in is the same as what we got out. If this
        // passes then we know that our insertEvent did put something in, and that it didn't change the
        // data in any way.
        // This assertion works by calling the equals method in the Event class.
        assertEquals(firstEvent, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        // Let's do this test again, but this time lets try to make it fail.
        // If we call the method the first time the event will be inserted successfully.
        eDao.insertEvent(firstEvent);

        // However, our sql table is set up so that the column "eventID" must be unique, so trying to insertEvent
        // the same event again will cause the insertEvent method to throw an exception, and we can verify this
        // behavior by using the assertThrows assertion as shown below.

        // Note: This call uses a lambda function. A lambda function runs the code that comes after
        // the "()->", and the assertThrows assertion expects the code that ran to throw an
        // instance of the class in the first parameter, which in this case is a DataAccessException.
        assertThrows(DataAccessException.class, () -> eDao.insertEvent(firstEvent));
    }

    @Test
    public void retrievePass() throws DataAccessException {
        // insertEvent some events into the database
        eDao.insertEvent(firstEvent);
        eDao.insertEvent(secondEvent);

        // retrieve the second event
        Event compareTest = eDao.retrieveEvent(secondEvent.getEventID());

        // make sure the retrieved event is not null
        assertNotNull(compareTest);
        assertEquals(secondEvent, compareTest);
    }

    @Test
    public void retrieveFail() throws DataAccessException {
        eDao.insertEvent(firstEvent);
        // retrieve the data that is not in the table
        Event compareTest = eDao.retrieveEvent(secondEvent.getEventID());
        assertNull(compareTest);
    }

    @Test
    public void clearPass() throws DataAccessException {
        eDao.insertEvent(firstEvent);
        eDao.insertEvent(secondEvent);
        // clearEvents all tables
        eDao.clearEvents();
        List<Event> compareTest = eDao.getAllEvents();
        assertNull(compareTest);
    }
}
