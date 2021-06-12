
package org.lagoontech.bookingroom.view;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/**
 * @author jush
 *
 */
public class GestureListener extends SimpleOnGestureListener {
    
    private WeekView mView;

    public GestureListener(WeekView view) {
        mView = view;
    }
    
    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        mView.doSingleTapUp(ev);
        return true;
    }

    @Override
    public void onLongPress(MotionEvent ev) {
        mView.doLongPress(ev);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mView.doScroll(e1, e2, distanceX, distanceY);
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mView.doFling(e1, e2, velocityX, velocityY);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent ev) {
        mView.doDown(ev);
        return true;
    }

}
