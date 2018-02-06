package jiepastor.brailletouch_proto.Keyboard;

import android.graphics.PointF;

/**
 * A point when user touches the screen
 */

public class TouchPointer{
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

    public void onTouchUp(){
        isPointActive = false;
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
