package service;

import dataaccess.*;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import result.LoginResult;

import java.util.UUID;

public class LoginService {

    public LoginResult login(LoginRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        LoginResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String username = request.getUsername();
            String password = request.getPassword();

            // use DAOs to do requested operation
            UserDAO uDao = new UserDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            User user = uDao.retrieveUser(username);

            // if this user is valid
            if (user != null) {
                // if the entered password is correct
                if (user.getPassword().equals(password)) {
                    // generate a new authtoken every time signed in
                    String authtoken = UUID.randomUUID().toString();
                    AuthToken authToken = new AuthToken(authtoken, username);

                    if (aDao.findAuthToken(username) != null) {
                        // if the user was registered or logged in before
                        aDao.updateAuthToken(authToken);
                    } else {
                        // if this is the first time the user logs in (no authtoken found)
                        aDao.insertAuthToken(authToken);
                    }

                    // Create and return success Result object
                    result = new LoginResult(authtoken, username, user.getPersonID(), true);
                }
                else {
                    result = new LoginResult("Error: incorrect password", false);
                }
            }
            else {
                result = new LoginResult("Error: username not found", false);
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
            result = new LoginResult("Error occurred when accessing the database", false);
            return result;
        }
    }
}
