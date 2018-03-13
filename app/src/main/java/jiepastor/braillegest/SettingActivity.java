package jiepastor.braillegest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * default activity
 */

public class SettingActivity extends Activity {

    String steps[]= new String[]{
            "1. Enable keyboard and turn on BrailleGest.\n2. Change keyboard and set BrailleGest as the default keyboard.",
            "3. Make sure to pause Talkback before typing to enable full functionality. To pause, hold and long press both volume keys when Talkback is on before typing.",
            "4. To enter a character, simply tap the corresponding braille key on the screen.",
            "5. The following gestures are added to further maximize user's usability:\n\n" +
            "\tDouble Tap: Read entire typed text\n" +
                    "\tLong Press: Clear entire text field",
            "\tSwipe Right: Input 'Space' character\n" +
                    "\tSwipe Left: Backspace/Delete last typed character\n" +
                    "\tSwipe Up: Switch mode between letters, numbers, and symbols\n" +
                    "\tSwipe Down: Input 'Enter' key",
            "\tTwo-Finger Swipe Up: Swap columns\n\t(default position is 2nd column on the left side and 1st column on the right side)\n" +
                    "\tTwo-Finger Swipe Left/Right: Move cursor between typed characters\n" +
                    "\tTwo-Finger Swipe Down: Go back/Exit keyboard",
            "\nThank you for using BrailleGest!\n"
    };
    int currentStep = 0;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        builder = new AlertDialog.Builder(SettingActivity.this);
        setContentView(R.layout.activity_setting);
    }

    public void selectDefaultKeyboardButtonClick(View view){
        InputMethodManager mgr =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null) {
            mgr.showInputMethodPicker();
        }
    }

    public void onHowToUseButtonClick(View v){
        currentStep = 0;
        buildAlertDialog(steps[currentStep]);
    }

    void buildAlertDialog(String message){
        builder.setTitle("How to use BrailleGest")
                .setMessage(message)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ++currentStep;
                        if(currentStep<steps.length){
                            buildAlertDialog(steps[currentStep]);
                        }
                        else
                        {
                            currentStep=0;
                            dialogInterface.cancel();
                        }
                    }
                })
                .show();
    }

    public void enableKeyboardButtonClick(View v) {
        try {
            Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
