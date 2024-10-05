package common.actions;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Password.
     */
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username + ":" + password;
    }

    @Override
    public int hashCode() {
        return username.hashCode() + password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof User userObj) {
            return username.equals(userObj.getUsername()) && password.equals(userObj.getPassword());
        }
        return false;
    }
}
