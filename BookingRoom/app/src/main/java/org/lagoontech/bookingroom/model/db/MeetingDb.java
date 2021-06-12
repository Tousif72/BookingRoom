
package org.lagoontech.bookingroom.model.db;

import org.lagoontech.bookingroom.logic.MeetingInfo;
import org.lagoontech.bookingroom.model.Meeting;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.List;

public class MeetingDb {
    public static List<Meeting> getMeetings(Time from, Time to) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        ArrayList<Meeting> records = new ArrayList<Meeting>();
        Cursor cursor = null;
        try {
            cursor = db
                    .rawQuery(
                            "SELECT id, user_id, pincode, title, start, end FROM meeting WHERE end > ? AND start < ?",
                            new String[] {
                                    "" + from.toMillis(false),
                                    "" + to.toMillis(false),
                            });

            while (cursor.moveToNext()) {
                Time startTime = new Time();
                startTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("start")));
                Time endTime = new Time();
                endTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("end")));

                Meeting m = new Meeting(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        startTime,
                        endTime,
                        cursor.getInt(cursor.getColumnIndexOrThrow("pincode"))
                        );
                records.add(m);
            }

            return records;
        } finally {
            if ( cursor != null ) {
                cursor.close();
            }
            db.close();
            DataBaseHelper.getInstance().close();
        }
    }

    public static int getMeetingCount() {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS count FROM meeting", null);

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
    public static Meeting get(long id) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db
                    .rawQuery(
                            "SELECT id, user_id, title, pincode, start, end FROM meeting WHERE id == ? LIMIT 1",
                            new String[] {
                                "" + id
                            });

            if (cursor.moveToNext()) {
                Time startTime = new Time();
                startTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("start")));
                Time endTime = new Time();
                endTime.set(cursor.getLong(cursor.getColumnIndexOrThrow("end")));

                return new Meeting(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getLong(cursor.getColumnIndexOrThrow("user_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        startTime,
                        endTime,
                        cursor.getInt(cursor.getColumnIndexOrThrow("pincode")));
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
     * @param meeting
     * @return
     */
    public static Meeting store(Meeting meeting) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("title", meeting.getTitle());
        value.put("user_id", meeting.getUserId());
        value.put("start", meeting.getStart().toMillis(false));
        value.put("end", meeting.getEnd().toMillis(false));
        value.put("pincode", meeting.getPin());
        final long id = db.insert("meeting", null, value);
        return MeetingDb.get(id);
    }

    public static Meeting update(MeetingInfo meeting) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put("title", meeting.getTitle());
        value.put("start", meeting.getStart().toMillis(false));
        value.put("end", meeting.getEnd().toMillis(false));

        db.update("meeting", value, "id = ?", new String[] { "" + meeting.getId() });
        return MeetingDb.get(meeting.getId());
    }

    /**
     * @param id
     * @return Number of rows affected
     */
    public static int delete(long id) {
        SQLiteDatabase db = DataBaseHelper.getInstance().getWritableDatabase();
        int result = db.delete("meeting", "id = ?", new String[] { ""+id });
        return result;
    }
}
