package service;

import dataaccess.*;
import model.*;
import request.LoadRequest;
import result.ClearResult;
import result.LoadResult;


import java.util.Arrays;
import java.util.UUID;

public class LoadService {

    public LoadResult load(LoadRequest request) throws DataAccessException {

        // clear all data from the database
        ClearService clearService = new ClearService();
        ClearResult clearResult = clearService.clear();

        DatabaseManager db = new DatabaseManager();
        LoadResult result = null;

        if (clearResult.isSuccess()) {
            try {
                // Open database connection;
                db.openConnection();

                // extract the data from request
                User[] users = request.getUsers();
                Person[] persons = request.getPersons();
                Event[] events = request.getEvents();

                // use DAOs to do requested operation
                UserDAO uDao = new UserDAO(db.getConnection());
                PersonDAO pDao = new PersonDAO(db.getConnection());
                EventDAO eDao = new EventDAO(db.getConnection());

                uDao.insertUsers(Arrays.asList(users));
                pDao.insertPersons(Arrays.asList(persons));
                eDao.insertEvents(Arrays.asList(events));

                String message = String.format("Successfully added %d users, %d persons, and %d events " +
                        "to the database.", users.length, persons.length, events.length);
                result = new LoadResult(message, true);

                // Close database connection, COMMIT transaction
                db.closeConnection(true);
                return result;
            }
            catch (DataAccessException e) {

                e.printStackTrace();

                // close connection and ROLLBACK transaction
                db.closeConnection(false);

                // Create and return failure Result object
                result = new LoadResult("Error occurred when accessing the database", false);
                return result;
            }
        }
        else {
            result = new LoadResult("Error occurred when clearing database", false);
            return result;
        }
    }
}
