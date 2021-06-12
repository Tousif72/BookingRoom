
package org.lagoontech.bookingroom.logic;

import android.text.format.Time;

/**
 * @author hannu
 */
public class MeetingInfo {
    private final Long id;
    private final UserInfo user;
    private final Time start;
    private final Time end;
    private final String title;
    private int startDay;
    private int endDay;
    private int startMinutesSinceMidnight;
    private int endMinutesSinceMidnight;
    private int pincode;

    public MeetingInfo(UserInfo user, Time start, Time end, String title, int pincode) {
        this(null, user, start, end, title, pincode);
    }

    public MeetingInfo(Long id, UserInfo user, Time start, Time end, String title, int pincode) {
        this.id = id;
        this.user = user;
        this.start = start;
        this.end = end;
        this.title = title;
        this.pincode = pincode;

        startDay = Time.getJulianDay(start.normalize(true), start.gmtoff);
        startMinutesSinceMidnight = start.hour * 60 + start.minute;
        endDay = Time.getJulianDay(end.normalize(true), end.gmtoff);
        endMinutesSinceMidnight = end.hour * 60 + end.minute;
    }

    public Long getId() {
        return id;
    }

    public UserInfo getUser() {
        return user;
    }

    public Time getStart() {
        return start;
    }

    public Time getEnd() {
        return end;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @return the start Julian day
     */
    public int getStartDay() {
        return startDay;
    }

    /**
     * @return the end Julian day
     */
    public int getEndDay() {
        return endDay;
    }

    /**
     * @return the minutes since midnight till the start time
     */
    public int getStartMinutesSinceMidnight() {
        return startMinutesSinceMidnight;
    }

    /**
     * @return the minutes since midnight till the end time
     */
    public int getEndMinutesSinceMidnight() {
        return endMinutesSinceMidnight;
    }

    @Override
    public String toString() {
        return "MeetingInfo [id=" + id + ", start=" + start.format3339(false) + ", end="
                + end.format3339(false) + ", title=" + title
                + "]";
    }
    
    public int getPin() {
        return pincode;
    }
}
