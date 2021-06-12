

package org.lagoontech.bookingroom.logic;

import org.lagoontech.bookingroom.model.Meeting;
import org.lagoontech.bookingroom.model.db.MeetingDb;
import org.lagoontech.bookingroom.validation.FieldError;
import org.lagoontech.bookingroom.validation.ObjectError;
import org.lagoontech.bookingroom.validation.ValidationResult;
import org.lagoontech.bookingroom.validation.Validator;

import android.text.format.Time;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author hannu
 */
public class MeetingInfoValidator implements Validator<MeetingInfo> {
    private static final int MAX_DAYS = 7;
    private static final long MAX_START_TIME_INCREASE_IN_MILLIS = MAX_DAYS * 24 * 60 * 60 * 1000;
    private static final int MAX_HOURS = 2;
    private static final long MAX_LENGTH_IN_MILLIS = MAX_HOURS * 60 * 60 * 1000;

    private static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
    private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    /**
     * @see org.hibernate.validator.constraints.impl.EmailValidator
     */
    private Pattern pattern = java.util.regex.Pattern.compile(
            "^" + ATOM + "+(\\." + ATOM + "+)*@"
                    + DOMAIN
                    + "|"
                    + IP_DOMAIN
                    + ")$",
            java.util.regex.Pattern.CASE_INSENSITIVE
            );

    @Override
    public ValidationResult fullValidate(MeetingInfo meetingInfo) {
        final ValidationResult errors = new ValidationResult();
        final long nowMillis = System.currentTimeMillis();
        final Time maximumStartingTime = new Time();
        maximumStartingTime.set(nowMillis + MAX_START_TIME_INCREASE_IN_MILLIS);
        if (meetingInfo.getStart().after(maximumStartingTime))
            errors.addError(new FieldError(meetingInfo, "start", "afterMax",
                    "Starting time too far ahead in the future"));

        final Time maximumEndingTime = new Time();
        maximumEndingTime.set(meetingInfo.getStart().toMillis(true) + MAX_LENGTH_IN_MILLIS);
        if (meetingInfo.getEnd().after(maximumEndingTime))
            errors.addError(new FieldError(meetingInfo, "end", "tooLong",
                    "Meeting can't be longer than " + MAX_HOURS + " hours."));

        errors.addAll(minimumValidate(meetingInfo));
        return errors;
    }

    public ValidationResult minimumValidate(MeetingInfo meetingInfo) {
        final ValidationResult errors = new ValidationResult();

        if ( meetingInfo.getTitle().trim().length() == 0 ) {
            errors.addError(new FieldError(meetingInfo, "title", "empty", "Meeting title is required"));
        }

        final Time now = new Time();
        now.setToNow();
        if (meetingInfo.getStart().before(now))
            errors.addError(new FieldError(meetingInfo, "start", "beforeNow",
                    "Starting time in past"));

        if (!meetingInfo.getStart().before(meetingInfo.getEnd()))
            errors.addError(new FieldError(meetingInfo, "end", "beforeStart",
                    "Ending time before starting time"));

        UserInfo userInfo = meetingInfo.getUser();
        String contactName = userInfo.getName();
        if (contactName == null || contactName.trim().length() == 0) {
            errors.addError(new FieldError(userInfo, "name", "empty", "Contact name is required"));
        }

        String contactMail = userInfo.getEmail();
        if (contactMail == null || contactMail.trim().length() == 0) {
            errors.addError(new FieldError(userInfo, "mail", "empty", "Contact mail is required"));
        } else if (!pattern.matcher(contactMail).matches()) {
            errors.addError(new FieldError(userInfo, "mail", "invalid", "Contact mail is invalid"));
        }

        List<Meeting> meetings = MeetingDb
                .getMeetings(meetingInfo.getStart(), meetingInfo.getEnd());

        // If there are more than one meeting in that time slot then for sure is
        // an error. The same meeting can't be returned twice from the model
        // layer
        if (meetings.size() > 1) {
            errors.addError(new ObjectError(meetingInfo, "clashing", "Clashing meeting"));
        } else if (meetings.size() == 1) {
            // In case the current meeting is a new one (it doesn't have id) or
            // if it has it is different than the one returned by the model
            // layer
            if (meetingInfo.getId() == null || !meetingInfo.getId()
                    .equals(meetings.get(0).getId())) {
                errors.addError(new ObjectError(meetingInfo, "clashing", "Clashing meeting"));
            }
        }
        return errors;
    }
}
