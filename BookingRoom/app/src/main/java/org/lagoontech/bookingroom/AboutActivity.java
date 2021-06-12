
package org.lagoontech.bookingroom;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
    }
    
    /* 
     * Makes sure the About activity is finished
     * when calling the Screensaver. Otherwise the
     * user might be a bit confused
     */
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}