

package org.lagoontech.bookingroom.view;

import org.lagoontech.bookingroom.logic.MeetingInfo;

import android.graphics.Rect;

public class MeetingGeometry {
    // This is the space from the grid line to the event rectangle.
    private static int mCellMargin = 0;

    private static float mMinuteHeight;

    private static float mHourGap;
    private static float mMinEventHeight;

    float top;
    float bottom;
    float left;
    float right;

    static void setCellMargin(int cellMargin) {
        mCellMargin = cellMargin;
    }

    static void setHourGap(float gap) {
        mHourGap = gap;
    }

    static void setMinEventHeight(float height) {
        mMinEventHeight = height;
    }

    static void setHourHeight(float height) {
        mMinuteHeight = height / 60.0f;
    }

    // Computes the rectangle coordinates of the given event on the screen.
    // Returns true if the rectangle is visible on the screen.
    boolean computeEventRect(int currentJulianDay, int left, int top, int cellWidth, MeetingInfo meeting) {
        float cellMinuteHeight = mMinuteHeight;
        int startDay = meeting.getStartDay();
        int endDay = meeting.getEndDay();

        if (startDay > currentJulianDay || endDay < currentJulianDay) {
            return false;
        }

        int startTime = meeting.getStartMinutesSinceMidnight();
        int endTime = meeting.getEndMinutesSinceMidnight();

        // If the event started on a previous day, then show it starting
        // at the beginning of this day.
        if (startDay < currentJulianDay) {
            startTime = 0;
        }

        // If the event ends on a future day, then show it extending to
        // the end of this day.
        if (endDay > currentJulianDay) {
            endTime = WeekView.MINUTES_PER_DAY;
        }

        int startHour = startTime / 60;
        int endHour = endTime / 60;

        // If the end point aligns on a cell boundary then count it as
        // ending in the previous cell so that we don't cross the border
        // between hours.
        if (endHour * 60 == endTime)
            endHour -= 1;

        this.top = top;
        this.top += (int) (startTime * cellMinuteHeight);
        this.top += startHour * mHourGap;

        this.bottom = top;
        this.bottom += (int) (endTime * cellMinuteHeight);
        this.bottom += endHour * mHourGap;

        // Make the rectangle be at least mMinEventHeight pixels high
        if (this.bottom < this.top + mMinEventHeight) {
            this.bottom = this.top + mMinEventHeight;
        }

        float colWidth = (float) (cellWidth - 2 * mCellMargin);
        this.left = left + mCellMargin;
        this.right = this.left + colWidth;
        return true;
    }

    /**
     * Returns true if this event intersects the selection region.
     */
    boolean eventIntersectsSelection(Rect selection) {
        if (this.left < selection.right && this.right >= selection.left
                && this.top < selection.bottom && this.bottom >= selection.top) {
            return true;
        }
        return false;
    }

    /**
     * Computes the distance from the given point to the given event.
     */
    float pointToEvent(float x, float y) {
        float left = this.left;
        float right = this.right;
        float top = this.top;
        float bottom = this.bottom;

        if (x >= left) {
            if (x <= right) {
                if (y >= top) {
                    if (y <= bottom) {
                        // x,y is inside the this rectangle
                        return 0f;
                    }
                    // x,y is below the this rectangle
                    return y - bottom;
                }
                // x,y is above the this rectangle
                return top - y;
            }

            // x > right
            float dx = x - right;
            if (y < top) {
                // the upper right corner
                float dy = top - y;
                return (float) Math.sqrt(dx * dx + dy * dy);
            }
            if (y > bottom) {
                // the lower right corner
                float dy = y - bottom;
                return (float) Math.sqrt(dx * dx + dy * dy);
            }
            // x,y is to the right of the this rectangle
            return dx;
        }
        // x < left
        float dx = left - x;
        if (y < top) {
            // the upper left corner
            float dy = top - y;
            return (float) Math.sqrt(dx * dx + dy * dy);
        }
        if (y > bottom) {
            // the lower left corner
            float dy = y - bottom;
            return (float) Math.sqrt(dx * dx + dy * dy);
        }
        // x,y is to the left of the this rectangle
        return dx;
    }
}
