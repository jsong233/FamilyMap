package byu.cs240.familymap;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.annotation.SuppressLint;
import android.media.metrics.Event;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import model.Person;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;

public class LoginFragment extends Fragment {

    private final static String NAME_KEY = "name";
    private final static String SUCCESS_KEY = "success";

    private EditText serverHostField, serverPortField, usernameField, passwordField, emailField, firstNameField, lastNameField;
    private RadioGroup genderField;
    private RadioButton selectedGender;

    private Button loginButton, registerButton;

    private String serverHost, serverPort, username, password, email, firstName, lastName, gender;

    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // extract views and widgets
        serverHostField = (EditText) view.findViewById(R.id.serverHostField);
        serverPortField = (EditText) view.findViewById(R.id.serverPortField);
        usernameField = (EditText) view.findViewById(R.id.usernameField);
        passwordField = (EditText) view.findViewById(R.id.passwordField);
        emailField = (EditText) view.findViewById(R.id.emailField);
        firstNameField = (EditText) view.findViewById(R.id.firstNameField);
        lastNameField = (EditText) view.findViewById(R.id.lastNameField);
        genderField = (RadioGroup) view.findViewById(R.id.genderField);
        int selectedID = genderField.getCheckedRadioButtonId();
        selectedGender = (RadioButton) view.findViewById(selectedID);

        loginButton = view.findViewById(R.id.loginButton);
        registerButton = view.findViewById(R.id.registerButton);

        // specify event listeners
        serverHostField.addTextChangedListener(textChangedListener);
        serverPortField.addTextChangedListener(textChangedListener);
        usernameField.addTextChangedListener(textChangedListener);
        passwordField.addTextChangedListener(textChangedListener);
        firstNameField.addTextChangedListener(textChangedListener);
        lastNameField.addTextChangedListener(textChangedListener);
        emailField.addTextChangedListener(textChangedListener);

        genderField.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedGender = (RadioButton) group.findViewById(checkedId);
                gender = selectedGender.getText().toString();
                tryEnableButtons();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set up a handler to process messages from the background task
                // and make updates on the UI thread
                @SuppressLint("HandlerLeak")
                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        if (bundle.getBoolean(SUCCESS_KEY)) {
                            Toast.makeText(getActivity(), "Welcome, " + bundle.getString(NAME_KEY), Toast.LENGTH_SHORT).show();
                            // notify the MainActivity to switch the fragments
                            listener.notifyDone();
                        } else {
                            Toast.makeText(getActivity(), "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                // set up background task
                LoginTask loginTask = new LoginTask(uiThreadMessageHandler, serverHost, serverPort, username, password);
                ExecutorService loginExecutor = Executors.newSingleThreadExecutor();
                loginExecutor.submit(loginTask);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("HandlerLeak")
                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        if (bundle.getBoolean(SUCCESS_KEY)) {
                            Toast.makeText(getActivity(), "Welcome, " + bundle.getString(NAME_KEY), Toast.LENGTH_SHORT).show();
                            // notify the MainActivity to switch the fragments
                            listener.notifyDone();
                        } else {
                            Toast.makeText(getActivity(), "Register Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                // set up background task
                RegisterTask registerTask = new RegisterTask(uiThreadMessageHandler, serverHost, serverPort,
                        username, password, email, firstName, lastName, gender);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(registerTask);
            }
        });

        return view;
    }

    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        private String serverHost;
        private String serverPort;
        private String username;
        private String password;

        public LoginTask(Handler messageHandler, String serverHost, String serverPort, String username, String password) {
            this.messageHandler = messageHandler;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            LoginRequest request = new LoginRequest(username, password);

            ServerProxy server = new ServerProxy();
            LoginResult result = server.login(serverHost, serverPort, request);

            if (!result.isSuccess()) {
                // If the login request fails, display an Android toast indicating that the login failed.
                sendMessage(false);
            } else {
                // If the login request succeeds, use the HttpURLConnection again
                // to retrieve the logged-in user’s family data from the server
                String fullName = getData(this.serverHost, this.serverPort, result.getAuthtoken(), result.getPersonID());

                // When the family data is received, display an Android
                // toast containing the logged-in user’s first and last names
                sendMessage(fullName, true);
            }
        }

        private void sendMessage(boolean success) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, success);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }

        private void sendMessage(String fullName, boolean success) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, success);
            messageBundle.putString(NAME_KEY, fullName);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private static class RegisterTask implements Runnable {
        private final Handler messageHandler;
        private String serverHost;
        private String serverPort;
        private String username;
        private String password;
        private String email;
        private String firstName;
        private String lastName;
        private String gender;


        public RegisterTask(Handler messageHandler, String serverHost, String serverPort,
                            String username, String password, String email,
                            String firstName, String lastName, String gender) {
            this.messageHandler = messageHandler;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.username = username;
            this.password = password;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
        }

        @Override
        public void run() {
            RegisterRequest request = new RegisterRequest(username, password, email,
                    firstName, lastName, gender);
            ServerProxy server = new ServerProxy();
            RegisterResult result = server.register(serverHost, serverPort, request);

            if (!result.isSuccess()) {
                sendMessage(false);
            } else {
                String fullName = getData(this.serverHost, this.serverPort, result.getAuthtoken(), result.getPersonID());
                sendMessage(fullName, true);
            }
        }

        private void sendMessage(boolean success) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, success);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }

        private void sendMessage(String fullName, boolean success) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean(SUCCESS_KEY, success);
            messageBundle.putString(NAME_KEY, fullName);
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private static String getData(String serverHost, String serverPort, String authtoken, String personID) {

        // retrieve the logged-in user's family data from the server
        PersonRequest personRequest = new PersonRequest(authtoken);
        EventRequest eventRequest = new EventRequest(authtoken);

        ServerProxy server = new ServerProxy();

        PersonResult personResult = server.getPeople(serverHost, serverPort, personRequest);
        EventResult eventResult = server.getEvents(serverHost, serverPort, eventRequest);

        DataCache data = DataCache.getInstance();
        data.cachePeople(personResult, personID);
        data.cacheEvents(eventResult, personID);

        Person userPerson = data.getUserPerson();
        return userPerson.getFirstName() + " " + userPerson.getLastName();
    }

    TextWatcher textChangedListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            serverHost = serverHostField.getText().toString().trim();
            serverPort = serverPortField.getText().toString().trim();
            username = usernameField.getText().toString().trim();
            password = passwordField.getText().toString().trim();
            firstName = firstNameField.getText().toString().trim();
            lastName = lastNameField.getText().toString().trim();
            email = emailField.getText().toString().trim();
            tryEnableButtons();
        }
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    };

    private void tryEnableButtons() {
        registerButton.setEnabled(!serverHost.isEmpty() && !serverPort.isEmpty() &&
                !username.isEmpty() && !password.isEmpty() &&
                !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && gender != null);

        loginButton.setEnabled(!serverHost.isEmpty() && !serverPort.isEmpty() &&
                !username.isEmpty() && !password.isEmpty());
    }
}