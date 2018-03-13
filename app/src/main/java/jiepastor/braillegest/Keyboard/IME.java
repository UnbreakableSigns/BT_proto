package jiepastor.braillegest.Keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PointF;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jiepastor.braillegest.Braille.BraillePattern;
import jiepastor.braillegest.Braille.KeySet;
import jiepastor.braillegest.Interpreter.Interpreter;
import jiepastor.braillegest.Touch.TouchDetector;
import jiepastor.braillegest.View.Screen;

/**
 * Keyboard class, handles the user inputs
 */

public class IME extends InputMethodService {

    private Interpreter interpreterLeft;
    private Interpreter interpreterRight;

    private Screen screenView;
    private KeySet keySet = new KeySet();

    private Speech speech;
    private TouchDetector touchDetector;
    private Vibrator vibrator = null;

    private int CURRENT_KEYBOARD_MODE = KeySet.LOWER_CASE_MODE;

    private AccessibilityManager accessibilityManager;

    @Override
    public View onCreateInputView() {
        screenView = new Screen(this);
        screenView.setOnTouchListener(mTouchListener);

        interpreterLeft = new Interpreter();
        interpreterRight = new Interpreter();

        interpreterLeft.calibrateWithPoints(screenView.getLeftDotCoordinates());
        interpreterRight.calibrateWithPoints(screenView.getRightDotCoordinates());

        accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isTouchExplorationEnabled()) {

            screenView.setOnHoverListener(new View.OnHoverListener() {

                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    return mTouchListener.onTouch(view, motionEvent);
                }
            });
        }

        touchDetector = new TouchDetector(this, screenView, new TouchDetector.TouchListener() {
            @Override
            public void onDoubleTap() {
                readFullText();
            }

            @Override
            public void onLongPress() {
                deleteAllText();
            }

            @Override
            public void onSwipe(int swipeDirection) {
                switch (swipeDirection) {
                    case TouchDetector.SWIPE_DIR_UP:
                        switch(CURRENT_KEYBOARD_MODE){
                            case KeySet.LOWER_CASE_MODE:
                                CURRENT_KEYBOARD_MODE = KeySet.UPPER_CASE_MODE;
                                speech.speakText("Uppercase Alphabet mode");
                                break;
                            case KeySet.UPPER_CASE_MODE:
                                CURRENT_KEYBOARD_MODE = KeySet.NUMBER_MODE;
                                speech.speakText("Number mode");
                                break;
                            case KeySet.NUMBER_MODE:
                                CURRENT_KEYBOARD_MODE = KeySet.SYMBOL_MODE;
                                speech.speakText("Symbol mode");
                                break;
                            case KeySet.SYMBOL_MODE:
                                CURRENT_KEYBOARD_MODE = KeySet.LOWER_CASE_MODE;
                                speech.speakText("Lowercase Alphabet mode");
                                break;
                        }
                        break;
                    case TouchDetector.SWIPE_DIR_DOWN:
                        sendEnter();
                        break;
                    case TouchDetector.SWIPE_DIR_LEFT:
                        sendBackspace();
                        break;
                    case TouchDetector.SWIPE_DIR_RIGHT:
                        sendSpace();
                        break;
                }
            }

            @Override
            public void onMultiFingerSingleTap(Set<PointF> points) {
                List<PointF> leftSide = new ArrayList<>();
                List<PointF> rightSide = new ArrayList<>();

                for (PointF p : points) {
                    if (p.x >= (screenView.getScreenWidth() / 2))
                        leftSide.add(p);
                    else
                        rightSide.add(p);
                }

                if (points.size() <= 6 && (leftSide.size() <= 3 && rightSide.size() <= 3)) {
                    String _curString = screenView.DOT_ORIENTATION == Screen.BACK_FACE_ORIENTATION
                            ? (interpreterLeft.interpretShortPress(leftSide) + interpreterRight.interpretShortPress(rightSide))
                            : (interpreterLeft.interpretShortPress(rightSide) + interpreterRight.interpretShortPress(leftSide));
                    BraillePattern pattern = new BraillePattern();
                    if (pattern.setStringPattern(_curString))
                        sendKeyCharacter(pattern);
                }
            }

            @Override
            public void onMultiFingerSwipe(int swipeDirection) {
                switch(swipeDirection){
                    case TouchDetector.SWIPE_DIR_UP:
                        swapColumns();
                        break;
                    case TouchDetector.SWIPE_DIR_DOWN:
                        exitKeyboard();
                        break;
                    case TouchDetector.SWIPE_DIR_LEFT:
                        moveCursorLeft();
                        break;
                    case TouchDetector.SWIPE_DIR_RIGHT:
                        moveCursorRight();
                        break;
                }
            }
        });

        speech = new Speech(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        return screenView;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onWindowShown() {

        String orientation;
        if(accessibilityManager!=null) {
            if (accessibilityManager.isTouchExplorationEnabled())
                speech.speakText("Please pause TalkBack for the keyboard to have full functionality");
        }
        if (screenView.getOrientation() == Screen.VERTICAL) {
            orientation = "vertical";
        } else
            orientation = "landscape";

        speech.speakText("Launching Braille Keyboard in " + orientation + " mode.");

        screenView.setLabel("");

    }

    @Override
    public void onWindowHidden() {
        speech.speakText("Braille Keyboard hidden.");
    }

    public View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            vibrator.vibrate(50);
            screenView.setLabel("");
            touchDetector.onTouchEvent(event);
            return true;

        }
    };

    void sendKeyCharacter(BraillePattern key) {
        Character character = keySet.getBrailleCharacter(CURRENT_KEYBOARD_MODE, key);
        if (character != '\u0000') {
            InputConnection ic = getCurrentInputConnection();
            String textchar = String.valueOf(character);
            ic.commitText(textchar, 1);
            if(CURRENT_KEYBOARD_MODE==KeySet.UPPER_CASE_MODE)
                speech.speakText("Capital " + textchar);
            else
                speech.speakText(textchar);
            screenView.setLabel(textchar);
        } else {
            speech.speakText("Invalid key");
            screenView.setLabel("");
            vibrator.vibrate(500);
        }
    }

    void readFullText() {
        InputConnection ic = getCurrentInputConnection();
        ic.performContextMenuAction(android.R.id.selectAll);
        CharSequence selectedText = ic.getSelectedText(0);

        if(selectedText!=null){
            speech.speakText("Your text is: " + selectedText);
            for(int i =0;i<selectedText.length();i++)
                sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
        }
        else
            speech.speakText("No text entered");
    }

    void deleteAllText() {
        InputConnection ic = getCurrentInputConnection();
        ic.performContextMenuAction(android.R.id.selectAll);
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        speech.speakText("Cleared text");
    }

    void sendSpace() {
        InputConnection ic = getCurrentInputConnection();
        ic.commitText(" ", 1);
        speech.speakText("Space");
        screenView.setLabel("Space");

    }

    public static int SWIPE_DIR_LEFT = TouchDetector.SWIPE_DIR_LEFT;
    public static int SWIPE_DIR_RIGHT = TouchDetector.SWIPE_DIR_RIGHT;

    void swapColumns(){
        screenView.changeOrientation();
        int temp = TouchDetector.SWIPE_DIR_LEFT;
        SWIPE_DIR_LEFT = TouchDetector.SWIPE_DIR_RIGHT;
        SWIPE_DIR_RIGHT = temp;

        speech.speakText("Swapped column orientation");
    }

    void sendEnter() {
        InputConnection ic = getCurrentInputConnection();

        speech.speakText("Enter.");
        screenView.setLabel("Enter");

        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
    }

    void sendBackspace() {
        InputConnection ic = getCurrentInputConnection();

        String deleted = String.valueOf(ic.getTextBeforeCursor(1, 0));

        if (ic.deleteSurroundingText(1, 0) && deleted != null && deleted.length() > 0) {
            if (deleted.equals(" "))
                deleted = "space";

            speech.speakText("Deleted " + deleted);
            screenView.setLabel("del " + deleted);
        }

    }

    void moveCursorLeft(){
        if(getCurrentInputConnection() != null && getCurrentInputConnection().getTextBeforeCursor(1,0).length()==0)
            speech.speakText("Beginning of text");
        else {
            speech.speakText("left: " + String.valueOf(getCurrentInputConnection().getTextBeforeCursor(1, 0)));
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_LEFT);
        }
    }

    void moveCursorRight(){
        if(getCurrentInputConnection() != null && getCurrentInputConnection().getTextAfterCursor(1,0).length()==0)
            speech.speakText("End of text");
        else{
            speech.speakText("right: " + String.valueOf(getCurrentInputConnection().getTextAfterCursor(1,0)));
            sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_RIGHT);
        }
    }

    void exitKeyboard(){
        InputConnection ic = getCurrentInputConnection();
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));

        speech.speakText("Exiting");
    }
}