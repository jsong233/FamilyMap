package dataaccess;

import model.Person;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDAOTest {

    private DatabaseManager db;
    private Person firstPerson;
    private Person secondPerson;
    private PersonDAO pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {

        db = new DatabaseManager();
        // and a new Person with random data
        firstPerson = new Person("#001", "jsong233", "Joy",
                "Song", "f", "#003", "#004",
                null);

        secondPerson = new Person("#002", "cyber_song", "Joy",
                "Song", "f", null, null,
                "#005");

        Connection conn = db.getConnection();
        pDao = new PersonDAO(conn);
        pDao.clearPersons();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        // insert the firstPerson with spouseID = null to see whether it works
        pDao.insertPerson(firstPerson);
        Person compareFirst = pDao.retrievePerson(firstPerson.getPersonID());
        assertNotNull(compareFirst);
        assertEquals(firstPerson, compareFirst);

        // insert the secondPerson with fatherID = motherID = null
        pDao.insertPerson(secondPerson);
        Person compareSecond = pDao.retrievePerson(secondPerson.getPersonID());
        assertNotNull(compareSecond);
        assertEquals(secondPerson, compareSecond);
    }

    @Test
    public void insertFail() throws DataAccessException {
        pDao.insertPerson(firstPerson);
        assertThrows(DataAccessException.class, () -> pDao.insertPerson(firstPerson));
    }

    @Test
    public void retrievePass() throws DataAccessException {
        // insertPerson some Persons into the database
        pDao.insertPerson(firstPerson);
        pDao.insertPerson(secondPerson);

        // retrieve the second Person
        Person compareTest = pDao.retrievePerson(secondPerson.getPersonID());

        // make sure the retrieved Person is not null
        assertNotNull(compareTest);
        assertEquals(secondPerson, compareTest);
    }

    @Test
    public void retrieveFail() throws DataAccessException {
        pDao.insertPerson(firstPerson);
        // retrieve the data that is not in the table
        Person compareTest = pDao.retrievePerson(secondPerson.getPersonID());
        assertNull(compareTest);
    }

    @Test
    public void clearPass() throws DataAccessException {
        pDao.insertPerson(firstPerson);
        pDao.insertPerson(secondPerson);
        // clearPersons all tables
        pDao.clearPersons();
        List<Person> compareTest = pDao.getAllPersons();
        assertNull(compareTest);
    }
}
