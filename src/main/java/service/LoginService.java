package service;

import dataaccess.*;
import model.User;
import request.LoginRequest;
import result.LoginResult;

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
                    String authtoken = aDao.findAuthToken(username);
                    assert authtoken != null;
                    // Create and return success Result object
                    result = new LoginResult(authtoken, username, user.getPersonID(), true);
                }
                else {
                    result = new LoginResult("incorrect password", false);
                }
            }
            else {
                result = new LoginResult("username not found", false);
            }

            // Close database connection, COMMIT transaction
            db.closeConnection(true);
            if (result == null) {
                System.out.println("LoginResult result is null!");
            }
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
