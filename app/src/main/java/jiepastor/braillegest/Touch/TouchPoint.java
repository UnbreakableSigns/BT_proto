package jiepastor.braillegest.Touch;

import android.graphics.PointF;

/**
 * A point (coordinate) when user touches the screen
 */

public class TouchPoint {
    private boolean isPointActive = false;
    private PointF point;
    private int id;

    public void onTouchDown(int id, PointF point){
        this.id = id;
        this.point = point;
        isPointActive = true;
    }

    public void onTouchMove(PointF point){
        this.point = point;
    }

    public boolean isPointActive(){
        return isPointActive;
    }

    public PointF getCoordinates(){
        return point;
    }

    public int getId(){
        return id;
    }
}
