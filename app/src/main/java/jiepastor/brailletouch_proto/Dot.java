package jiepastor.brailletouch_proto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by jiepastor on 12/29/2017.
 */

public class Dot extends View {
    Context context;
    Paint paint = null;
    Paint paintNo = null;
    Canvas canvas;
    boolean tapped;

    public static int radius = 110;
    int dotNo;
    int x,y;

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

    public Dot(Context context, Point point, int dotNo) {
        super(context);
        try
        {
            this.x = point.x;
            this.y = point.y;
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

    public void tapDot(boolean t) {
        //set dot as selected
        tapped = t;
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

}
