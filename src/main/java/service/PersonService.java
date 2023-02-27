package service;

import dataaccess.*;
import model.AuthToken;
import model.Person;
import request.PersonRequest;
import result.EventResult;
import result.PersonResult;

import java.util.List;

public class PersonService {

    public PersonResult getPerson(PersonRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        PersonResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String authtoken = request.getAuthToken();
            String personID = request.getPersonID();

            // use DAOs to do requested operation
            PersonDAO pDao = new PersonDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            // find the user associated with this authtoken
            AuthToken authToken = aDao.retrieveAuthToken(authtoken);

            if (authToken != null) {
                String username = authToken.getUsername();

                // find the person associated with this personID
                Person person = pDao.retrievePerson(personID);

                // if this person is associated with the current user
                if (person.getAssociatedUsername().equals(username)) {

                    // Returns the single Person object with the specified ID
                    result = new PersonResult(person.getAssociatedUsername(), person.getPersonID(), person.getFirstName(),
                            person.getLastName(), person.getGender(), person.getFatherID(), person.getMotherID(),
                            person.getSpouseID(), true);
                } else {
                    result = new PersonResult("Error: this person is not associated with the current user.", false);
                }
            } else {
                result = new PersonResult("Error: authtoken not found", false);
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
            result = new PersonResult("Error occurred when accessing the database", false);
            return result;
        }
    }

    public PersonResult getPeople(PersonRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        PersonResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String authtoken = request.getAuthToken();

            // use DAOs to do requested operation
            PersonDAO pDao = new PersonDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            // find the user associated with this authtoken
            AuthToken authToken = aDao.retrieveAuthToken(authtoken);

            if (authToken != null) {
                String username = authToken.getUsername();

                // Returns all family members of the current user
                List<Person> people = pDao.getUserPersons(username);
                Person[] family = new Person[people.size()];
                family = people.toArray(family);

                result = new PersonResult(family, true);
            } else {
                result = new PersonResult("Error: authtoken not found", false);
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
            result = new PersonResult("Error occurred when accessing the database", false);
            return result;
        }
    }
}
