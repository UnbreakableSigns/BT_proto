package jiepastor.brailletouch_proto.Keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputConnection;

import java.util.ArrayList;
import java.util.HashMap;

import jiepastor.brailletouch_proto.Braille.BraillePattern;
import jiepastor.brailletouch_proto.Braille.KeySet;
import jiepastor.brailletouch_proto.Setting;
import jiepastor.brailletouch_proto.View.Layout;

/**
 * Keyboard class, handles the user inputs
 */

public class IME extends InputMethodService{

    private BraillePattern brailleModel = new BraillePattern();

    private Layout layoutView;

    private Speech speech;
    private GestureDetector gestureRecognizer;
    private TapRecognizer tapRecognizer = new TapRecognizer();
    private Vibrator vibrator = null;
    private HashMap<Integer, TouchPointer> currentPointers = new HashMap<>();

    public enum GESTURE_TYPE{ SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, NONE}
    private GESTURE_TYPE lastGesture = GESTURE_TYPE.NONE;
    private int CURRENT_KEYBOARD_MODE = KeySet.LOWER_CASE_MODE;

    private AccessibilityManager accessibilityManager;

    @Override
    public View onCreateInputView() {
        layoutView = new Layout(this);
        layoutView.setOnTouchListener(mTouchListener);

        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if(accessibilityManager.isTouchExplorationEnabled()){
            layoutView.setOnHoverListener(new View.OnHoverListener(){

                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    return mTouchListener.onTouch(view,motionEvent);
                }
            });
        }
        

        speech = new Speech(this);
        gestureRecognizer= new GestureDetector(this, new GestureRecognizer());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        return layoutView;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onWindowShown(){

        String orientation;
        if(layoutView.getOrientation() == Layout.VERTICAL){
            orientation = "vertical";
        }
        else
            orientation = "landscape";

        speech.speakText("Launching Braille Keyboard in " + orientation + " mode.");

        layoutView.setLabel("");
    }

