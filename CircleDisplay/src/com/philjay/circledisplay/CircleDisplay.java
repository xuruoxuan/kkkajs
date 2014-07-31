
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
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.DecimalFormat;

/**
 * Simple custom-view for displaying values (with and without animation) and
 * selecting values onTouch().
 * 
 * @author Philipp Jahoda
 */
@SuppressLint("NewApi")
public class CircleDisplay extends View implements OnGestureListener {

    private static final String LOG_TAG = "CircleDisplay";

    /** the unit that is represented by the circle-display */
    private String mUnit = "%";

    /** startangle of the view */
    private float mStartAngle = 270f;

    /**
     * field representing the minimum selectable value in the display - the
     * minimum interval
     */
    private float mStepSize = 1f;

    /** angle that represents the displayed value */
    private float mAngle = 0f;

    /** current state of the animation */
    private float mPhase = 0f;

    /** the currently displayed value, can be percent or actual value */
    private float mValue = 0f;

    /** the maximum displayable value, depends on the set value */
    private float mMaxValue = 0f;

    /** percent of the maximum width the arc takes */
    private float mValueWidthPercent = 50f;

    /** if enabled, the inner circle is drawn */
    private boolean mDrawInner = true;

    /** if enabled, the center text is drawn */
    private boolean mDrawText = true;

    /** if enabled, touching and therefore selecting values is enabled */
    private boolean mTouchEnabled = true;

    /** represents the alpha value used for the remainder bar */
    private int mDimAlpha = 80;

    /** the decimalformat responsible for formatting the values in the view */
    private DecimalFormat mFormatValue = new DecimalFormat("###,###,###,##0.0");

    /** array that contains values for the custom-text */
    private String[] mCustomText = null;

    /**
     * rect object that represents the bounds of the view, needed for drawing
     * the circle
     */
    private RectF mCircleBox = new RectF();

