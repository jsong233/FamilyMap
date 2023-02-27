package byu.cs240.familymap;

import java.io.*;
import java.net.*;
import com.google.gson.*;

import request.*;
import result.*;

// creates and sends the HTTP requests
// ServerProxy, ServerFacade
// needs to have the same web APIs as the real server

public class ServerProxy {

    // LoginResult login(LoginRequest request)
    public LoginResult login(String serverHost, String serverPort, LoginRequest request) {

        LoginResult result = null;

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                System.out.println("Route successfully claimed.");

                InputStream resBody = http.getInputStream();
                String resData = readString(resBody);
                result = gson.fromJson(resData, LoginResult.class);
            }
            else {

                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream resBody = http.getErrorStream();
                String resData = readString(resBody);
                result = new LoginResult(resData, false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // RegisterResult register(RegisterRequest request)
    public RegisterResult register(String serverHost, String serverPort, RegisterRequest request) {

        RegisterResult result = null;

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");

            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                String resData = readString(resBody);

                result = gson.fromJson(resData, RegisterResult.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream resBody = http.getErrorStream();
                String resData = readString(resBody);
                result = new RegisterResult(resData, false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // PersonResult getPeople(GetPeopleRequest request)
    public PersonResult getPeople(String serverHost, String serverPort, PersonRequest request) {

        PersonResult result = null;

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", request.getAuthToken());
            http.addRequestProperty("Accept", "application/json");

            http.connect();


            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                String resData = readString(resBody);
                Gson gson = new Gson();
                result = gson.fromJson(resData, PersonResult.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream resBody = http.getErrorStream();
                String resData = readString(resBody);
                result = new PersonResult(resData, false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // EventResult getEvents(GetEventsResult request)
    public EventResult getEvents(String serverHost, String serverPort, EventRequest request) {

        EventResult result = null;

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod("GET");
            http.setDoOutput(false);

            http.addRequestProperty("Authorization", request.getAuthToken());
            http.addRequestProperty("Accept", "application/json");

            http.connect();


            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream resBody = http.getInputStream();
                String resData = readString(resBody);
                Gson gson = new Gson();
                result = gson.fromJson(resData, EventResult.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream resBody = http.getErrorStream();
                String resData = readString(resBody);
                result = new EventResult(resData, false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    /*
		The readString method shows how to read a String from an InputStream.
	*/
    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