    @Override
    public void onWindowHidden(){
        speech.speakText("Braille Keyboard hidden.");
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent keyEvent){

            if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
            {
                if(getCurrentInputConnection() != null && getCurrentInputConnection().getTextBeforeCursor(1,0).length()==0)
                    speech.speakText("Beginning of text");
                else {
                    speech.speakText(String.valueOf(getCurrentInputConnection().getTextBeforeCursor(1, 0)));
                    sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
                }

                return true;
            }
            else if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
                if(getCurrentInputConnection() != null && getCurrentInputConnection().getTextAfterCursor(1,0).length()==0)
                    speech.speakText("End of text");
                else{
                    sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
                    speech.speakText(String.valueOf(getCurrentInputConnection().getTextAfterCursor(1,0)));
                }
                return true;
            }
            else
                return super.onKeyDown(keyCode, keyEvent);
    }

    //public View.OnTouchListener
    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getActionMasked();

            int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex);

            gestureRecognizer.onTouchEvent(event);
            System.out.println("ACTION: " + actionToString(action));

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_HOVER_MOVE:
                    //track current pointers
                    for(int i = 0; i < event.getPointerCount(); i++){
                        System.out.println("moving pointer: " + event.getPointerId(i) + " count: " + event.getPointerCount());
                        TouchPointer touch = currentPointers.get(event.getPointerId(i));
                        touch.onTouchMove(new PointF(event.getX(i), event.getY(i)));
                    }

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_HOVER_ENTER:
                    tapRecognizer.onKeyDown(event);

                    //register touch pointers
                    TouchPointer touchPointer = currentPointers.get(pointerId);
                    if(touchPointer == null){
                        touchPointer = new TouchPointer();
                        currentPointers.put(pointerId, touchPointer);
                    }
                    touchPointer.onTouchDown(pointerId, new PointF(event.getX(pointerIndex), event.getY(pointerIndex)));

                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:

                    //remove all pointers
                    for(int i = 0; i < event.getPointerCount(); i++){
                        currentPointers.get(event.getPointerId(i)).onTouchUp();
                    }

                    tapRecognizer.onKeyUp(event);
                    lastGesture = GESTURE_TYPE.NONE;

                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    currentPointers.get(pointerId).onTouchUp();

            }
            layoutView.setCurrentTouchPoints(new ArrayList(currentPointers.values()));
            return true;

        }
    };

    @Override
    public void onFinishInputView(boolean finishingInput){
//        if(Setting.rotationLocked)
//            Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, rotationLock);
    }

    //Swipe Gestures
    class GestureRecognizer extends android.view.GestureDetector.SimpleOnGestureListener {
        InputConnection ic = getCurrentInputConnection();

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

            if(brailleModel.isPatternEmpty()) {
                if (motionEvent.getY() - motionEvent1.getY() > 100) {
                    return swipeUp();
                } else if (motionEvent1.getY() - motionEvent.getY() > 100) {
                    return swipeDown();
                } else if (motionEvent.getX() - motionEvent1.getX() > 100) {
                    return swipeLeft();
                } else if (motionEvent1.getX() - motionEvent.getX() > 100) {
                    return swipeRight();
                }
            }
            lastGesture = GESTURE_TYPE.NONE;
            return true;
        }


        boolean swipeUp(){
            lastGesture = GESTURE_TYPE.SWIPE_UP;
            return true;
        }

        boolean swipeDown(){
            lastGesture = GESTURE_TYPE.SWIPE_DOWN;

            speech.speakText("Enter.");

            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));

            return true;
        }

        boolean swipeLeft(){
            lastGesture = GESTURE_TYPE.SWIPE_LEFT;
            speech.speakText("Space.");

            ic.sendKeyEvent(new KeyEvent( KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
            ic.sendKeyEvent(new KeyEvent( KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));

            return true;
        }

        boolean swipeRight(){
            lastGesture = GESTURE_TYPE.SWIPE_RIGHT;

            speech.speakText("Backspace.");
            ic.deleteSurroundingText(1,0);

            return true;
        }
    }

    //Tap Gestures
    class TapRecognizer {
        //tap selected dots
        public void onKeyDown(MotionEvent event) {
            int index = event.getActionIndex();
            if (event.getPointerCount() >= 1) {
                    if (layoutView.fillDot(event.getX(index), event.getY(index))) {
                        for (int j = 0; j < brailleModel.getPattern().length; j++) {
                            if (brailleModel.getPattern()[j]==0){//check untapped dots only
                                brailleModel.setDot(layoutView.getDotLayout().get(j).isTapped() ? 1 : 0, j);

                                //vibrate once tapped
                                if (brailleModel.getPattern()[j]==1)
                                    vibrator.vibrate(300);
                            }
                        }
                    }
                }
        }

        //sends key event
        public void onKeyUp(MotionEvent event) {

            KeySet keySet = new KeySet();
            char c = keySet.getBrailleCharacter(CURRENT_KEYBOARD_MODE, brailleModel);
            if (c == '\u0000' && lastGesture.equals(GESTURE_TYPE.NONE) && !brailleModel.isPatternEmpty()) {
                speech.speakText("This is not a braille character");
                layoutView.setLabel("");
            } else if(c != '\u0000'){
                speech.speakText(String.valueOf(c));
                layoutView.setLabel(String.valueOf(c));
                InputConnection ic = getCurrentInputConnection();
                ic.commitText(String.valueOf(c), 1);
            }

            layoutView.resetLayout();
            brailleModel.setPattern(0, 0, 0, 0, 0, 0);
        }
    }

    public String actionToString(int action){
        switch(action){
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_HOVER_ENTER:
                return "ACTION_HOVER_ENTER";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_BUTTON_PRESS:
                return "ACTION_BUTTON_PRESS";
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return "ACTION_BUTTON_RELEASE";
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_HOVER_EXIT:
                return "ACTION_HOVER_EXIT";
            case MotionEvent.ACTION_HOVER_MOVE:
                return "ACTION_HOVER_MOVE";
            case MotionEvent.ACTION_MASK:
                return "ACTION_MASK";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_OUTSIDE:
                return "ACTION_OUTSIDE";
            case MotionEvent.ACTION_POINTER_2_DOWN:
                return "ACTION_POINTER_2_DOWN";
            case MotionEvent.ACTION_POINTER_2_UP:
                return "ACTION_POINTER_2_UP";
            case MotionEvent.ACTION_POINTER_3_DOWN:
                return "ACTION_POINTER_3_DOWN";
            case MotionEvent.ACTION_POINTER_3_UP:
                return "ACTION_POINTER_3_UP";
            case MotionEvent.ACTION_POINTER_DOWN:
                return "ACTION_POINTER_DOWN";
            case MotionEvent.ACTION_POINTER_ID_MASK:
                return "ACTION_POINTER_ID_MASK";
            case MotionEvent.ACTION_POINTER_ID_SHIFT:
                return "ACTION_POINTER_ID_SHIFT";
            case MotionEvent.ACTION_POINTER_UP:
                return "ACTION_POINTER_UP";
        }
        return "unknown action";
    }

}
