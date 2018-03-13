package jiepastor.braillegest.Touch;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.util.ArraySet;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import jiepastor.braillegest.View.Screen;

/**
 * Detects gestures drawn from user input
 */

public class TouchDetector {

    //Listeners
    SingleGestureListener singleGestureListener;

    //Detectors
    private final GestureDetectorCompat singleGDetect;
    ScaleGestureDetectorCompat scaleGestureDetectorCompat;
    private final TouchListener touchListener;

    public static final int SWIPE_DIR_UP = 1;
    public static final int SWIPE_DIR_RIGHT = 2;
    public static final int SWIPE_DIR_DOWN = 3;
    public static final int SWIPE_DIR_LEFT = 4;

    private HashMap<Integer, TouchPoint> currentPoints = new HashMap<>();
    private HashMap<Integer, TouchPoint> firstTouchPoints = new HashMap<>();
    private HashMap<Integer, TouchPoint> lastTouchPoints = new HashMap<>();
    private Screen view;

    public TouchDetector(Context context, Screen view, TouchListener touchListener) {
        singleGestureListener = new SingleGestureListener();

        singleGDetect = new GestureDetectorCompat(context, singleGestureListener);
        this.touchListener = touchListener;
        this.view = view;
    }

    public interface TouchListener {
        void onDoubleTap();
        void onLongPress();
        void onSwipe(int swipeDirection);
        void onMultiFingerSingleTap(Set<PointF> points);
        void onMultiFingerSwipe(int swipeDirection);
    }


    class SingleGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onDown(MotionEvent e)
        {
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if(lastTouchPoints.size()==1){
                touchListener.onDoubleTap();
                isSingleGesturePerformed = true;
                return super.onDoubleTap(e);
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent startevent, MotionEvent finishevent, float velocityX,
                               float velocityY) {
            if(firstTouchPoints.size()>1)
                return false;

            final float deltaX = finishevent.getX() - startevent.getX();
            final float deltaY = finishevent.getY() - startevent.getY();

            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                if (Math.abs(deltaX) > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    if (deltaX > 0) {
                        touchListener.onSwipe(SWIPE_DIR_RIGHT);
                    } else {
                        touchListener.onSwipe(SWIPE_DIR_LEFT);
                    }
                }
            } else {
                if (Math.abs(deltaY) > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (deltaY > 0) {
                        touchListener.onSwipe(SWIPE_DIR_DOWN);
                    } else {
                        touchListener.onSwipe(SWIPE_DIR_UP);
                    }
                }
            }
            isSingleGesturePerformed = true;
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(firstTouchPoints.size()>1)
                return;


            touchListener.onLongPress();
            isSingleGesturePerformed = true;
            super.onLongPress(e);
        }

    }
    boolean isSingleGesturePerformed = false;
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int pointerCount = event.getPointerCount();

        singleGDetect.onTouchEvent(event);

        /*Paint Current TouchPoints*/
        switch (action) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_HOVER_MOVE:
                /*track current pointers*/
                for(int i = 0; i < pointerCount; i++){
                    TouchPoint touch = currentPoints.get(event.getPointerId(i));
                    touch.onTouchMove(new PointF(event.getX(i), event.getY(i)));
                }

                break;
            case MotionEvent.ACTION_DOWN:
                lastTouchPoints.clear();/*clear remaining touchpoints*/
                singleGDetect.onTouchEvent(event);

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_HOVER_ENTER:
                TouchPoint touchPoint = currentPoints.get(pointerId);
                if (touchPoint == null) {
                    touchPoint = new TouchPoint();
                    currentPoints.put(pointerId, touchPoint);

                    TouchPoint touchPoint1 = new TouchPoint();
                    firstTouchPoints.put(pointerId, touchPoint1);
                    touchPoint1.onTouchDown(pointerId, new PointF(event.getX(pointerIndex), event.getY(pointerIndex)));

                }
                touchPoint.onTouchDown(pointerId, new PointF(event.getX(pointerIndex), event.getY(pointerIndex)));
                break;
            case MotionEvent.ACTION_HOVER_EXIT:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                singleGDetect.onTouchEvent(event);
                touchPoint = currentPoints.get(pointerId);
                lastTouchPoints.put(pointerId, touchPoint);

                if(lastTouchPoints.size() == firstTouchPoints.size()) {
                    float error = 0f;
                    PointF start = new PointF(), stop = new PointF();

                    for(Integer i : firstTouchPoints.keySet()){
                        PointF first = firstTouchPoints.get(i).getCoordinates();
                        PointF last = lastTouchPoints.get(i).getCoordinates();
                        error += getDistanceError(first, last);

                        start.x += first.x; start.y += first.y;
                        stop.x += last.x; stop.y += last.y;
                    }

                    start.x /= firstTouchPoints.size();
                    start.y /= firstTouchPoints.size();
                    stop.x /= firstTouchPoints.size();
                    stop.y /= firstTouchPoints.size();

                    if(error<(firstTouchPoints.size()*100)){
                        //TAP
                        if(!isSingleGesturePerformed){
                            Set<PointF> pointFList = new ArraySet<>();
                            for(TouchPoint tp : firstTouchPoints.values()){
                                pointFList.add(tp.getCoordinates());
                            }
                            touchListener.onMultiFingerSingleTap(pointFList);
                        }
                    }
                    else {
                        //SWIPE
                        if(firstTouchPoints.size()==2)
                            onMultiFingerSwipe(start,stop);
                    }
                }
                currentPoints.clear();
                firstTouchPoints.clear();
                isSingleGesturePerformed = false;

                break;
            case MotionEvent.ACTION_POINTER_UP:
                //remove current pointer
                touchPoint = currentPoints.get(pointerId);
                lastTouchPoints.put(pointerId, touchPoint);


                currentPoints.remove(pointerId);
                break;
        }
        view.setCurrentTouchPoints(new ArrayList(currentPoints.values()));
        /**/

        return true;
    }

    //TODO pinch
    class mScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            System.out.println("scale: " + detector.getScaleFactor());

            return true;
            //return super.onScale(detector);
        }
    }

    public void onMultiFingerSwipe(PointF point1, PointF point2) {
        if (Math.abs(point1.x - point2.x) > SingleGestureListener.SWIPE_MIN_DISTANCE) {
            if (point1.x > point2.x) {
                touchListener.onMultiFingerSwipe(SWIPE_DIR_LEFT);// Swipe left.
            } else {
                touchListener.onMultiFingerSwipe(SWIPE_DIR_RIGHT);// Swipe right.
            }
        }
        else if (Math.abs(point1.y - point2.y) > SingleGestureListener.SWIPE_MIN_DISTANCE) {
            if (point1.y > point2.y) {
                touchListener.onMultiFingerSwipe(SWIPE_DIR_UP);// Swipe up.
            } else {
                touchListener.onMultiFingerSwipe(SWIPE_DIR_DOWN);// Swipe down.
            }
        }
    }

    private float getDistanceError(PointF point, PointF reference){
        if(reference ==null || point == null)
            return Float.MAX_VALUE;
        return (float) Math.sqrt(Math.pow((point.x-reference.x),2)+Math.pow((point.y-reference.y),2));
    }
}