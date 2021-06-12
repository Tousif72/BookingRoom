

package org.lagoontech.bookingroom.model;

import android.text.format.Time;

/**
 * @author hannu
 */
public class Meeting {
    private final Long id;
    private final Long userId;
    private final Time start;
    private final Time end;
    private final String title;
    private final int pincode;

    public Meeting(Long userId, String title, Time start, Time end, int pincode) {
        this(null, userId, title, start, end, pincode);
    }

    public Meeting(Long id, Long userId, String title, Time start, Time end, int pincode) {
        this.id = id;
        this.userId = userId;
        this.start = start;
        this.end = end;
        this.title = title;
        this.pincode = pincode;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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
    
    public int getPin() {
        return pincode;
    }
}
