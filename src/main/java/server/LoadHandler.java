package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import dataaccess.DataAccessException;
import request.LoadRequest;
import result.LoadResult;
import service.LoadService;

import java.io.*;
import java.net.HttpURLConnection;

public class LoadHandler extends Handler {

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

                // parse the JSON string to LoadRequest object
                Gson gson = new Gson();

                LoadRequest request = (LoadRequest)gson.fromJson(reqData, LoadRequest.class);

                LoadService service = new LoadService();

                assert request != null : "request is null";
                LoadResult result = service.load(request);

                // send the response back
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream resBody = exchange.getResponseBody();
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
        catch (IOException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
