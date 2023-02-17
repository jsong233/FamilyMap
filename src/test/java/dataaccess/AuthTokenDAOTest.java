package dataaccess;

import model.AuthToken;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDAOTest {

    private DatabaseManager db;
    private AuthToken firstAuthToken;
    private AuthToken secondAuthToken;
    private AuthTokenDAO aDao;

    @BeforeEach
    public void setUp() throws DataAccessException {

        db = new DatabaseManager();
        // and a new AuthToken with random data
        firstAuthToken = new AuthToken("abc123&987ZYX", "jsong233");

        secondAuthToken = new AuthToken("def456&654WVU", "cyber_song");

        Connection conn = db.getConnection();
        aDao = new AuthTokenDAO(conn);
        aDao.clearAuthTokens();
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {
        aDao.insertAuthToken(firstAuthToken);
        AuthToken compareTest = aDao.retrieveAuthToken(firstAuthToken.getAuthToken());
        assertNotNull(compareTest);
        assertEquals(firstAuthToken, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {
        aDao.insertAuthToken(firstAuthToken);
        assertThrows(DataAccessException.class, () -> aDao.insertAuthToken(firstAuthToken));
    }

    @Test
    public void retrievePass() throws DataAccessException {
        // insertAuthToken some AuthTokens into the database
        aDao.insertAuthToken(firstAuthToken);
        aDao.insertAuthToken(secondAuthToken);

        // retrieve the second AuthToken
        AuthToken compareTest = aDao.retrieveAuthToken(secondAuthToken.getAuthToken());

        // make sure the retrieved AuthToken is not null
        assertNotNull(compareTest);
        assertEquals(secondAuthToken, compareTest);
    }

    @Test
    public void retrieveFail() throws DataAccessException {
        aDao.insertAuthToken(firstAuthToken);
        // retrieve the data that is not in the table
        AuthToken compareTest = aDao.retrieveAuthToken(secondAuthToken.getAuthToken());
        assertNull(compareTest);
    }

    @Test
    public void clearPass() throws DataAccessException {
        aDao.insertAuthToken(firstAuthToken);
        aDao.insertAuthToken(secondAuthToken);
        // clearAuthTokens all tables
        aDao.clearAuthTokens();
        List<AuthToken> compareTest = aDao.getAllAuthTokens();
        assertNull(compareTest);
    }
}
