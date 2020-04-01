package com.example.drawsomething.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PaintView extends View {
    public static int BRUSH_SIZE = 10;
    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;

    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

    private ArrayList<Draw> paths = new ArrayList<>();
    private ArrayList<Draw> undo = new ArrayList<>();

    public PaintView(Context context) {

        super(context, null);

    }

    public PaintView(Context context, AttributeSet attrs) {

        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

    }

    public void initialise (DisplayMetrics displayMetrics) {

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;

    }

    @Override


    protected void onDraw(Canvas canvas) {

        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (Draw draw : paths) {

            mPaint.setColor(draw.color);
            mPaint.setStrokeWidth(draw.strokeWidth);
            mPaint.setMaskFilter(null);

            mCanvas.drawPath(draw.path, mPaint);

        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();

    }

    private void touchStart (float x, float y) {

        mPath = new Path();

        Draw draw = new Draw(currentColor, strokeWidth, mPath);
        paths.add(draw);

        mPath.reset();
        mPath.moveTo(x, y);

        mX = x;
        mY = y;

    }

    private void touchMove (float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

            mX = x;
            mY = y;

        }

    }

    private void touchUp () {

        mPath.lineTo(mX, mY);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;

        }

        return true;

    }


    //simpele clear dit zorgt ervoor dat de paint view leeg word gemaakt.
    public void clear () {

        backgroundColor = DEFAULT_BG_COLOR;
    // alle pats worden hier verwijdert.
        paths.clear();
        invalidate();

    }

    public void undo () {
    //Als de path (lijn) groter dan 0 is dan neemt het een stap terug bij de vorige release en begin
        if (paths.size() > 0) {

            undo.add(paths.remove(paths.size() - 1));
            invalidate();

        } else {

            Toast.makeText(getContext(), "Niks om ongedaan te maken", Toast.LENGTH_LONG).show();

        }

    }


    public void redo () {
        //dit checkt of de path al een keer een stap terug heeft genomen met redo
        if (undo.size() > 0) {
        //als dit zo is dan haalt het een undo weg. in het kort het meest recente ongedaan maken word ongedaan gemaakt
            paths.add(undo.remove(undo.size() - 1));
            invalidate();

        } else {
            //Als er geen undo's zijn aangeroepen dan geeft het een messageop het scherm
            Toast.makeText(getContext(), "Niks om opnieuw te doen", Toast.LENGTH_LONG).show();

        }

    }

    public void setStrokeWidth (int width) {

        strokeWidth = width;

    }

    public void setColor (int color) {

        currentColor = color;

    }

    public void saveImage () {

        int count = 0;

        File sdDirectory = Environment.getExternalStorageDirectory();
        File subDirectory = new File(sdDirectory.toString() + "/Pictures/Paint");

        if (subDirectory.exists()) {

            File[] existing = subDirectory.listFiles();

            for (File file : existing) {

                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {

                    count++;

                }

            }

        } else {

            subDirectory.mkdir();

        }
        //Dit checked of de file al bestaat en als dat zo is dan krijt die een nummer erbij
        if (subDirectory.exists()) {

            File image = new File(subDirectory, "/drawing_" + (count + 1) + ".png");
            FileOutputStream fileOutputStream;

            try {

                fileOutputStream = new FileOutputStream(image);

                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                fileOutputStream.flush();
                fileOutputStream.close();

                Toast.makeText(getContext(), "saved", Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {


            } catch (IOException e) {


            }

        }

    }

}