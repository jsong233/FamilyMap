package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataaccess.DataAccessException;
import request.LoginRequest;
import result.LoginResult;
import service.LoginService;

import java.io.*;
import java.net.HttpURLConnection;

public class LoginHandler extends Handler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        boolean success = false;

        try {
            // Only deal with POST requests
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {
                // get the request body input stream
                InputStream reqBody = exchange.getRequestBody();
                // read JSON string from the input stream
                String reqData = readString(reqBody);
                System.out.println(reqData);

                // parse the JSON string to RegisterRequest object
                Gson gson = new Gson();
                LoginRequest request = (LoginRequest)gson.fromJson(reqData, LoginRequest.class);

                LoginService service = new LoginService();
                assert request != null : "request is null";
                LoginResult result = service.login(request);

                success = result.isSuccess();

                if (success) {
                    // send the response back
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream resBody = exchange.getResponseBody();
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
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
