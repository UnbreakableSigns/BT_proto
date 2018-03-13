package jiepastor.braillegest.Interpreter;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jiepastor on 3/6/2018.
 */

public class Interpreter {
    List<PointF> calibratedPoints = new ArrayList<>();
    boolean _inLandscape; // Whether the view is in landscape orientation or not
    HashMap<Integer,List<Layout>> _layouts = new HashMap<>(); // Possible layouts
    public static final int TOTAL_FINGERS = 3;

    public Interpreter(){
        for(int i = 0; i < TOTAL_FINGERS; i++) {
            List<Layout> layouts = generateLayoutsWithFingersTotalAndFingersDown(TOTAL_FINGERS, i);
            _layouts.put(i,layouts);
        }
    }

    private List<Layout> generateLayoutsWithFingersTotalAndFingersDown(int fingersTotal, int fingersDown){
        // Base cases
//        System.out.println("generateLayoutsWithFingersTotalAndFingersDown - " + fingersDown + "/" + fingersTotal );

        if (fingersDown == 0) {
            // Return an array with one element, where no fingers are down.
            Layout layout = new Layout(fingersTotal);
            return Arrays.asList(new Layout[]{layout});
        } else if(fingersTotal == 0) {
            // Return an empty array of layouts.
            return new ArrayList<>();
        }

        // Recursive cases
        List<Layout> set1Suffixes = generateLayoutsWithFingersTotalAndFingersDown(fingersTotal - 1,fingersDown);
        int set1Length = set1Suffixes.size();

        List<Layout> set2Suffixes = generateLayoutsWithFingersTotalAndFingersDown(fingersTotal - 1,fingersDown - 1);
        int set2Length = set2Suffixes.size();

        // Create a new NSMutableArray.
        List<Layout> layoutArray = new ArrayList<>(set1Length + set2Length);

        // Add the first set if syffuxes, where the first position of each _piLayout is NOT down.
        for(int i = 0; i < set1Length; ++i) {
            // Create a new _piLayout (no fingers are down by default).
            Layout _piLayout = new Layout(fingersTotal);

            // Set each _piLayout position
            for(int pos = 1; pos < fingersTotal; ++pos) {
                if(set1Suffixes.get(i).isFingerDownAtIndex(pos - 1)){
                    _piLayout.setFingerDownAtIndex(pos);
                }
            }

            // Add the _piLayout to the array of layouts
            layoutArray.add(_piLayout);
        }

        // Second set. the first position of each _piLayout is DOWN.
        for(int i = 0; i < set2Length; ++i) {
            // Create a new _piLayout and set the first finger down this time.
            Layout _piLayout = new Layout(fingersTotal);
            _piLayout.setFingerDownAtIndex(0);

            // Set each _piLayout position
            for(int pos = 1; pos < fingersTotal; ++pos) {
                if(set2Suffixes.get(i).isFingerDownAtIndex(pos - 1)){
                    _piLayout.setFingerDownAtIndex(pos);
                }
            }
            // Add the _piLayout to the array of layouts
            layoutArray.add(_piLayout);
        }

        return layoutArray;
    }


    public String interpretShortPress(List<PointF> touches){

        //List<PointF> sortedTouches = sortTouches(touches);
        Collections.sort(touches,comparator);
        int numFingers = touches.size();

        List<Layout> layouts = _layouts.get(numFingers);

        if(numFingers==TOTAL_FINGERS){
            String curString = "";
            for(int i=0;i<TOTAL_FINGERS;i++)
                curString += "1";

            return curString;
        }

        if(layouts == null)
            return "error";

        int count = layouts.size();

        double minError = Double.MAX_VALUE;
        Layout bestLayout = null;

        for (int i = 0; i < count; i++) {
            Layout currentLayout = layouts.get(i);
            double error = currentLayout.getErrorForTouchesWithCalibrationPoints(touches, calibratedPoints);

            if (error < minError) { // Update the best _piLayout
                minError = error;
                bestLayout = currentLayout;
            }
        }
        return bestLayout.toString();
    }

    public void calibrateWithPoints(List<PointF> touches){
        PointF ranges = getRange(touches);
        _inLandscape = ranges.y < ranges.x;
        System.out.println("Calibrating: " +  (_inLandscape ? "Landscape" : "Portrait"));
        calibratedPoints = touches;

    }

    private PointF getRange(List<PointF> touches) {
        float minY = Float.POSITIVE_INFINITY, minX = Float.POSITIVE_INFINITY, maxY = 0, maxX = 0;

        for (PointF touch : touches) {
            float x = touch.x;
            float y = touch.y;

            if(x < minX) { minX = x; }
            if(x > maxX) { maxX = x; }
            if(y < minY) { minY = y; }
            if(y > maxY) { maxY = y; }
        }
        return new PointF(maxX - minX, maxY - minY);
    }
    Comparator<PointF> comparator = new Comparator<PointF>() {
        @Override
        public int compare (PointF p1, PointF p2){
            if(_inLandscape)
                return Float.compare(p1.x, p2.x);
            else
                return Float.compare(p1.y, p2.y);
        }
    };

}