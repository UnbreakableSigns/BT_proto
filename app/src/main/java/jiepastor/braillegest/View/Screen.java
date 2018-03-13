package jiepastor.braillegest.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jiepastor.braillegest.Touch.TouchPoint;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Layout of the view (display)
 */

public class Screen extends RelativeLayout {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Context context;

    private int height;
    private int width;
    private int orientation;

    private List<PointF> calibratedPoints = new ArrayList<>();

    private List<Dot> dotLayout = new ArrayList<>();
    private String label = "";

    private List<TouchPoint> listOfCurrentPoints = new ArrayList<>();

    public Screen(Context context) {
        super(context);
        this.context = context;
        setBackgroundColor(Color.BLACK);
        setAlpha((float) 0.5);

        //get screen width and height
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();

        //screen dimensions
        display.getSize(size);
        width=size.x;
        height=size.y;
        orientation = width > height ? HORIZONTAL : VERTICAL;

        setDefaultReferencePoints();
    }

    public int getOrientation(){
        return orientation;
    }

    public int DOT_ORIENTATION = BACK_FACE_ORIENTATION;
    public static final int BACK_FACE_ORIENTATION = 0;
    public static final int FRONT_FACE_ORIENTATION = 1;

    public void changeOrientation(){
        if(DOT_ORIENTATION == BACK_FACE_ORIENTATION)
            DOT_ORIENTATION = FRONT_FACE_ORIENTATION;
        else
            DOT_ORIENTATION = BACK_FACE_ORIENTATION;

        setDefaultReferencePoints();
    }

    private void setDefaultReferencePoints(){
        PointF[] rightCoordinates = new PointF[3];
        PointF[] leftCoordinates = new PointF[3];

//        Dot.radius = (orientation == HORIZONTAL) ? height/6 : height/10;
        Dot.radius = 50;
        int radius = Dot.radius + 10;

        //right side
        rightCoordinates[0] = new PointF((width - radius), (height / 6));
        rightCoordinates[1] = new PointF(rightCoordinates[0].x, (rightCoordinates[0].y + height / 3));
        rightCoordinates[2] = new PointF(rightCoordinates[0].x, (rightCoordinates[1].y + height / 3));

        //left side
        leftCoordinates[0] = new PointF(radius, rightCoordinates[0].y);
        leftCoordinates[1] = new PointF(radius, (leftCoordinates[0].y + height / 3));
        leftCoordinates[2] = new PointF(radius, (leftCoordinates[1].y + height / 3));

        switch(DOT_ORIENTATION){
            case FRONT_FACE_ORIENTATION:
                calibratedPoints.clear();
                calibratedPoints.addAll(Arrays.asList(leftCoordinates));
                calibratedPoints.addAll(Arrays.asList(rightCoordinates));
                break;
            case BACK_FACE_ORIENTATION:
            default:
                calibratedPoints.clear();
                calibratedPoints.addAll(Arrays.asList(rightCoordinates));
                calibratedPoints.addAll(Arrays.asList(leftCoordinates));
                break;
        }

        removeAllViewsInLayout();

        //add each dot to view
        for (int x =0;x<6;x++) {
            Dot dot = new Dot(context, calibratedPoints.get(x).x, calibratedPoints.get(x).y, x + 1);

            dotLayout.add(dot);
            addView(dot);
        }
    }

    public void setLabel(String label) {
        this.label = label;
        invalidate();
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

    public List<PointF> getLeftDotCoordinates() {
        List<PointF> left = new ArrayList<>();
        left.add(calibratedPoints.get(0));
        left.add(calibratedPoints.get(1));
        left.add(calibratedPoints.get(2));
        return left;
    }

    public List<PointF> getRightDotCoordinates() {
        List<PointF> right = new ArrayList<>();
        right.add(calibratedPoints.get(3));
        right.add(calibratedPoints.get(4));
        right.add(calibratedPoints.get(5));
        return right;
    }

    public void setCurrentTouchPoints(List<TouchPoint> p){
        listOfCurrentPoints = p;
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Paint paint = new Paint();
        for(TouchPoint point : listOfCurrentPoints){
            if(point.isPointActive()){
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(point.getCoordinates().x,point.getCoordinates().y, 50, paint);
            }
        }

        Paint paintLabel = new Paint();
        paintLabel.setTextSize(150);
        paintLabel.setColor(Color.WHITE);
        paintLabel.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(label,(canvas.getWidth() / 2),((canvas.getHeight() / 2) - ((paintLabel.descent() + paintLabel.ascent()) / 2)) ,paintLabel);
    }

    public int getScreenHeight() {
        return height;
    }

    public int getScreenWidth() {
        return width;
    }
}
