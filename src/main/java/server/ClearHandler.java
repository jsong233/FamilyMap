package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dataaccess.DataAccessException;
import result.ClearResult;
import service.ClearService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class ClearHandler extends Handler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        boolean success = false;

        try {
            // Only deal with POST requests
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // call ClearService to deal with the request
                ClearService service = new ClearService();
                ClearResult result = service.clear();

                // send the response back
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream resBody = exchange.getResponseBody();
                // encode the result (RegisterResult object) to JSON string
                // and write it to the OutputStream
                Gson gson = new Gson();
                String resData = gson.toJson(result);
                writeString(resData, resBody);
                resBody.close();

                success = true;
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
        }
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }
}


