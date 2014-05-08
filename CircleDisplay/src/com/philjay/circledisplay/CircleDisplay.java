
package com.philjay.circledisplay;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.DecimalFormat;

@SuppressLint("NewApi")
public class CircleDisplay extends View {

//    private static final String LOG_TAG = "PercentageView";

    /** startangle of the view */
    private float mStartAngle = 270f;

    /** angle that represents the displayed value */
    private float mAngle = 0f;

    /** current state of the animation */
    private float mPhase = 0f;

    /** the currently displayed value, can be percent or actual value */
    private float mValue = 0f;

    /** percent of the maximum width the arc takes */
    private float mValueWidthPercent = 50f;

    /** if true, percentage is drawn as text, else the actual value */
    private boolean mShowPercentage = true;

    /** if enabled, the inner circle is drawn */
    private boolean mDrawInner = true;

    /** if enabled, the center text is drawn */
    private boolean mDrawText = true;
    
    /** the decimalformat responsible for formatting the values in the view */
    private DecimalFormat mFormatValue = new DecimalFormat("###,###,###,##0.0");;

    /**
     * rect object that represents the bounds of the view, needed for drawing
     * the circle
     */
    private RectF mCircleBox = new RectF();

    private Paint mAnglePaint;
    private Paint mInnerCirclePaint;
    private Paint mTextPaint;

    /** object animator for doing the drawing animations */
    private ObjectAnimator mDrawAnimator;

    public CircleDisplay(Context context) {
        super(context);
        init();
    }

    public CircleDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mBoxSetup = false;

        mAnglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAnglePaint.setStyle(Style.FILL);
        mAnglePaint.setColor(Color.rgb(111, 219, 90));

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setStyle(Style.FILL);
        mInnerCirclePaint.setColor(Color.WHITE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Style.STROKE);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(Utils.convertDpToPixel(getResources(), 24f));

        mDrawAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 1.0f).setDuration(3000);
        mDrawAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    /** boolean flag that indicates if the box has been setup */
    private boolean mBoxSetup = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mBoxSetup) {
            mBoxSetup = true;
            setupBox();
        }

        // canvas.drawColor(Color.WHITE);

        drawWholeCircle(canvas);

        drawValue(canvas);

        if (mDrawInner)
            drawInnerCircle(canvas);

        if (mDrawText)
            drawText(canvas);
    }

    /**
     * draws the text in the center of the view
     * 
     * @param c
     */
    private void drawText(Canvas c) {
        if (mShowPercentage)
            c.drawText(mFormatValue.format(((mAngle * mPhase) / 360f * 100f)) + " %", getWidth() / 2, getHeight()
                    / 2 + mTextPaint.descent(), mTextPaint);
        else
            c.drawText(mFormatValue.format(mValue * mPhase), getWidth() / 2,
                    getHeight() / 2 + mTextPaint.descent(), mTextPaint);
    }

    /**
     * draws the background circle with less alpha
     * 
     * @param c
     */
    private void drawWholeCircle(Canvas c) {
        mAnglePaint.setAlpha(80);

        float r = getRadius();

        c.drawCircle(getWidth() / 2, getHeight() / 2, r, mAnglePaint);
    }

    /**
     * draws the inner circle of the view
     * 
     * @param c
     */
    private void drawInnerCircle(Canvas c) {

        c.drawCircle(getWidth() / 2, getHeight() / 2, getRadius() / 100f
                * (100f - mValueWidthPercent), mInnerCirclePaint);
    }

    /**
     * draws the actual value slice/arc
     * 
     * @param c
     */
    private void drawValue(Canvas c) {

        mAnglePaint.setAlpha(255);

        float angle = mAngle * mPhase;

        c.drawArc(mCircleBox, mStartAngle, angle, true, mAnglePaint);

        // Log.i(LOG_TAG, "CircleBox bounds: " + mCircleBox.toString() +
        // ", Angle: " + angle + ", StartAngle: " + mStartAngle);
    }

    /**
     * sets up the bounds of the view
     */
    private void setupBox() {

        int width = getWidth();
        int height = getHeight();

        float diameter = getDiameter();

        mCircleBox = new RectF(width / 2 - diameter / 2, height / 2 - diameter / 2, width / 2
                + diameter / 2, height / 2 + diameter / 2);
    }

    /**
     * shows the given value in the percentage view
     * 
     * @param toShow
     * @param total
     * @param animated
     */
    public void showValue(float toShow, float total, boolean animated) {

        mShowPercentage = false;

        mAngle = calcAngle(toShow / total * 100f);
        mValue = toShow;

        if (animated)
            startAnim();
        else {
            mPhase = 1f;
            invalidate();
        }
    }

    /**
     * shows the given percentage in the percentageview
     * 
     * @param percentage
     * @param animated
     */
    public void showPercentage(float percentage, boolean animated) {

        mShowPercentage = true;

        if (percentage > 100f)
            percentage = 100f;
        if (percentage < 0f)
            percentage = 0f;

        mAngle = calcAngle(percentage);
        mValue = percentage;

        if (animated)
            startAnim();
        else {
            mPhase = 1f;
            invalidate();
        }
    }

    /**
     * Returns the currently displayed value from the view. Depending on the
     * used method to show the value, this value can be percent or actual value.
     * 
     * @return
     */
    public float getValue() {
        return mValue;
    }

    public void startAnim() {
        mPhase = 0f;
        mDrawAnimator.start();
    }

    /**
     * set the duration of the drawing animation in milliseconds
     * 
     * @param durationmillis
     */
    public void setAnimDuration(int durationmillis) {
        mDrawAnimator.setDuration(durationmillis);
    }

    /**
     * returns the diameter of the drawn circle/arc
     * 
     * @return
     */
    public float getDiameter() {
        return Math.min(getWidth(), getHeight());
    }

    /**
     * returns the radius of the drawn circle
     * 
     * @return
     */
    public float getRadius() {
        return getDiameter() / 2f;
    }

    /**
     * calculates the needed angle for a given value
     * 
     * @param percent
     * @return
     */
    private float calcAngle(float percent) {
        return percent / 100f * 360f;
    }

    /**
     * set the starting angle for the view
     * 
     * @param angle
     */
    public void setStartAngle(float angle) {
        mStartAngle = angle;
    }

    /**
     * returns the current animation status of the view
     * 
     * @return
     */
    public float getPhase() {
        return mPhase;
    }

    /**
     * DONT USE THIS METHOD
     * 
     * @param phase
     */
    public void setPhase(float phase) {
        mPhase = phase;
        invalidate();
    }

    public void setDrawInnerCircle(boolean enabled) {
        mDrawInner = enabled;
    }

    public boolean isDrawInnerCircleEnabled() {
        return mDrawInner;
    }

    public void setDrawText(boolean enabled) {
        mDrawText = enabled;
    }

    public boolean isDrawTextEnabled() {
        return mDrawText;
    }

    /**
     * set the color of the arc
     * 
     * @param color
     */
    public void setColor(int color) {
        mAnglePaint.setColor(color);
    }

    /**
     * set the size of the center text in dp
     * 
     * @param size
     */
    public void setTextSize(float size) {
        mTextPaint.setTextSize(Utils.convertDpToPixel(getResources(), size));
    }

    /**
     * set the thickness of the value bar, default 50%
     * 
     * @param percentFromTotalWidth
     */
    public void setValueWidthPercent(float percentFromTotalWidth) {
        mValueWidthPercent = percentFromTotalWidth;
    }
    
    /**
     * sets the number of digits used to format values
     * @param digits
     */
    public void setFormatDigits(int digits) {
        
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormatValue = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    public static abstract class Utils {

        /**
         * This method converts dp unit to equivalent pixels, depending on
         * device density.
         * 
         * @param dp A value in dp (density independent pixels) unit. Which we
         *            need to convert into pixels
         * @return A float value to represent px equivalent to dp depending on
         *         device density
         */
        public static float convertDpToPixel(Resources r, float dp) {
            DisplayMetrics metrics = r.getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return px;
        }
    }
}
