

package org.lagoontech.bookingroom.logic;

import org.lagoontech.bookingroom.model.User;
import org.lagoontech.bookingroom.model.db.UserDb;

/**
 * @author jush
 */
public class UserManager {
    public static UserInfo getUser(String email) {
        User user = UserDb.get(email);
        if (user == null) {
            return null;
        }
        return new UserInfo(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
                user.getSalt());
    }

    /**
     * @param admin
     * @param newPassStr
     */
    public static UserInfo updatePassword(UserInfo admin) {
        User user = UserDb.update(admin);
        return new UserInfo(user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getSalt());
    }
}
