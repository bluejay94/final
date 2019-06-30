package com.crea_si.eviacam.api_demo;

import com.crea_si.eviacam.api.IDockPanelEventListener;
import com.crea_si.eviacam.api.IGamepadEventListener;
import com.crea_si.eviacam.api.GamepadButtons;
import com.crea_si.eviacam.api.IMouseEventListener;
import com.crea_si.eviacam.api.SlaveMode;
import com.crea_si.eviacam.api.SlaveModeStatusListener;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.widget.Toast;

public class MainActivity extends Activity implements
        SlaveModeStatusListener, IGamepadEventListener,
        IMouseEventListener, IDockPanelEventListener,GestureDetector.OnGestureListener {
    public static final int SWIPE_THRESH = 100;
    public static final int SWIPE_VELO_THRESH = 100;

    private GestureDetector gestureDetector;
    private static final String TAG= "EVIACAM_API_DEMO";
    
    private static final float INC= 0.05f;


    // slave mode remote facade
    private SlaveMode mSlaveMode;

    private PointF mPoint= new PointF(0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gestureDetector = new GestureDetector(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_connect) {
            SlaveMode.connect(this, this);
        }
        else if (id == R.id.action_disconnect) {
            if (mSlaveMode!= null) mSlaveMode.disconnect();
        }
        else if (id == R.id.action_start) {
            if (mSlaveMode!= null) mSlaveMode.start();
        }
        else if (id == R.id.action_stop) {
            if (mSlaveMode!= null) mSlaveMode.stop();
        }
        else if (id == R.id.action_register_events) {
            if (mSlaveMode!= null) {
                mSlaveMode.registerGamepadListener(this);
                mSlaveMode.registerMouseListener(this);
                mSlaveMode.registerDockPanelListener(this);
            }
        }
        else if (id == R.id.action_unregister_events) {
            if (mSlaveMode!= null) {
                mSlaveMode.unregisterGamepadListener();
                mSlaveMode.unregisterMouseListener();
                mSlaveMode.unregisterDockPanelListener();
            }
        }
        else if (id == R.id.action_gamepad_abs_mode) {
            if (mSlaveMode!= null) {
                mSlaveMode.setOperationMode(SlaveMode.GAMEPAD_ABSOLUTE);
            }
        }
        else if (id == R.id.action_gamepad_rel_mode) {
            if (mSlaveMode!= null) {
                mSlaveMode.setOperationMode(SlaveMode.GAMEPAD_RELATIVE);
            }
        }
        else if (id == R.id.action_mouse_mode) {
            if (mSlaveMode!= null) {
                mSlaveMode.setOperationMode(SlaveMode.MOUSE);
            }
        }
        /*
         * These don't need an active connection
         */
        else if (id == R.id.action_preferences) {
            SlaveMode.openSettingsActivity(this);
        }
        else if (id == R.id.action_gamepad_preferences) {
            SlaveMode.openGamepadSettingsActivity(this);
        }
        else if (id == R.id.action_mouse_preferences) {
            SlaveMode.openMouseSettingsActivity(this);
        }

        return super.onOptionsItemSelected(item);
    }

    /* This is called from a secondary thread */
    @Override
    public void buttonPressed(int arg0) throws RemoteException {
        switch (arg0) {
        case GamepadButtons.PAD_DOWN:
            mPoint.y+= INC;
            break;
        case GamepadButtons.PAD_DOWN_LEFT:
            mPoint.x-= INC;
            mPoint.y+= INC;
            break;
        case GamepadButtons.PAD_LEFT:
            mPoint.x-= INC;
            break;
        case GamepadButtons.PAD_UP_LEFT:
            mPoint.x-= INC;
            mPoint.y-= INC;
            break;
        case GamepadButtons.PAD_UP:
            mPoint.y-= INC;
            break;
        case GamepadButtons.PAD_UP_RIGHT:
            mPoint.x+= INC;
            mPoint.y-= INC;
            break;
        case GamepadButtons.PAD_RIGHT:
            mPoint.x+= INC;
            break;
        case GamepadButtons.PAD_DOWN_RIGHT:
            mPoint.x+= INC;
            mPoint.y+= INC;
            break;
        }
        
        if (mPoint.x< -1.0f) mPoint.x= -1.0f;
        else if (mPoint.x> 1.0f) mPoint.x= 1.0f;
        if (mPoint.y< -1.0f) mPoint.y= -1.0f;
        else if (mPoint.y> 1.0f) mPoint.y= 1.0f;
        
        MyCanvas cv= (MyCanvas) findViewById(R.id.the_canvas);
        cv.setPositionNorm(mPoint);
        cv.postInvalidate();
    }

    /* This is called from a secondary thread */
    @Override
    public void buttonReleased(int arg0) throws RemoteException {
        // Currently does nothing
    }

    /* This is called from a secondary thread */
    @Override
    public void onMouseEvent(MotionEvent event) throws RemoteException {
        if (event.getAction()== MotionEvent.ACTION_MOVE) {
            MyCanvas cv= (MyCanvas) findViewById(R.id.the_canvas);
            mPoint.x= event.getRawX();
            mPoint.y= event.getRawY();
            cv.setPosition(mPoint);
            cv.postInvalidate();
        }
        else if (event.getAction()== MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Click!");
        }
        else if (event.getAction()== MotionEvent.ACTION_UP) {
            Log.d(TAG, "Clack!");
        }
        event.recycle();


    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false;
        float diffY = moveEvent.getRawY() - downEvent.getRawY();
        float diffX = moveEvent.getRawX() - downEvent.getRawX();
        // which was greater?  movement across Y or X?
        if (Math.abs(diffX) > Math.abs(diffY)) {
            // right or left swipe
            if (Math.abs(diffX)> SWIPE_THRESH && Math.abs(velocityX) > SWIPE_VELO_THRESH) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }
        } else {
            // up or down swipe
            if (Math.abs(diffY) > SWIPE_THRESH && Math.abs(velocityY)> SWIPE_VELO_THRESH) {
                if (diffY > 0) {
                    onSwipeBottom();
                } else {
                    onSwipeTop();
                }
                result = true;
            }
        }

        return result;
    }

    private void onSwipeTop() {
        Toast.makeText(this, "Swipe Top", Toast.LENGTH_LONG).show();
    }

    private void onSwipeBottom() {
        Toast.makeText(this, "Swipe Bottom", Toast.LENGTH_LONG).show();
    }

    private void onSwipeLeft() {
        Toast.makeText(this, "Swipe Left", Toast.LENGTH_LONG).show();
    }

    private void onSwipeRight() {
        Toast.makeText(this, "Swipe Right", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onDockMenuOption(int option) {
        Log.d(TAG, "Menu option:" + option);
    }

    @Override
    public void onReady(SlaveMode sm) {
        mSlaveMode= sm;

        // Uncomment if you wish to start listening to events ASAP
        //if (mSlaveMode!= null) mSlaveMode.registerListener(this);
    }

    @Override
    public void onDisconnected() {
        mSlaveMode = null;
    }

    // TODO: remove
    @Override
    public IBinder asBinder() {
        return null;
    }
}
