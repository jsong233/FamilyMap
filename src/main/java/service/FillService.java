package service;

import dataaccess.*;
import model.AuthToken;
import model.Person;
import model.User;
import request.FillRequest;
import result.FillResult;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class FillService {

    public FillResult fill(FillRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        FillResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String username = request.getUsername();
            int generation = request.getGeneration();

            // use DAOs to do requested operation
            UserDAO uDao = new UserDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());
            EventDAO eDao = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            User user = uDao.retrieveUser(username);

            // if this username is registered before
            if (user != null) {

                // Generates given generations of ancestor data for the new user
                GenTreeService genTree = new GenTreeService(user, pDao, eDao);
                genTree.clearFamily();
                genTree.generate(generation);

                String message = String.format("Successfully added %d generations of ancestor data to the database.", generation);
                result = new FillResult(message, true);
            }
            else {
                result = new FillResult("This user is not registered.", false);
            }

            // Close database connection, COMMIT transaction
            db.closeConnection(true);
            return result;
        }
        catch (DataAccessException e) {

            e.printStackTrace();

            // close connection and ROLLBACK transaction
            db.closeConnection(false);

            // Create and return failure Result object
            result = new FillResult("Error occurred when accessing the database", false);
            return result;
        }
    }
}
