package jiepastor.braillegest.Interpreter;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jiepastor on 3/6/2018.
 */

public class Layout {
    int length;
    List<Integer> layout;

    public Layout(int numFingers){
        length = numFingers;
        layout = new ArrayList<>();
        for(int i = 0; i < numFingers; ++i) {
            layout.add(0);
        }
    }

    public void setFingerDownAtIndex(int index){
        if (index >= 0 && index < length) { // Valid index
            layout.set(index, 1);
            //System.out.println("set finger down at " + index);
        }
        else{
            System.out.println("Index is out of bounds. Index: "+index+", Length: " + length);
        }
    }

    public boolean isFingerDownAtIndex(int index) {
        if (index >= 0 && index < length) {
            int cond = layout.get(index);
            return cond == 1;
        } else {
            System.out.println("Index is out of bounds. Index: "+ index + ", Length: " + length);
            return false;
        }
    }

    public double getErrorForTouchesWithCalibrationPoints(List<PointF> touches, List<PointF>calibrationPoints){
        int error = 0;
        PointF callibrationTouch;
        Iterator cpEnumerator = calibrationPoints.iterator();
        PointF inputTouch;
        Iterator itEnumerator = touches.iterator();

        for(int i = 0; i < length; ++i) {
            callibrationTouch = (PointF) cpEnumerator.next();
            if(isFingerDownAtIndex(i)) {
                inputTouch = (PointF) itEnumerator.next();
                double xError = callibrationTouch.x - inputTouch.x;
                double yError = callibrationTouch.y - inputTouch.y;
                error += xError * xError + yError * yError;
            }
        }

        return error;
    }

    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < length; ++i) {
            String touch = "0";
            if(isFingerDownAtIndex(i)) {
                touch = "1";
            }
            result += touch;
        }
        return result;
    }

}
