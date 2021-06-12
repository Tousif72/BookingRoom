
package org.lagoontech.bookingroom;

import org.lagoontech.bookingroom.logic.UserInfo;
import org.lagoontech.bookingroom.logic.UserManager;
import org.lagoontech.bookingroom.model.User;
import org.lagoontech.bookingroom.model.db.DataBaseHelper;
import org.lagoontech.bookingroom.model.db.UserDb;
import org.lagoontech.bookingroom.view.WeekView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String DEFAULT_ADMIN_EMAIL = "bookingroom_admin@aaltovg.com";
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView title;
    private WeekView currentView;
    private Runnable screensaverLauncher;
    private Dialog dialog = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBaseHelper.setContext(this.getBaseContext());
        setContentView(R.layout.main);

        title = (TextView) findViewById(R.id.title);
        currentView = (WeekView) findViewById(R.id.weekView);
        currentView.setTitleTextView(title);
        screensaverLauncher = new ScreensaverLauncher(this);

        if (UserDb.get(DEFAULT_ADMIN_EMAIL) == null) {
            User admin = new User(null, "bookingroom_admin", DEFAULT_ADMIN_EMAIL, "012345", -1,
                    true);
            UserDb.store(admin);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        DataBaseHelper.getInstance().close();
        dismissChangePasswordDialog();
    }

    private void dismissChangePasswordDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.week_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.goToday:
                Time now = new Time();
                now.setToNow();
                now.normalize(true);
                currentView.setSelectedDay(now);
                break;
            case R.id.aboutPopup:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.changeAdminPassword:
                showChangePasswordDialog();
                break;
        }
        return false;
    }

    private void showChangePasswordDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.changepassword);
        dialog.setTitle("Change administrator password");
        dialog.setCancelable(true);
        dialog.show();

        Button okBt = (Button) dialog.findViewById(R.id.passButtonOk);
        final EditText currentPass = (EditText) dialog.findViewById(R.id.currentpass);
        final EditText newPass = (EditText) dialog.findViewById(R.id.newpass);
        final EditText newPassAgain = (EditText) dialog.findViewById(R.id.newpassagain);

        okBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo admin = UserManager.getUser(DEFAULT_ADMIN_EMAIL);
                if (admin == null) {
                    Toast error = Toast.makeText(MainActivity.this,
                            "Default admin user not found!", Toast.LENGTH_LONG);
                    error.show();
                    dismissChangePasswordDialog();
                } else {
                    String newPassStr = newPass.getText().toString();
                    if (!newPassStr.equals(newPassAgain.getText().toString())) {
                        Toast error = Toast.makeText(MainActivity.this,
                                "New passwords don't match!", Toast.LENGTH_LONG);
                        error.show();
                    } else {
                        if (currentPass.getText().toString().equals(admin.getPassword())) {
                            UserInfo newAdmin = new UserInfo(admin.getId(), admin.getName(), admin
                                    .getEmail(), newPassStr, admin.getSalt());
                            UserManager.updatePassword(newAdmin);
                            Toast error = Toast.makeText(MainActivity.this,
                                    "Admin password changed!", Toast.LENGTH_LONG);
                            error.show();
                            dismissChangePasswordDialog();
                        } else {
                            Toast error = Toast.makeText(MainActivity.this,
                                    "Invalid current password!", Toast.LENGTH_LONG);
                            error.show();
                        }
                    }
                }
            }
        });

        Button cancelBt = (Button) dialog.findViewById(R.id.passButtonCancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissChangePasswordDialog();
            }
        });
    }

    public Runnable getScreensaverLauncher() {
        return screensaverLauncher;
    }

    private static final class ScreensaverLauncher implements Runnable {
        private final MainActivity mainActivity;

        public ScreensaverLauncher(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void run() {
            Log.d(TAG, "Starting screen saver!");
            mainActivity.startActivity(new Intent(mainActivity, Screensaver.class));
        }
    }
}
