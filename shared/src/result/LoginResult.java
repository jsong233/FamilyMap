package result;

public class LoginResult extends RegisterResult{

    public LoginResult(String message, boolean success) {
        super(message, success);
    }

    public LoginResult(String authtoken, String username, String personID, boolean success) {
        super(authtoken, username, personID, success);
    }
}
