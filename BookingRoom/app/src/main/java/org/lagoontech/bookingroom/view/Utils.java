

package org.lagoontech.bookingroom.view;

import android.text.format.Time;

import java.util.Calendar;

/**
 * @author jush
 */
public class Utils {
    /**
     * Get first day of week as android.text.format.Time constant.
     * 
     * @return the first day of week in android.text.format.Time
     */
    public static int getFirstDayOfWeek() {
        int startDay = Calendar.getInstance().getFirstDayOfWeek();
        if (startDay == Calendar.SATURDAY) {
            return Time.SATURDAY;
        } else if (startDay == Calendar.MONDAY) {
            return Time.MONDAY;
        } else {
            return Time.SUNDAY;
        }
    }

    /**
     * Determine whether the column position is Saturday or not.
     * 
     * @param column the column position
     * @param firstDayOfWeek the first day of week in android.text.format.Time
     * @return true if the column is Saturday position
     */
    public static boolean isSaturday(int column, int firstDayOfWeek) {
        return (firstDayOfWeek == Time.SUNDAY && column == 6)
                || (firstDayOfWeek == Time.MONDAY && column == 5)
                || (firstDayOfWeek == Time.SATURDAY && column == 0);
    }

    /**
     * Determine whether the column position is Sunday or not.
     * 
     * @param column the column position
     * @param firstDayOfWeek the first day of week in android.text.format.Time
     * @return true if the column is Sunday position
     */
    public static boolean isSunday(int column, int firstDayOfWeek) {
        return (firstDayOfWeek == Time.SUNDAY && column == 0)
                || (firstDayOfWeek == Time.MONDAY && column == 6)
                || (firstDayOfWeek == Time.SATURDAY && column == 1);
    }
}
