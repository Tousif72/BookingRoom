
package org.lagoontech.bookingroom;

import org.lagoontech.bookingroom.logic.MeetingInfo;
import org.lagoontech.bookingroom.logic.MeetingManager;
import org.lagoontech.bookingroom.logic.UserInfo;
import org.lagoontech.bookingroom.logic.UserManager;
import org.lagoontech.bookingroom.validation.ObjectError;
import org.lagoontech.bookingroom.validation.ValidationException;
import org.lagoontech.bookingroom.validation.ValidationResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.format.Time;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;

public class MeetingActivity extends Activity {

    private static final String TAG = MeetingActivity.class.getSimpleName();

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_START_HOUR = "hour";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_PIN = "pincode";

    AlertDialog.Builder alertDialog = null;
    Dialog dialog = null;
    EditText titleEdit, nameEdit, emailEdit, pinText;
    TimePicker startPicker, endPicker;
    TextView meetingHeader;
    Button buttonOk, buttonCancel, buttonDelete, buttonDateSelector;

    private int day;
    private int month;
    private int year;

    static final int DATE_DIALOG_ID = 0;

    /**
     * Contains the current meeting being shown or null if it's new
     */
    private MeetingInfo mMeeting = null;

    private static enum ActionEnum {
        NEW_AS_ADMIN, EDIT, DELETE;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting);

        this.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        meetingHeader = (TextView) findViewById(R.id.meetingHeader);
        startPicker = (TimePicker) findViewById(R.id.startPicker);
        endPicker = (TimePicker) findViewById(R.id.endPicker);
        titleEdit = (EditText) findViewById(R.id.titleEdit);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        buttonOk = (Button) findViewById(R.id.buttonOK);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonDateSelector = (Button) findViewById(R.id.buttonDateSelector);

        startPicker.setIs24HourView(true);
        endPicker.setIs24HourView(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Long meetingId = extras.getLong(EXTRA_ID);
            if (meetingId != 0) {
                // Get the meeting info with its user info.
                mMeeting = MeetingManager.getMeeting(meetingId);
                setValuesForEditing(mMeeting);
            } else {
                setValuesForNew(extras.getInt(EXTRA_DAY), extras.getInt(EXTRA_START_HOUR));
            }
        }
        alertDialog = new AlertDialog.Builder(this);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time start = getStartTime();

                Time end = getEndTime(start);

