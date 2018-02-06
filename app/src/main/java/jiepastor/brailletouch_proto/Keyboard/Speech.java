package jiepastor.brailletouch_proto.Keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.util.Locale;

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

        AccessibilityManager am = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);

        isAccessibilityEnabled = am.isTouchExplorationEnabled();

        tts=new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.setPitch(1.1f);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void speakText(String text){
        if (text.equals(null) || text.equals(""))
            return;

        if(!isAccessibilityEnabled)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");
        else
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();

        System.out.println("Log: '" + text + "'");
    }
}
