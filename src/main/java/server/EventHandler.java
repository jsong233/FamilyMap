package server;


import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dataaccess.DataAccessException;
import request.EventRequest;
import result.EventResult;
import service.EventService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class EventHandler extends Handler {
    // GetPeopleResult getPeople(GetPeopleRequest request)


    // look up the authtoken in the database using the authtokenDAO
    // if it's valid, it will also tell which user is making the request
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {
                // get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();
                // an authtoken in the Authorization Header is required
                if (reqHeaders.containsKey("Authorization")) {
                    // Extract the auth token from the "Authorization" header
                    String authToken = reqHeaders.getFirst("Authorization");
                    // get the URI string: /event/eventID or /event
                    String[] params = exchange.getRequestURI().toString().split("/");
                    System.out.println(exchange.getRequestURI());
                    System.out.println(Arrays.toString(params));

                    EventService service = new EventService();
                    EventResult result = null;

                    // if the request URL is /person
                    if (params.length == 2) {
                        EventRequest request = new EventRequest(authToken);
                        // returns all events for all family members of the current user determined by the authToken
                        result = service.getEvents(request);
                        success = true;
                    }
                    // if the request URL is /person/personID
                    else if (params.length == 3) {
                        String eventID = params[2];
                        EventRequest request = new EventRequest(authToken, eventID);
                        // returns the single Event object with the given ID
                        result = service.getEvent(request);
                        success = true;
                    }

                    if (success) {
                        // send the response back
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream resBody = exchange.getResponseBody();
                        Gson gson = new Gson();
                        String resData = gson.toJson(result);
                        writeString(resData, resBody);
                        resBody.close();
                    }
                }
            }
            if (!success) {
                // the request method was not GET, or it does not contain an Authorization header
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                // not sending any response body
                exchange.getResponseBody().close();
            }

        }
        catch (DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        } catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }
}
