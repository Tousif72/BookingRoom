

package org.lagoontech.bookingroom.model;


/**
 * @author hannu
 */
public class User {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final int salt;
    private final boolean admin;

    public User(String name, String email) {
        this(null, name, email, null, -1, false);
    }

    public User(Long id, String name, String email, String password, int salt, boolean admin) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.salt = salt;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getSalt() {
        return salt;
    }
    
    public boolean isAdmin() {
        return admin;
    }
}
