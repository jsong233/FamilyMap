package server;

import dataaccess.DataAccessException;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.RegisterService;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

public class RegisterHandler extends Handler {
    // exchange is an HTTP request/response pair
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        boolean success = false;

        try {
            // Only deal with POST requests
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                // get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();
                // get the request body input stream
                InputStream reqBody = exchange.getRequestBody();
                // read JSON string from the input stream
                String reqData = readString(reqBody);
                System.out.println(reqData);

                // parse the JSON string to RegisterRequest object
                Gson gson = new Gson();
                RegisterRequest request = (RegisterRequest)gson.fromJson(reqData, RegisterRequest.class);
                // call RegisterService to deal with the request
                RegisterService service = new RegisterService();
                // assertions won't slow Java down, ignored by default
                assert request != null : "request is null";
                RegisterResult result = service.register(request);

                success = result.isSuccess();

                if (success) {
                    // send the response back
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream resBody = exchange.getResponseBody();
                    // encode the result (RegisterResult object) to JSON string
                    // and write it to the OutputStream
                    String resData = gson.toJson(result);
                    writeString(resData, resBody);
                    resBody.close();
                }
            }

            if (!success) {
                // The HTTP request was invalid somehow, return a "bad request" status code
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
