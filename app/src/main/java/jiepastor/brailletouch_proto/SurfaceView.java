package jiepastor.brailletouch_proto;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SurfaceView extends ConstraintLayout {
    Point[] dotCoordinates = new Point[6];
    List<Dot> dotLayout = new ArrayList<>();
    int[] selectedPattern = new int[6];
    String disp;
    KeySet keySet = new KeySet();
    android.view.GestureDetector gestureRecognizer;
    List<PointF> pointers = new ArrayList<>();
    Vibrator vibrator = null;
    TextToSpeech tts;
    Context context;

    public SurfaceView(Context context) {
        super(context);
        this.context = context;

        //text to speech
        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    tts.setPitch(1.1f);
                }
            }
        });

        //gesture detector
        gestureRecognizer= new GestureDetector(context, new GestureRecognizer());

        //vibrator
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        SetRefPoints();
        CreateLayout();

    }

    void SetRefPoints(){
        DisplayMetrics displayMetrics = new DisplayMetrics();

        int height = this.getHeight();
        int width = this.getWidth();
        System.out.println("height - " + height + "width - " + width);
        int radius = Dot.radius + 10;

        //right side
        dotCoordinates[0] = new Point((int)(width - radius), (int)(height / 6));
        dotCoordinates[1] = new Point((int)dotCoordinates[0].x, (int)(dotCoordinates[0].y + height / 3));
        dotCoordinates[2] = new Point((int)dotCoordinates[0].x, (int)(dotCoordinates[1].y + height / 3));

        //left side
        dotCoordinates[3] = new Point(radius, dotCoordinates[0].y);
        dotCoordinates[4] = new Point(radius, (int)(dotCoordinates[3].y + height / 3));
        dotCoordinates[5] = new Point(radius, (int)(dotCoordinates[4].y + height / 3));
    }

    void CreateLayout(){
        dotLayout.clear();

        for (int x =0;x<6;x++) {
            Dot d = new Dot(context, dotCoordinates[x].x, dotCoordinates[x].y, x + 1);

            dotLayout.add(d);
        }

        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.clayout);
        for(Dot d : dotLayout) clayout.addView(d);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();
        int index = event.getActionIndex();

        switch(action) {
            case MotionEvent.ACTION_MOVE:
                PointF p = new PointF();
                p.x = event.getX();
                p.y = event.getY();
                pointers.add(p);
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                for (int i = 0; i < event.getPointerCount(); i++) {

                    PointF f = new PointF();
                    f.x = event.getX(index);
                    f.y = event.getY(index);

                    fillDot(f.x, f.y);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                char c = keySet.getCharacter(KeySet.LOWER_CASE_MODE, selectedPattern);

                int sum=0;
                for (int i : selectedPattern) sum += i;
                if (sum != 0) {
                    disp = "[ ";

                    for (int n = 0; n < 6; n++) {
                        disp += selectedPattern[n] + " ";
                        dotLayout.get(n).tapDot(false);
                        selectedPattern[n] = 0;
                    }
                    disp += "]";

                    if (c == '\u0000') {
                        ((android.widget.TextView) findViewById(R.id.label)).setText("INVALID");
                        speakText("This is not a braille character");
                    } else {
                        ((android.widget.TextView) findViewById(R.id.label)).setText(c + ": " + disp);
                        speakText(String.valueOf(c));
                    }
                }
                break;

        }
        return gestureRecognizer.onTouchEvent(event);

    }

    void fillDot(float touchX, float touchY){
        //fill dot when selected

        for (int n=0;n<6;n++)
        {
            if (selectedPattern[n]== 0)
            {
                selectedPattern[n] = checkIfSelected(touchX,touchY,dotCoordinates[n].x,dotCoordinates[n].y,(float)Dot.radius) ? 1 : 0;

                if(selectedPattern[n] == 1){
                    dotLayout.get(n).tapDot(true);
                    vibrator.vibrate(100);
                }
            }

        }
    }

    boolean checkIfSelected(float x, float y, float h, float k, float r){
        return ((x-h)*(x-h) + (y-k)*(y-k)) <= r*r;
        /*
            x : x-coordinate touch point
            y : y-coordinate touch point
            h : x-coordinate reference point (dot)
            k : y-coordinate reference point (dot)
            r : radius of dot
        */
    }

    class GestureRecognizer extends android.view.GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            ((android.widget.TextView) findViewById(R.id.label)).setText("Double tap");
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            if(motionEvent.getX()<(dotCoordinates[0].x-dotCoordinates[3].x)&&motionEvent.getX()>(dotCoordinates[3].x+dotCoordinates[3].x)){
                if (motionEvent.getY() - motionEvent1.getY() > 50) {
                    //swipe up
//                    ((android.widget.TextView) findViewById(R.id.label)).setText("Swipe up");

                } else if (motionEvent1.getY() - motionEvent.getY() > 50) {
                    //swipe down
                    ((android.widget.TextView) findViewById(R.id.label)).setText("Swipe down");
                    speakText("New line");
                } else if (motionEvent.getX() - motionEvent1.getX() > 50) {
                    //swipe left
                    ((android.widget.TextView) findViewById(R.id.label)).setText("Swipe left");
                    speakText("Space");

                } else if (motionEvent1.getX() - motionEvent.getX() > 50) {
                    //swipe right
                    ((android.widget.TextView) findViewById(R.id.label)).setText("Swipe right");
                    speakText("Backspace");


                }
            }
            return true;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void speakText(String text){
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, hashCode() + "");
    }

}
