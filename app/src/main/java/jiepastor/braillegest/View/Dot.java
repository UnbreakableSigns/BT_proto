package jiepastor.braillegest.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

/**
 * Paints the dot on the view
 */

public class Dot extends View {
    private Context context;
    private Canvas canvas;

    private Paint paint = null;
    private Paint paintNo = null;
    private boolean tapped;

    public static int radius = 20;
    private int dotNo;
    private float x,y;

    public Dot(Context context, float x, float y, int dotNo) {
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

    public PointF getDotCoordinates(){return new PointF(x,y);}

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
        canvas.drawText(dotNo+"",x,y + (radius/4),paintNo);
    }
}
