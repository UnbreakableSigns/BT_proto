package jiepastor.braillegest.Keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import static android.content.Context.ACCESSIBILITY_SERVICE;

/**
 * Text to Speech class
 */

public class Speech {

    private TextToSpeech tts;
    private boolean isAccessibilityEnabled;
    private Context context;

    public Speech(Context context){

        this.context = context;

        tts=new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    //tts.setLanguage(Locale.US);
                    tts.setPitch(1.1f);
                }
            }
        });
    }

    private boolean isExporeByTouchEnabled(){
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
        return am.isTouchExplorationEnabled();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void speakText(String text){
        if (text.equals(null) || text.equals(""))
            return;

        if(!isExporeByTouchEnabled())
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");
        else
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void speakText(String text, int queueMode){
        if (text.equals(null) || text.equals(""))
            return;

        if(!isExporeByTouchEnabled())
            tts.speak(text, queueMode, null, hashCode() + "");
        else
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
