package result;

import model.Person;

public class PersonResult extends Result {
    private Person rootPerson;
    private Person[] family;

    public PersonResult(Person rootPerson, boolean success) {
        this.rootPerson = rootPerson;
        this.success = success;
    }

    public PersonResult(Person[] family, boolean success) {
        this.family = family;
        this.success = success;
    }

    public PersonResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
