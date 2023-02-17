package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataaccess.DataAccessException;
import request.FillRequest;
import result.FillResult;
import service.FillService;

import java.io.*;
import java.net.HttpURLConnection;

public class FillHandler extends Handler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        boolean success = false;

        try {
            // Only deal with POST requests
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                // get the URL parameters: host/fill/susan or host/fill/susan/3
                String[] params = exchange.getRequestURI().toString().split("/");
                System.out.println(params);
                // extract information from the URL path
                assert params.length >= 3;
                String username = params[2];
                int generation = 4;
                if (params.length > 3) {
                    generation = Integer.parseInt(params[3]);
                }

                // call fill service
                FillRequest request = new FillRequest(username, generation);
                FillService service = new FillService();
                FillResult result = service.fill(request);

                // send the response back
                Gson gson = new Gson();
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
