package request;

public class FillRequest {
    private String username;
    private int generation = 4;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public FillRequest(String username, int generation) {
        this.username = username;
        this.generation = generation;
    }
}