    private Paint mArcPaint;
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

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Style.FILL);
        mArcPaint.setColor(Color.rgb(192, 255, 140));

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

        mGestureDetector = new GestureDetector(getContext(), this);
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

        drawWholeCircle(canvas);

        drawValue(canvas);

        if (mDrawInner)
            drawInnerCircle(canvas);

        if (mDrawText) {
            
            if (mCustomText != null)
                drawCustomText(canvas);
            else
                drawText(canvas);
        }
    }

    /**
     * draws the text in the center of the view
     * 
     * @param c
     */
    private void drawText(Canvas c) {
        c.drawText(mFormatValue.format(mValue * mPhase) + " " + mUnit, getWidth() / 2,
                getHeight() / 2 + mTextPaint.descent(), mTextPaint);
    }

    /**
     * draws the custom text in the center of the view
     * 
     * @param c
     */
    private void drawCustomText(Canvas c) {
        
        int index = (int) ((mValue * mPhase) / mStepSize);
        
        if(index < mCustomText.length) {
            c.drawText(mCustomText[index], getWidth() / 2,
                    getHeight() / 2 + mTextPaint.descent(), mTextPaint);
        } else {
            Log.e(LOG_TAG, "Custom text array not long enough.");
        }        
    }

    /**
     * draws the background circle with less alpha
     * 
     * @param c
     */
    private void drawWholeCircle(Canvas c) {
        mArcPaint.setAlpha(mDimAlpha);

        float r = getRadius();

        c.drawCircle(getWidth() / 2, getHeight() / 2, r, mArcPaint);
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

        mArcPaint.setAlpha(255);

        float angle = mAngle * mPhase;

        c.drawArc(mCircleBox, mStartAngle, angle, true, mArcPaint);

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
     * shows the given value in the circle view
     * 
     * @param toShow
     * @param total
     * @param animated
     */
    public void showValue(float toShow, float total, boolean animated) {

        mAngle = calcAngle(toShow / total * 100f);
        mValue = toShow;
        mMaxValue = total;

        if (animated)
            startAnim();
        else {
            mPhase = 1f;
            invalidate();
        }
    }

    /**
     * Sets the unit that is displayed next to the value in the center of the
     * view. Default "%". Could be "€" or "$" or left blank or whatever it is
     * you display.
     * 
     * @param unit
     */
    public void setUnit(String unit) {
        mUnit = unit;
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

    /**
     * set this to true to draw the inner circle, default: true
     * 
     * @param enabled
     */
    public void setDrawInnerCircle(boolean enabled) {
        mDrawInner = enabled;
    }

    /**
     * returns true if drawing the inner circle is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawInnerCircleEnabled() {
        return mDrawInner;
    }

    /**
     * set the drawing of the center text to be enabled or not
     * 
     * @param enabled
     */
    public void setDrawText(boolean enabled) {
        mDrawText = enabled;
    }

    /**
     * returns true if drawing the text in the center is enabled
     * 
     * @return
     */
    public boolean isDrawTextEnabled() {
        return mDrawText;
    }

    /**
     * set the color of the arc
     * 
     * @param color
     */
    public void setColor(int color) {
        mArcPaint.setColor(color);
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
     * Set an array of custom texts to be drawn instead of the value in the
     * center of the CircleDisplay. If set to null, the custom text will be
     * reset and the value will be drawn. Make sure the length of the array corresponds with the maximum number of steps (set with setStepSize(float stepsize).
     * 
     * @param custom
     */
    public void setCustomText(String[] custom) {
        mCustomText = custom;
    }

    /**
     * sets the number of digits used to format values
     * 
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

    /**
     * set the aplha value to be used for the remainder of the arc, default 80
     * (use value between 0 and 255)
     * 
     * @param alpha
     */
    public void setDimAlpha(int alpha) {
        mDimAlpha = alpha;
    }

    /** paint used for drawing the text */
    public static final int PAINT_TEXT = 1;

    /** paint representing the value bar */
    public static final int PAINT_ARC = 2;

    /** paint representing the inner (by default white) area */
    public static final int PAINT_INNER = 3;

    /**
     * sets the given paint object to be used instead of the original/default
     * one
     * 
     * @param which, e.g. CircleDisplay.PAINT_TEXT to set a new text paint
     * @param p
     */
    public void setPaint(int which, Paint p) {

        switch (which) {
            case PAINT_ARC:
                mArcPaint = p;
                break;
            case PAINT_INNER:
                mInnerCirclePaint = p;
                break;
            case PAINT_TEXT:
                mTextPaint = p;
                break;
        }
    }

    /**
     * Sets the stepsize (minimum selection interval) of the circle display,
     * default 1f. It is recommended to make this value not higher than 1/5 of
     * the maximum selectable value, and not lower than 1/200 of the maximum
     * selectable value. For a maximum value of 100 for example, a stepsize
     * between 0.5 and 20 is recommended.
     * 
     * @param stepsize
     */
    public void setStepSize(float stepsize) {
        mStepSize = stepsize;
    }

    /**
     * returns the current stepsize of the display, default 1f
     * 
     * @return
     */
    public float getStepSize() {
        return mStepSize;
    }

    /**
     * returns the center point of the view in pixels
     * 
     * @return
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Enable touch gestures on the circle-display. If enabled, selecting values
     * onTouch() is possible. Set a SelectionListener to retrieve selected
     * values. Do not forget to set a value before selecting values. By default
     * the maxvalue is 0f and therefore nothing can be selected.
     * 
     * @param enabled
     */
    public void setTouchEnabled(boolean enabled) {
        mTouchEnabled = enabled;
    }

    /**
     * returns true if touch-gestures are enabled, false if not
     * 
     * @return
     */
    public boolean isTouchEnabled() {
        return mTouchEnabled;
    }

    /**
     * set a selection listener for the circle-display that is called whenever a
     * value is selected onTouch()
     * 
     * @param l
     */
    public void setSelectionListener(SelectionListener l) {
        mListener = l;
    }

    /** listener called when a value has been selected on touch */
    private SelectionListener mListener;

    /** gesturedetector for recognizing single-taps */
    private GestureDetector mGestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mTouchEnabled) {

            if (mListener == null)
                Log.w(LOG_TAG,
                        "No SelectionListener specified. Use setSelectionListener(...) to set a listener for callbacks when selecting values.");

            // if the detector recognized a gesture, consume it
            if (mGestureDetector.onTouchEvent(e))
                return true;

            float x = e.getX();
            float y = e.getY();

            // get the distance from the touch to the center of the view
            float distance = distanceToCenter(x, y);
            float r = getRadius();

            // touch gestures only work when touches are made exactly on the
            // bar/arc
            if (distance >= r - r * mValueWidthPercent / 100f && distance < r) {

                switch (e.getAction()) {

                // case MotionEvent.ACTION_DOWN:
                // if (mListener != null)
                // mListener.onSelectionStarted(mValue, mMaxValue);
                // break;
                    case MotionEvent.ACTION_MOVE:

                        updateValue(x, y);
                        invalidate();
                        if (mListener != null)
                            mListener.onSelectionUpdate(mValue, mMaxValue);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mListener != null)
                            mListener.onValueSelected(mValue, mMaxValue);
                        break;
                }
            }

            return true;
        }
        else
            return super.onTouchEvent(e);
    }

    /**
     * updates the display with the given touch position, takes stepsize into
     * consideration
     * 
     * @param x
     * @param y
     */
    private void updateValue(float x, float y) {

        // calculate the touch-angle
        float angle = getAngleForPoint(x, y);

        // calculate the new value depending on angle
        float newVal = mMaxValue * angle / 360f;

        // if no stepsize
        if (mStepSize == 0f) {
            mValue = newVal;
            mAngle = angle;
            return;
        }

        float remainder = newVal % mStepSize;

        // check if the new value is closer to the next, or the previous
        if (remainder <= mStepSize / 2f) {

            newVal = newVal - remainder;
        } else {
            newVal = newVal - remainder + mStepSize;
        }

        // set the new values
        mAngle = getAngleForValue(newVal);
        mValue = newVal;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        // get the distance from the touch to the center of the view
        float distance = distanceToCenter(e.getX(), e.getY());
        float r = getRadius();

        // touch gestures only work when touches are made exactly on the
        // bar/arc
        if (distance >= r - r * mValueWidthPercent / 100f && distance < r) {

            updateValue(e.getX(), e.getY());
            invalidate();

            if (mListener != null)
                mListener.onValueSelected(mValue, mMaxValue);
        }

        return true;
    }

    /**
     * returns the angle relative to the view center for the given point on the
     * chart in degrees. The angle is always between 0 and 360°, 0° is NORTH
     * 
     * @param x
     * @param y
     * @return
     */
    public float getAngleForPoint(float x, float y) {

        PointF c = getCenter();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > c.x)
            angle = 360f - angle;

        angle = angle + 180;

        // neutralize overflow
        if (angle > 360f)
            angle = angle - 360f;

        return angle;
    }

    /**
     * returns the angle representing the given value
     * 
     * @param value
     * @return
     */
    public float getAngleForValue(float value) {
        return value / mMaxValue * 360f;
    }

    /**
     * returns the value representing the given angle
     * 
     * @param angle
     * @return
     */
    public float getValueForAngle(float angle) {
        return angle / 360f * mMaxValue;
    }

    /**
     * returns the distance of a certain point on the view to the center of the
     * view
     * 
     * @param x
     * @param y
     * @return
     */
    public float distanceToCenter(float x, float y) {

        PointF c = getCenter();

        float dist = 0f;

        float xDist = 0f;
        float yDist = 0f;

        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }

        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }

        // pythagoras
        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        return dist;
    }

    /**
     * listener for callbacks when selecting values ontouch
     * 
     * @author Philipp Jahoda
     */
    public interface SelectionListener {

        /**
         * called everytime the user moves the finger on the circle-display
         * 
         * @param val
         * @param maxval
         */
        public void onSelectionUpdate(float val, float maxval);

        /**
         * called when the user releases his finger fromt he circle-display
         * 
         * @param val
         * @param maxval
         */
        public void onValueSelected(float val, float maxval);
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

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }
}
