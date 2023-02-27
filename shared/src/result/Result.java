package result;

public class Result {
    // The base Result class
    boolean success;
    String message;

    // default to be null so that
    // JSON won't return that field if not set

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
