package service;

import dataaccess.AuthTokenDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.EventDAO;
import model.AuthToken;
import model.Event;
import request.EventRequest;
import result.EventResult;

import java.util.List;

public class EventService {

    public EventResult getEvent(EventRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        EventResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String authtoken = request.getAuthToken();
            String eventID = request.getEventID();

            // use DAOs to do requested operation
            EventDAO eDao = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            // find the user associated with this authtoken
            AuthToken authToken = aDao.retrieveAuthToken(authtoken);
            String username = authToken.getUsername();

            // find the event associated with this eventID
            Event event = eDao.retrieveEvent(eventID);

            // if this event is associated with the current user
            if (event.getAssociatedUsername().equals(username)) {

                // Returns the single Event object with the specified ID
                result = new EventResult(event, true);
            }
            else {
                result = new EventResult("This event is not associated with the current user.", false);
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
            result = new EventResult("Error occurred when accessing the database", false);
            return result;
        }
    }

    public EventResult getEvents(EventRequest request) throws DataAccessException {

        DatabaseManager db = new DatabaseManager();

        EventResult result = null;

        try {
            // Open database connection;
            db.openConnection();

            // extract the data from request
            String authtoken = request.getAuthToken();

            // use DAOs to do requested operation
            EventDAO eDao = new EventDAO(db.getConnection());
            AuthTokenDAO aDao = new AuthTokenDAO(db.getConnection());

            // find the user associated with this authtoken
            AuthToken authToken = aDao.retrieveAuthToken(authtoken);
            String username = authToken.getUsername();

            // Returns all events for all family members of the current user
            List<Event> events = eDao.getUserEvents(username);
            Event[] data = new Event[events.size()];
            data = events.toArray(data);

            result = new EventResult(data, true);

            // Close database connection, COMMIT transaction
            db.closeConnection(true);
            return result;
        }
        catch (DataAccessException e) {

            e.printStackTrace();

            // close connection and ROLLBACK transaction
            db.closeConnection(false);

            // Create and return failure Result object
            result = new EventResult("Error occurred when accessing the database", false);
            return result;
        }
    }

}