                if (mMeeting != null) {
                    popup(ActionEnum.EDIT, start, end);
                } else {
                    createNewMeeting(false, start, end);
                }

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup(ActionEnum.DELETE, null, null);
            }
        });

        buttonDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
    }

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int rYear, int rMonth, int rDay) {
            year = rYear;
            month = rMonth;
            day = rDay;

            // Update date in UI
            Time newTime = new Time();
            newTime.set(rDay, rMonth, rYear);
            updateHeaderDate(newTime);
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, year, month, day);
        }
        return null;
    }

    private void popup(final ActionEnum action, final Time start, final Time end) {
        dialog = new Dialog(MeetingActivity.this);
        dialog.setContentView(R.layout.editpin);
        pinText = (EditText) dialog.findViewById(R.id.pincode);
        String title = "Introduce your pin code";
        if (action == ActionEnum.NEW_AS_ADMIN) {
            title = "Introduce admin password";
            pinText.setTransformationMethod(new PasswordTransformationMethod());
            pinText.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        }
        dialog.setTitle(title);
        dialog.setCancelable(true);
        dialog.show();

        Button pinButtonOk = (Button) dialog.findViewById(R.id.pinButtonOk);

        pinButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean check = checkPin(pinText.getText().toString());
                Boolean checkAdmin = false;
                if (!check) {
                    checkAdmin = checkAdminPass(pinText.getText().toString());
                }
                if (check || checkAdmin) {
                    switch (action) {
                        case DELETE:
                            if (mMeeting == null) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Unable to delete meeting", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                MeetingManager.delete(mMeeting.getId());
                                // If meeting has been deleted as a normal user
                                // show a
                                // simple toast messsage. If deleted as admin
                                // show a dialog
                                if (check) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Meeting deleted", Toast.LENGTH_SHORT);
                                    toast.show();
                                } else if (checkAdmin) {
                                    alertDialog.setTitle("Meeting deleted");
                                    alertDialog.setCancelable(false);
                                    alertDialog.setMessage("Please contact "
                                            + mMeeting.getUser().getEmail() +
                                            " to inform about his/her meeting being cancelled.");
                                    alertDialog.setNeutralButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                        int which) {
                                                    finish();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }
                            // The dialog when deleting a meeting as admin
                            // already takes
                            // care of finishing the activity
                            if (!checkAdmin) {
                                finish();
                            }
                            break;
                        case EDIT:
                            if (mMeeting == null) {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Unable to edit meeting", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                updateMeeting(MeetingActivity.this, start, end);
                            }
                            finish();
                            break;
                        case NEW_AS_ADMIN:
                            createNewMeeting(true, start, end);
                            break;
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Wrong pin",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
                dialog.dismiss();
            }
        });

        Button pinButtonCancel = (Button) dialog.findViewById(R.id.pinButtonCancel);
        pinButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * @param pin
     * @return
     */
    private Boolean checkAdminPass(String pin) {
        UserInfo adminUser = UserManager.getUser(MainActivity.DEFAULT_ADMIN_EMAIL);
        if (adminUser == null) {
            return false;
        }
        return adminUser.getPassword().equals(pin);
    }

    /**
     * @param int1
     * @param int2
     */
    private void setValuesForNew(int day, int startHour) {
        Time start = new Time();
        start.setJulianDay(day);
        start.hour = startHour;
        start.minute = 0;

        Time end = new Time();
        end.setJulianDay(day);
        end.hour = startHour + 1;
        if (end.hour > 23)
            end.hour = 0;
        end.minute = 0;

        setTimeValues(start, end);
    }

    private void setTimeValues(Time start, Time end) {
        updateHeaderDate(start);
        
        // Avoid a nasty bug when user taps the lowest (empty) line on the week view grid
        if (start.hour >= 24) {
            start.hour = 23;
        }

        startPicker.setCurrentHour(start.hour);
        startPicker.setCurrentMinute(start.minute);

        endPicker.setCurrentHour(end.hour);
        endPicker.setCurrentMinute(end.minute);

        day = start.monthDay;
        month = start.month;
        year = start.year;
    }

    private void setValuesForEditing(MeetingInfo meeting) {
        // Show delete button if editing a meeting
        buttonDelete.setVisibility(View.VISIBLE);
        buttonOk.setText("Update");

        // Prevent people editing/deleting past events
        if (meeting.getEnd().toMillis(false) < System.currentTimeMillis()) {
            buttonDelete.setVisibility(View.INVISIBLE);
            buttonOk.setVisibility(View.INVISIBLE);
            buttonDateSelector.setVisibility(View.INVISIBLE);
        }

        setTimeValues(meeting.getStart(), meeting.getEnd());
        titleEdit.setText(meeting.getTitle());
        nameEdit.setText(meeting.getUser().getName());
        emailEdit.setText(meeting.getUser().getEmail());
    }

    private boolean checkPin(String userPin) {
        Bundle extras = getIntent().getExtras();
        Integer meetingId = extras.getInt(EXTRA_PIN);
        Log.d(TAG, "User pin code: " + userPin.toString() + " Pin code: " + meetingId.toString());

        return userPin.equals(meetingId.toString());
    }

    private void updateMeeting(final Activity meetingActivity, Time start, Time end) {
        try {
            UserInfo updateUser = new UserInfo((long) mMeeting.getUser().getId(),
                    nameEdit.getText().toString(), emailEdit.getText().toString());
            MeetingInfo updateMI = new MeetingInfo(
                    mMeeting.getId(),
                    updateUser,
                    start,
                    end,
                    titleEdit.getText().toString(),
                    mMeeting.getPin());
            MeetingManager.update(updateMI);
            Toast toast = Toast.makeText(getApplicationContext(), "Meeting updated",
                    Toast.LENGTH_SHORT);
            toast.show();
        } catch (ValidationException e) {
            showError(e);
        }
    }

    private boolean createNewMeeting(boolean asAdmin, Time start, Time end) {
        try {
            MeetingInfo myMI;
            if (asAdmin) {
                myMI = MeetingManager.bookAsAdmin(start, end, titleEdit.getText().toString(),
                        nameEdit.getText().toString(), emailEdit.getText().toString());
            } else {
                myMI = MeetingManager.book(start, end, titleEdit.getText().toString(), nameEdit
                        .getText().toString(), emailEdit.getText().toString());
            }

            alertDialog.setTitle("Booking PIN code: " + myMI.getPin());
            alertDialog.setCancelable(false);
            alertDialog
                    .setMessage("Please don't forget the PIN code if you want to cancel this meeting.");
            alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // If we reach this point then booking went ok
                    finish();
                }
            });
            alertDialog.show();
            return true;
        } catch (ValidationException e) {
            showError(e);
            return false;
        }
    }

    private void showError(ValidationException e) {
        // Initially set a generic error message
        String errorMessage = "Please check all the fields!";
        ValidationResult result = e.getErrors();
        List<ObjectError> errors = result.getErrors();
        if (!errors.isEmpty())
            errorMessage = errors.get(0).getMessage();
        Toast.makeText(MeetingActivity.this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void updateHeaderDate(Time time) {
        SpannableString contentUnderline = new SpannableString(
                getResources().getString(R.string.meetingHeaderText) + " - "
                        + time.format("%d/%m/%Y"));
        contentUnderline.setSpan(new UnderlineSpan(), 0, contentUnderline.length(), 0);
        meetingHeader.setText(contentUnderline);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meeting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookAdmin:
                Time start = getStartTime();
                Time end = getEndTime(start);
                popup(ActionEnum.NEW_AS_ADMIN, start, end);
                break;
        }
        return false;
    }

    private Time getStartTime() {
        Time start = new Time();
        start.set(0, startPicker.getCurrentMinute(), startPicker.getCurrentHour(), day,
                month, year);
        start.normalize(true);
        return start;
    }

    private Time getEndTime(Time start) {
        Time end = new Time();
        end.set(0, endPicker.getCurrentMinute(), endPicker.getCurrentHour(), day, month,
                year);
        end.normalize(true);

        // Add one day to the end date if is earlier than start
        if (end.before(start)) {
            end.monthDay++;
            end.normalize(true);
        }
        return end;
    }
}
