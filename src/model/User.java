package model;

/**
 * Turns out I don't really need this for anything, at least until I decide to add the ability to create new
 * users from within the application...
 */
public class User {
    private String name;

    public String getName() {
        return name;
    }

    public User(String name) {
        this.name = name;
    }
}
