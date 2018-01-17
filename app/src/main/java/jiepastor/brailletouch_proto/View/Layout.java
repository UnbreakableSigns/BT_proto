package jiepastor.brailletouch_proto.View;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Layout of the view
 */

public class Layout extends ConstraintLayout {

    private Context context;

    private int height;
    private int width;

    private List<Dot> dotLayout = new ArrayList<>();
    private String label = "BrailleTouch";

    public Layout(Context context) {
        super(context);
        this.context = context;
        setBackgroundColor(Color.BLACK);

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

    public boolean fillDot(float touchX, float touchY){
        //fill dot when selected
        boolean isADotFilled = false;
        for (int n=0;n<6;n++)
        {
            if (checkIfSelected(touchX,touchY,dotLayout.get(n).getDotCoordinates().x,dotLayout.get(n).getDotCoordinates().y,(float)Dot.radius))
            {
                isADotFilled = true;
                dotLayout.get(n).tapDot(true);
                break;
            }
        }
        return isADotFilled;
    }

    public void resetLayout(){
        for(Dot dot : dotLayout) dot.tapDot(false);
    }

    private void setDefaultReferencePoints(){
        Point[] dotCoordinates = new Point[6];

        Dot.radius = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)? height/6 : height/10;
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

    public List<Dot> getDotLayout() {
        return dotLayout;
    }

    public List<PointF> current_pointers = new ArrayList<>();

    public void addCurrentPointer(PointF point){
        current_pointers.add(point);
        invalidate();
    }

    public void resetPointers(){
        current_pointers.clear();
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Paint paint = new Paint();
        for(PointF point : current_pointers){
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(point.x,point.y, 25, paint);
        }

        Paint paintLabel = new Paint();
        paintLabel.setTextSize(150);
        paintLabel.setColor(Color.WHITE);
        paintLabel.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(label,(canvas.getWidth() / 2),((canvas.getHeight() / 2) - ((paintLabel.descent() + paintLabel.ascent()) / 2)) ,paintLabel);
    }

    public void setLabel(String label) {
        this.label = label;
        invalidate();
    }
}
