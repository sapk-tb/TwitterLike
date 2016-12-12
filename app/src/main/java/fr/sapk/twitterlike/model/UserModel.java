package fr.sapk.twitterlike.model;

/**
 * The type User.
 */
public class UserModel {

    private String username;

    @Override
    public String toString() {
        return "UserModel{" +
                "username='" + username + '\'' +
                '}';
    }

    /**
     * Instantiates a new User.
     *
     * @param username the username
     */
    public UserModel(String username) {
        this.username = username;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }


}
