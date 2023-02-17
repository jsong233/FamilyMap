package service;

import dataaccess.*;
import result.ClearResult;


public class ClearService {

    public ClearResult clear() throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        ClearResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // use DAOs to do requested operation
            UserDAO uDao = new UserDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());
            EventDAO eDao = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            uDao.clearUsers();
            pDao.clearPersons();
            eDao.clearEvents();
            aDao.clearAuthTokens();

            // Close database connection, COMMIT transaction
            db.closeConnection(true);
            result = new ClearResult("Clear succeeded.", true);
            return result;
        }
        catch (DataAccessException e) {

            e.printStackTrace();

            // close connection and ROLLBACK transaction
            db.closeConnection(false);

            // Create and return failure Result object
            result = new ClearResult("Error occurred when accessing the database", false);
            return result;
        }
    }
}
