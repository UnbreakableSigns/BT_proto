package jiepastor.brailletouch_proto.Keyboard;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import jiepastor.brailletouch_proto.Braille.BraillePattern;
import jiepastor.brailletouch_proto.Braille.KeySet;
import jiepastor.brailletouch_proto.View.Layout;

/**
 * Keyboard class, handles the user inputs
 */

public class IME extends InputMethodService{

    private BraillePattern brailleModel = new BraillePattern();

    private Layout layoutView;

    private Speech speech;
    private GestureDetector gestureRecognizer;
    TapRecognizer tapRecognizer = new TapRecognizer();
    Vibrator vibrator = null;

    private int CURRENT_KEYBOARD_MODE = KeySet.LOWER_CASE_MODE;
    public enum GESTURE_TYPE{ SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT, NONE}
    private GESTURE_TYPE lastGesture = GESTURE_TYPE.NONE;

    @Override
    public View onCreateInputView() {
        layoutView = new Layout(this);
        layoutView.setOnTouchListener(mTouchListener);

        speech = new Speech(this);
        gestureRecognizer= new GestureDetector(this, new GestureRecognizer());
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        return layoutView;
    }

    @Override
    public void onWindowShown(){
        speech.speakText("Braille Keyboard has been launched.");
        layoutView.setLabel("");
    }

    @Override
    public void onWindowHidden(){
        speech.speakText("Braille Keyboard is exiting.");
    }

    //public View.OnTouchListener
    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getActionMasked();

            gestureRecognizer.onTouchEvent(event);

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    layoutView.resetPointers();
                    layoutView.addCurrentPointer(new PointF(event.getX(),event.getY()));

                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN:

                    tapRecognizer.onKeyDown(event);

                    layoutView.addCurrentPointer(new PointF(event.getX(event.getActionIndex()),event.getY(event.getActionIndex())));

                    if(!brailleModel.isPatternEmpty()){
                        lastGesture = GESTURE_TYPE.NONE;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    layoutView.resetPointers();
                    tapRecognizer.onKeyUp(event);
                    lastGesture = GESTURE_TYPE.NONE;
                    break;
            }
            return true;

        }
    };

    class GestureRecognizer extends android.view.GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            System.out.println("onDoubleTap: " + e.toString());
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            System.out.println("onFling: " + motionEvent.toString() + motionEvent1.toString());

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
            return true;
        }

        boolean swipeLeft(){
            lastGesture = GESTURE_TYPE.SWIPE_LEFT;
            return true;
        }

        boolean swipeRight(){
            lastGesture = GESTURE_TYPE.SWIPE_RIGHT;
            return true;
        }
    }

    class TapRecognizer {
        //fingers down
        public void onKeyDown(MotionEvent event) {
            int index = event.getActionIndex();


            if (event.getPointerCount() >= 1) {
                    if (layoutView.fillDot(event.getX(index), event.getY(index))) {
                        for (int j = 0; j < brailleModel.getPattern().length; j++) {
                            if (brailleModel.getPattern()[j]==0){//check untapped dots only
                                brailleModel.setDot(layoutView.getDotLayout().get(j).isTapped() ? 1 : 0, j);

                                //vibrate once tapped
                                if (brailleModel.getPattern()[j]==1)
                                    vibrator.vibrate(100);
                            }
                        }
                    }
                }

        }

        //fingers up
        public void onKeyUp(MotionEvent event) {
            KeySet keySet = new KeySet();
            char c = keySet.getBrailleCharacter(CURRENT_KEYBOARD_MODE, brailleModel);

            layoutView.resetLayout();
            brailleModel.setPattern(0, 0, 0, 0, 0, 0);

            if (c == '\u0000' && lastGesture.equals(GESTURE_TYPE.NONE)) {
                speech.speakText("This is not a braille character");
                layoutView.setLabel("");
            } else if (!lastGesture.equals(GESTURE_TYPE.NONE)) {
                InputConnection ic = getCurrentInputConnection();
                layoutView.setLabel("");
                switch (lastGesture) {
                    case SWIPE_UP:
                        break;
                    case SWIPE_DOWN:
                        speech.speakText("New line.");

                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                        break;
                    case SWIPE_LEFT:
                        speech.speakText("Space.");

                        ic.sendKeyEvent(new KeyEvent( KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE));
                        ic.sendKeyEvent(new KeyEvent( KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE));
                        break;
                    case SWIPE_RIGHT:
                        speech.speakText("Backspace.");
                        ic.deleteSurroundingText(1, 0);
                        break;
                }
            } else {
                speech.speakText(String.valueOf(c));
                layoutView.setLabel(String.valueOf(c));
                InputConnection ic = getCurrentInputConnection();
                ic.commitText(String.valueOf(c), 1);
            }

        }
    }
}
