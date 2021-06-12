

package org.lagoontech.bookingroom.model.db;

import org.lagoontech.bookingroom.logic.UserInfo;
import org.lagoontech.bookingroom.model.User;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDb {
    public static int getUserCount() {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS count FROM user", null);

            if (cursor.moveToNext()) {
                return cursor.getInt(cursor.getColumnIndexOrThrow("count"));
            }
            return 0;
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    /**
     * @param email
     * @return
     */
    public static User get(String email) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM user WHERE email == ? LIMIT 1",
                    new String[] {
                        email
                    });

            if (cursor.moveToNext()) {
                return new User(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("salt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_admin")) > 0);
            }
            return null;
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    /**
     * @param id
     * @return
     */
    public static User get(long id) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM user WHERE id == ? LIMIT 1",
                    new String[] {
                        "" + id
                    });

            if (cursor.moveToNext()) {
                return new User(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("password")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("salt")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("is_admin")) > 0);
            }
            return null;
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    /**
     * @param user
     * @return
     */
    public static User store(User user) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("Name", user.getName());
        value.put("email", user.getEmail());
        value.put("is_admin", user.isAdmin());
        value.put("salt", user.getSalt());
        value.put("password", user.getPassword());
        db.insert("user", null, value);
        return UserDb.get(user.getEmail());
    }

    /**
     * @param user
     * @return
     */
    public static User update(UserInfo user) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("name", user.getName());
        value.put("email", user.getEmail());
        value.put("salt", user.getSalt());
        value.put("password", user.getPassword());
        db.update("user", value, "id = ?", new String[] { "" + user.getId() });
        return UserDb.get(user.getId());
    }
}
