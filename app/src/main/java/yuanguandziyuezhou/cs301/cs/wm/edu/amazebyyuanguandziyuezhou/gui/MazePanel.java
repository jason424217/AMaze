package yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.gui;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import yuanguandziyuezhou.cs301.cs.wm.edu.amazebyyuanguandziyuezhou.R;

import static android.content.ContentValues.TAG;

/**
 * Add functionality for double buffering to an AWT Panel class.
 * Used for drawing a maze.
 *
 * @author Ziyue Zhou/Yuan Gu
 *
 */
public class MazePanel extends View {
    private final static String TAG = "Debug";
    private Paint paint;
    private Canvas canvas;
    private Bitmap bitmap;

    /**
     * Constructor.
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public MazePanel(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor.
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Constructor.
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public MazePanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * initialization for different constructors
     *
     * @param attrs:attribute set
     * @param defStyle:def style
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    private void init(AttributeSet attrs, int defStyle) {
        setFocusable(false);
        paint = new Paint();
        bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH-100,Constants.VIEW_HEIGHT, Bitmap.Config.RGB_565);
        canvas = new Canvas(bitmap);
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
    }

    /**
     * Draw the view
     *
     * @param canvas1: used to draw
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    @Override
    protected void onDraw(Canvas canvas1) {
        Log.v("Project6","MazePanel: onDraw called.");
        canvas1.drawBitmap(bitmap, 0,0,paint);
    }


    /**
     * set the color of graphics of this panel
     * @param r,g,b is awt.Color's rgb input
     * @param bool indicate 2D or not
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void setcolor(int r, int g, int b, boolean bool){
        paint.setARGB(255, r, g, b);
    }
    /**
     * set the color of graphics of this view
     * @param r,g,b is awt.Color's rgb input
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void setcolor(int r, int g, int b){
        paint.setARGB(255, r, g, b);
    }
    /**
     * Draw line with graphics of this view
     * @param a, b, c, d are input to awt.drawLine method
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void drawline(int a, int b, int c, int d){
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(a, b, c, d, paint);
    }
    /**
     * Fill oval with graphics of this view
     * @param x,y,width,height are input to awt.fillOval method
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void filloval(int x, int y, int width, int height){
        paint.setStyle(Paint.Style.FILL);
        canvas.drawOval(x-width/2, y+height/2, x+height/2, y-height/2, paint);
    }
    /**
     * Fill polygon with graphics of this view
     * @param a, b, c are input to awt.fillPolygon method
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void fillpolygon(int a[], int b[], int c){
        paint.setStyle(Paint.Style.FILL);
        Path polypath = new Path();
        polypath.moveTo(a[0], b[0]);
        for(int i=0; i < c; i++){
            polypath.lineTo(a[i], b[i]);
        }
        polypath.lineTo(a[0], b[0]);
        canvas.drawPath(polypath, paint);
    }
    /**
     * Fill polygon with graphics of this view
     * @param a, b, c are input to fillPolygoe method
     * @param bool indicate 2D or not
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void fillpolygon(int a[], int b[], int c, boolean bool){
        this.fillpolygon(a,b,c);
    }
    /**
     * Fill Rectangle with graphics of this panel
     * @param x,y,width,height are input to awt.fillRect method
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void fillrect(int x, int y, int width, int height){
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y+height, x+width, y, paint);
    }
    /**
     * Get RGB value of color
     * @param color is array of r, g, b value
     * @return RGB value of color
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    static public int getRGB(int[] color){
        int rgb = Color.rgb(color[0], color[1], color[2]);
        return rgb;
    }
    /**
     * Get r, g, b value array of color with RBG value
     * @param rgb value of color
     * @return array of r, g, b value
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    static public int[] getColorCompenent(int rgb){
        int[] color = new int[3];
        color[0] = Color.red(rgb);
        color[1] = Color.green(rgb);
        color[2] = Color.blue(rgb);
        return color;
    }


    /**
     * To set the boundary
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    /*@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = Constants.VIEW_WIDTH;
        int desiredHeight = Constants.VIEW_HEIGHT;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }*/

    /**
     * Update panel
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void update(Canvas g) {
        Log.d(TAG, "update");
        paint(g);
    }

    /**
     * Update panel
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public void update(MazePanel mp) {
        Canvas g = mp.getCanvas();
        paint(g);
    }
    public void update() {
        paint(this.getCanvas());
    }
    /**
     * Draws the buffer image to the given graphics object.
     * This method is called when this panel should redraw itself.
     * The given graphics object is the one that actually shows
     * on the screen.
     */
    public void paint() {
        paint(this.getCanvas());
    }

    /**
     * Draws the buffer image to the given graphics object.
     * This method is called when this panel should redraw itself.
     * The given graphics object is the one that actually shows
     * on the screen.
     * @param g is graphics of panel's input
     */
    public void paint(Canvas g) {
        Log.v(TAG, "Paint CALLED");
        if (null == g) {
            Log.v(TAG, "No Graphics!");}
        else {
            this.invalidate();
        }
    }

    /*
     * Get bitmap
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public Bitmap getBitmap() {
        return bitmap;
    }

    /*
     * Get paint
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public Paint getPaint() {
        return paint;
    }

    /*
     * Get canvas
     *
     * @author Ziyue Zhou/Yuan Gu
     */
    public Canvas getCanvas() {
        return canvas;
    }
}
