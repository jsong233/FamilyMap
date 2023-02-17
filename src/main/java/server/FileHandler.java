package server;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

import com.sun.net.httpserver.*;

public class FileHandler extends Handler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        boolean success = false;

        try {
            // only deal with GET requests
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                // get the request URL from the exchange
                String urlPath = exchange.getRequestURI().toString();
                // If urlPath is null or "/", set urlPath = "/index.html"
                if (urlPath == null || urlPath.equals("") || urlPath.equals("/")) {
                    urlPath = "/index.html";
                }
                // "/" or "/index.html" --> file path web/index.html
                // "/favicon.ico" --> web/favicon.ico
                // "/css/main.css" --> web/css/main.css
                String filePath = "web" + urlPath;
                System.out.println("The request is: " + filePath);

                OutputStream resBody = exchange.getResponseBody();
                File file = new File(filePath);

                if (!file.exists()) {
                    // return 404 if the file does not exist
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);
                    // define a new file path which contains the 404 page
                    filePath = "web/HTML/404.html";
                    file = new File(filePath);
                    // send the 404 page to the response body
                    Files.copy(file.toPath(), resBody);
                }
                else {
                    // if the file exist, read the file and write it
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    // copies a file to an output stream
                    Files.copy(file.toPath(), resBody);
                }

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
        }
    }
}
