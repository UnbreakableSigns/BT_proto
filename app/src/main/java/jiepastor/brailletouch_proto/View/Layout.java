package jiepastor.brailletouch_proto.View;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jiepastor.brailletouch_proto.Braille.BraillePattern;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Layout of the view
 */

public class Layout extends ConstraintLayout {

    private Context context;

    private int height;
    private int width;

    private List<Dot> dotLayout = new ArrayList<>();
    private BraillePattern pattern = new BraillePattern();
    private TextView label;

    public Layout(Context context) {
        super(context);
        this.context = context;
        setBackgroundColor(Color.WHITE);

        //add textview to layout
        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(this);
        label = new TextView(context);
        label.setId(View.generateViewId());
        label.setText("BrailleTouch");
        constraints.center(label.getId(), ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                0, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0, 0.5f);
        constraints.applyTo(this);

        //get screen width and height
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();

        //screen dimensions
        display.getSize(size);
        width=size.x;
        height=size.y;

        setDefaultReferencePoints();
    }

    public BraillePattern getPattern() {return pattern;}

    public boolean fillDot(float touchX, float touchY){
        //fill dot when selected
        boolean isADotFilled = false;
        int[] selectedPattern = new int[]{pattern.getDot1(),pattern.getDot2(),pattern.getDot3(),pattern.getDot4(),pattern.getDot5(),pattern.getDot6()};

        for (int n=0;n<6;n++)
        {
            if (selectedPattern[n]== 0)
            {
                selectedPattern[n] = checkIfSelected(touchX,touchY,dotLayout.get(n).getDotCoordinates().x,dotLayout.get(n).getDotCoordinates().y,(float)Dot.radius) ? 1 : 0;

                if(selectedPattern[n] == 1){
                    dotLayout.get(n).tapDot(true);
                    isADotFilled = true;
                }
            }
        }
        pattern.setPattern(selectedPattern[0],selectedPattern[1],selectedPattern[2],selectedPattern[3],selectedPattern[4],selectedPattern[5]);
        return isADotFilled;
    }

    public void resetLayout(){
        pattern = new BraillePattern();
        for(Dot dot : dotLayout) dot.tapDot(false);
    }

    private void setDefaultReferencePoints(){
        Point[] dotCoordinates = new Point[6];

        Dot.radius = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)? height/6 : height/8;
        int radius = Dot.radius + 10;

        //right side
        dotCoordinates[0] = new Point((width - radius), (height / 6));
        dotCoordinates[1] = new Point(dotCoordinates[0].x, (dotCoordinates[0].y + height / 3));
        dotCoordinates[2] = new Point(dotCoordinates[0].x, (dotCoordinates[1].y + height / 3));

        //left side
        dotCoordinates[3] = new Point(radius, dotCoordinates[0].y);
        dotCoordinates[4] = new Point(radius, (dotCoordinates[3].y + height / 3));
        dotCoordinates[5] = new Point(radius, (dotCoordinates[4].y + height / 3));

        removeAllViewsInLayout();

        //add each dot to view
        for (int x =0;x<6;x++) {
            Dot dot = new Dot(context, dotCoordinates[x].x, dotCoordinates[x].y, x + 1);

            dotLayout.add(dot);
            addView(dot);
        }
    }

    private boolean checkIfSelected(float x, float y, float h, float k, float r){
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
