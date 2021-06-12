

package org.lagoontech.bookingroom.logic;

/**
 * @author jush
 */
public interface MeetingEventListener {
    public void onNewMeeting(Long meetingId);

    public void onDeleteMeeting(Long meetingId);

    public void onEditMeeting(Long meetingId);

}
