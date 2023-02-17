package dataaccess;

import model.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    private DatabaseManager db;
    private User firstUser;
    private User secondUser;
    private UserDAO uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new DatabaseManager();

        firstUser = new User("jsong233", "Ab123!xyz", "jsong97224@gmail.com",
                "Joy", "Song", "f", "#001");

        secondUser = new User("cyber_song", "iamnotArobot!", "jsong@mathematics.byu.edu",
                "Joy", "Song", "f", "#002");

        Connection conn = db.getConnection();
        uDao = new UserDAO(conn);
        uDao.clearUsers();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {

        uDao.insertUser(firstUser);
        User compareTest = uDao.retrieveUser(firstUser.getUsername());

        assertNotNull(compareTest);
        assertEquals(firstUser, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        uDao.insertUser(firstUser);
        assertThrows(DataAccessException.class, () -> uDao.insertUser(firstUser));
    }

    @Test
    public void retrievePass() throws DataAccessException {

        uDao.insertUser(firstUser);
        uDao.insertUser(secondUser);

        // retrieve the second user
        User compareTest = uDao.retrieveUser(secondUser.getUsername());

        // make sure the retrieved user is not null
        assertNotNull(compareTest);
        assertEquals(secondUser, compareTest);
    }

    @Test
    public void retrieveFail() throws DataAccessException {
        uDao.insertUser(firstUser);
        // retrieve the data that is not in the table
        User compareTest = uDao.retrieveUser(secondUser.getUsername());
        assertNull(compareTest);
    }

    @Test
    public void clearPass() throws DataAccessException {
        uDao.insertUser(firstUser);
        uDao.insertUser(secondUser);

        uDao.clearUsers();
        List<User> compareTest = uDao.getAllUsers();
        assertNull(compareTest);
    }
}

