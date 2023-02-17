package server;


import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import dataaccess.DataAccessException;
import request.PersonRequest;
import result.PersonResult;
import service.PersonService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;

public class PersonHandler extends Handler {
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
                    // get the URI string: /person/personID or /person
                    String[] params = exchange.getRequestURI().toString().split("/");
                    System.out.println(exchange.getRequestURI());
                    System.out.println(Arrays.toString(params));

                    PersonService service = new PersonService();
                    PersonResult result = null;

                    // if the request URL is /person
                    if (params.length == 2) {
                        PersonRequest request = new PersonRequest(authToken);
                        // returns family members of the current user determined by the authToken
                        result = service.getPeople(request);
                        success = true;
                    }
                    // if the request URL is /person/personID
                    else if (params.length == 3) {
                        String personID = params[2];
                        PersonRequest request = new PersonRequest(authToken, personID);
                        // returns the single Person object with the given ID
                        result = service.getPerson(request);
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
