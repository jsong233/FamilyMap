package service;

import model.*;
import dataaccess.*;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.UUID;

public class RegisterService {
    // be careful if one service is calling another service
    // since each service opens its own database connection
    // one way to resolve this: pass in the connection to another service
    
    public RegisterResult register(RegisterRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        RegisterResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String username = request.getUsername();
            String password = request.getPassword();
            String email = request.getEmail();
            String firstName = request.getFirstName();
            String lastName = request.getLastName();
            String gender = request.getGender();

            // use DAOs to do requested operation
            UserDAO uDao = new UserDAO(db.getConnection());
            PersonDAO pDao = new PersonDAO(db.getConnection());
            EventDAO eDao = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            // if this username is valid (not generated before)
            if (uDao.retrieveUser(username) == null) {

                String personID = UUID.randomUUID().toString();
                String authtoken = UUID.randomUUID().toString();

                // Creates a new user account (user row in the database)
                User user = new User(username, password, email, firstName, lastName, gender, personID);
                uDao.insertUser(user);

                // Creates a new authtoken object (authtoken row in the database)
                AuthToken authToken = new AuthToken(authtoken, username);
                aDao.insertAuthToken(authToken);

                // Generates 4 generations of ancestor data for the new user
                GenTreeService genTree = new GenTreeService(user, pDao, eDao);
                genTree.generate(4);

                // Logs the user in
                // what should I do here?
                // Create and return success Result object
                result = new RegisterResult(authtoken, username, personID, true);
            }
            else {
                result = new RegisterResult("Error: username already taken", false);
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
            result = new RegisterResult("Error occurred when accessing the database", false);
            return result;
        }
    }
}
