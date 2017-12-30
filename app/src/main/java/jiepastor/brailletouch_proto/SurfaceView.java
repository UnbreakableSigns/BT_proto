package jiepastor.brailletouch_proto;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SurfaceView extends AppCompatActivity {
    Point[] dotCoordinates = new Point[6];
    List<Dot> dotLayout = new ArrayList<>();
    Vibrator vibrator = null;
    int[] selectedPattern = new int[6];
    String disp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        SetRefPoints();
        CreateLayout();

    }

    void SetRefPoints(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
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
            Dot d = new Dot(this, dotCoordinates[x].x, dotCoordinates[x].y, x + 1);

            dotLayout.add(d);
        }

        ConstraintLayout clayout = (ConstraintLayout) findViewById(R.id.clayout);
        for(Dot d : dotLayout) clayout.addView(d);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();
        int index = event.getActionIndex();

        int p_id = event.getPointerId(index);;

        switch(action){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_MOVE:

                for(int i=0;i<event.getPointerCount();i++){

                    PointF f = new PointF();
                    f.x = event.getX(index);
                    f.y = event.getY(index);

                    fillDot(f.x,f.y);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                disp = "[";
                for (int n = 0; n < 6; n++) {
                    disp += selectedPattern[n] + " ";
                    dotLayout.get(n).tapDot(false);
                    selectedPattern[n]=0;
                }
                disp += "]";

                System.out.println("disp " + disp);

                break;
        }
        return true;
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
}
