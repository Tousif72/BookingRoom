

package org.lagoontech.bookingroom.logic;

/**
 * @author hannu
 */
public class UserInfo {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final Integer salt;

    public UserInfo(Long id, String name, String email) {
        this(id, name, email, null, null);
    }

    public UserInfo(String name, String email) {
        this(null, name, email, null, null);
    }

    public UserInfo(Long id, String name, String email, String password, Integer salt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.salt = salt;
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

    public Integer getSalt() {
        return salt;
    }
}
