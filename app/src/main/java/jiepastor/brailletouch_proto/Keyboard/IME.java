package jiepastor.brailletouch_proto.Keyboard;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import jiepastor.brailletouch_proto.Braille.KeySet;
import jiepastor.brailletouch_proto.Keyboard.Speech;
import jiepastor.brailletouch_proto.R;
import jiepastor.brailletouch_proto.View.Layout;

public class IME extends InputMethodService{

    Layout layoutView;
    Speech speech;
    GestureDetector gestureRecognizer;
    int KEYBOARD_MODE = KeySet.LOWER_CASE_MODE;
    @Override
    public View onCreateInputView() {
        layoutView = new Layout(this);
        layoutView.setOnTouchListener(mTouchListener);


        speech = new Speech(this);
        gestureRecognizer= new GestureDetector(this, new GestureRecognizer());


        return layoutView;
    }

    @Override
    public void onWindowShown(){
        speech.speakText("Braille Keyboard has been launched.");
    }

    @Override
    public void onWindowHidden(){
        speech.speakText("Braille Keyboard is exiting.");
    }



    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getActionMasked();
            int index = event.getActionIndex();
            boolean gesture =  gestureRecognizer.onTouchEvent(event);

            switch (action) {
//                case MotionEvent.ACTION_MOVE:break;
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    System.out.println("down");
                    for (int i = 0; i < event.getPointerCount(); i++) {
                        layoutView.fillDot(event.getX(index), event.getY(index));
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    System.out.println("up");

                    KeySet keySet = new KeySet();
                    char c = keySet.getCharacter(KEYBOARD_MODE, layoutView.getPattern());

                    layoutView.resetLayout();

                    if (c == '\u0000' && !gesture) {
//                        ((android.widget.TextView) layoutView.findViewById(R.id.label)).setText("INVALID");
                        speech.speakText("This is not a braille character");
                    } else if (gesture){
//                        ((android.widget.TextView) .findViewById(R.id.label)).setText(c);
                        switch(lastGesture){
                            case SWIPE_UP:
                                break;
                            case SWIPE_DOWN:
                                speech.speakText("New line.");
                                break;
                            case SWIPE_LEFT:
                                speech.speakText("Space.");
                                break;
                            case SWIPE_RIGHT:
                                speech.speakText("Backspace.");
                                break;
                        }
                    } else{
                        speech.speakText(String.valueOf(c));
                    }

                    break;
            }
            return true;

        }
    };

    public enum GESTURE_TYPE{ SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT};
    GESTURE_TYPE lastGesture;

    class GestureRecognizer extends android.view.GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            //if(motionEvent.getX()<(dotCoordinates[0].x-dotCoordinates[3].x)&&motionEvent.getX()>(dotCoordinates[3].x+dotCoordinates[3].x)){
            if (motionEvent.getY() - motionEvent1.getY() > 50) {
                return swipeUp();
            } else if (motionEvent1.getY() - motionEvent.getY() > 50) {
                return swipeDown();
            } else if (motionEvent.getX() - motionEvent1.getX() > 50) {
                return swipeLeft();
            } else if (motionEvent1.getX() - motionEvent.getX() > 50) {
                return swipeRight();
            }
            //}
            return false;
        }


        boolean swipeUp(){
            //swipe up

            lastGesture = GESTURE_TYPE.SWIPE_UP;
            return true;
        }

        boolean swipeDown(){
//        ((android.widget.TextView) findViewById(R.id.label)).setText("Swipe down");

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
}
