package server;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

/**
 * The Server class is the "main" class for the server
 * (i.e., it contains the "main" method for the server program).
 * 	When the server runs, all command-line arguments are passed in to Server.main.
 * 	For this server, the only command-line argument is the port number on which
 * 	the server should accept incoming client connections.
 */
public class Server {
    // The maximum number of waiting incoming connections to queue.
    private static final int MAX_WAITING_CONNECTIONS = 12;

    // Java provides an HttpServer class that can be used to embed an HTTP server in any Java program.
    // HttpServer is the class that actually implements the HTTP network protocol.
    // The "server" field contains the HttpServer instance for this program,
    // which is initialized in the "run" method below.
    private HttpServer server;

    private void run(String portNumber) {

        System.out.println("Initializing HTTP Server");

        try {
            // Create a new HttpServer object.
            // we create the object by calling the HttpServer.create static factory method.
            // this method returns a reference to the new object.
            server = HttpServer.create(
                    new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Indicate that we are using the default "executor". This line is necessary.
        server.setExecutor(null);

        System.out.println("Creating contexts");

        // When the HttpServer receives an HTTP request containing the specified URL path,
        // it will forward the request to the corresponding Handler class for processing.

        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill/", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());

        // All requests that do not match the other handler URLs will be passed to this handler.
        // These are requests to download a file from the server (e.g., web site files)
        server.createContext("/", new FileHandler());


        System.out.println("Starting server");
        // Tells the HttpServer to start accepting incoming client connections.
        // This method call will return immediately, and the "main" method for the program will also complete.
        // Even though the "main" method has completed, the program will continue running
        // because the HttpServer object we created is still running in the background.
        server.start();
        // Log message indicating that the server has successfully started.
        System.out.println("Server started");
    }

    public static void main(String[] args) {
        String portNumber = args[0];
        new Server().run(portNumber);
    }
}
