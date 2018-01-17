package jiepastor.brailletouch_proto.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Paints the dot on the view
 */

public class Dot extends View {
    private Context context;
    private Paint paint = null;
    private Paint paintNo = null;
    private Canvas canvas;
    private boolean tapped;

    public static int radius = 110;
    private int dotNo;
    private int x,y;

    public Dot(Context context, int x, int y, int dotNo) {
        super(context);
        try
        {
            this.x = x;
            this.y = y;
            this.context = context;
            this.paint = new Paint();
            this.paintNo = new Paint();
            this.dotNo = dotNo;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Point getDotCoordinates(){return new Point(x,y);}

    public void tapDot(boolean t) {
        //set dot as selected
        tapped = t;
        invalidate();
    }

    public void repositionDot(Point point){
        x = point.x;
        y = point.y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        int dotColor = tapped ? Color.DKGRAY : Color.BLUE;
        paint.setColor(dotColor);

        paintNo.setColor(Color.WHITE);
        paintNo.setTextSize(radius);
        paintNo.setTextAlign(Paint.Align.CENTER);

        canvas.drawCircle(x,y,  radius, paint);
        canvas.drawText(dotNo+"",(float)x,(float) y + (radius/4),paintNo);
    }

    public boolean isTapped() {
        return tapped;
    }
}
