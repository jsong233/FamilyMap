package result;

public class RegisterResult extends Result {
    private String authtoken;
    private String username;
    private String personID;

    public RegisterResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public RegisterResult(String authtoken, String username, String personID, boolean success) {
        this.authtoken = authtoken;
        this.username = username;
        this.personID = personID;
        this.success = success;
    }

}
