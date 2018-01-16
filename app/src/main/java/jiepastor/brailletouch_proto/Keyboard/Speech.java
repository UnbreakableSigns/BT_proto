package jiepastor.brailletouch_proto.Keyboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by jiepastor on 1/15/2018.
 */

public class Speech {

    TextToSpeech tts;

    public Speech(Context context){

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
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");
    }
}
