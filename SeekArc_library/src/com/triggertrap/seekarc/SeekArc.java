/*

  ***************************************************************************
 The MIT License (MIT)

 Copyright (c) 2013 Triggertrap Ltd
 Author Neil Davies

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal in
 the Software without restriction, including without limitation the rights to
 use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 the Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.triggertrap.seekarc;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * SeekArc.java
 * <p>
 * This is a class that functions much like a SeekBar but
 * follows a circle path instead of a straight line.
 *
 * @author Neil Davies
 */
public class SeekArc extends View {

    private static final String TAG = SeekArc.class.getSimpleName();
    private static int INVALID_PROGRESS_VALUE = -1;

    /**
     * The Drawable for the seek arc thumbnail
     */
    private Drawable mThumb;

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private int mMax = 100;

    /**
     * The Current value that the SeekArc is set to
     */
    private int mProgress = 0;

    /**
     * The width of the progress line for this SeekArc
     */
    private int mProgressWidth = 6;

    /**
     * The Width of the background arc for the SeekArc
     */
    private int mArcWidth = 11;

    /**
     * The Angle to start drawing this Arc from
     */
    private int mStartAngle = 0;

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    private int mSweepAngle = 360;

    /**
     * The rotation of the SeekArc- 0 is twelve o'clock
     */
    private int mRotation = 0;

    /**
     * Give the SeekArc rounded edges
     */
    private boolean mRoundedEdges = true;

    /**
     * Enable touch inside the SeekArc
     */
    private boolean mTouchInside = true;

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private boolean mClockwise = true;


    /**
     * is the control enabled/touchable
     */
    private boolean mEnabled = true;

    // Internal variables
    private int mArcRadius = 0;
    private float mProgressSweep = 0;
    private float mProgressSweep2 = 0;
    private RectF mArcRect = new RectF();
    private Paint mArcPaint;

    Paint[] progressPaint = new Paint[4];

    /**
     * Here we will provide ranges values for gauge meter.
     */
    private float[] seekBarRangesAr = new float[]{0, 68.25f, 136.5f, 204.75f};

    /**
     * Here we will provide the colors according to gauge meter range.
     */
    private int[] rangesColorAr = new int[]{getResources().getColor(R.color.dot_color_red),
            getResources().getColor(R.color.dot_color_blue),
            getResources().getColor(R.color.dot_color_green),
            getResources().getColor(R.color.dot_color_orange)};

    /**
     * Here we will provide drawable icon for particular range.
     */
    private Drawable[] rangesDrawableAr = new Drawable[]{getResources().getDrawable(R.drawable.red_dot),
            getResources().getDrawable(R.drawable.blue_dot),
            getResources().getDrawable(R.drawable.green_dot),
            getResources().getDrawable(R.drawable.orange_dot)};
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private int mThumbX1Pos;
    private int mThumbY1Pos;

    private float mTouchIgnoreRadius;
    private OnSeekArcChangeListener mOnSeekArcChangeListener;

    public void setRangesAr(int rangesCount) {
        this.seekBarRangesAr = new float[rangesCount];
        seekBarRangesAr[0] = 1;
        // TODO Mange other values according to this total range.
        int TOTAL_RANGE = 273;
        int threshold = TOTAL_RANGE / rangesCount;
        for (int i = 1; i < rangesCount; i++) {
            seekBarRangesAr[i] = i * threshold;
        }
    }

    public void setRangesColorAr(int[] rangesColorAr) {
        this.rangesColorAr = rangesColorAr;
        progressPaint[0] = setupPaint(0);
        progressPaint[1] = setupPaint(1);
        progressPaint[2] = setupPaint(2);
        progressPaint[3] = setupPaint(3);
    }

    public void setRangesDrawableAr(Drawable[] rangesDrawableAr) {
        this.rangesDrawableAr = rangesDrawableAr;
    }

    public interface OnSeekArcChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param seekArc  The SeekArc whose progress has changed
         * @param progress The current progress level. This will be in the range
         *                 0..max where max was set by
         *                 max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStartTrackingTouch(SeekArc seekArc);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the seekarc.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStopTrackingTouch(SeekArc seekArc);
    }

    public SeekArc(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SeekArc(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.seekArcStyle);
    }

    public SeekArc(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        Log.d(TAG, "Initialising SeekArc");
        final Resources res = getResources();
        float density = context.getResources().getDisplayMetrics().density;

        // Defaults, may need to link this into theme settings
        int arcColor = res.getColor(R.color.progress_gray);
        // Convert progress width to pixels for current density
        mProgressWidth = (int) (mProgressWidth * density);


        if (attrs != null) {
            // Attribute initialization
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.SeekArc, defStyle, 0);
            mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
            mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
            mProgressWidth = (int) a.getDimension(
                    R.styleable.SeekArc_progressWidth, mProgressWidth);
            mArcWidth = (int) a.getDimension(R.styleable.SeekArc_arcWidth,
                    mArcWidth);

            mStartAngle = a.getInt(R.styleable.SeekArc_startAngle, mStartAngle);
            mSweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, getSweepAngle());
            mRotation = a.getInt(R.styleable.SeekArc_rotation, getArcRotation());
            mRoundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges,
                    mRoundedEdges);
            mTouchInside = a.getBoolean(R.styleable.SeekArc_touchInside,
                    mTouchInside);
            mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise,
                    mClockwise);
            mEnabled = a.getBoolean(R.styleable.SeekArc_enabled, mEnabled);

            arcColor = a.getColor(R.styleable.SeekArc_arcColor, arcColor);
            a.recycle();
        }

        mProgress = (mProgress > mMax) ? mMax : mProgress;
        mProgress = (mProgress < 0) ? 0 : mProgress;

        mSweepAngle = (getSweepAngle() > 360) ? 360 : getSweepAngle();
        mSweepAngle = (getSweepAngle() < 0) ? 0 : getSweepAngle();

        mProgressSweep = (float) mProgress / mMax * getSweepAngle();

        mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
        mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        //mArcPaint.setAlpha(45);

        progressPaint[0] = setupPaint(0);
        progressPaint[1] = setupPaint(1);
        progressPaint[2] = setupPaint(2);
        progressPaint[3] = setupPaint(3);

        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        setProgressWidth(mProgressWidth);
        setArcWidth(mArcWidth);
    }

    private Paint setupPaint(int i) {
        Paint paint = new Paint();
        paint.setColor(rangesColorAr[i]);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mProgressWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private int pointerThreshold = 0;

    public void resetPointerThreshold() {
        this.pointerThreshold = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mClockwise) {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }
        drawFourArc(canvas, pointerThreshold);
        pointerThreshold += 8;
        if (moveMarker || moveMarker2) {
            divideArc(canvas);
        }

        if (moveMarker) {
            // Draw the thumb nail
            setupThumb();
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
        }

        if (moveMarker2) {
            setupThumb2();
            canvas.translate(mTranslateX - mThumbX1Pos, mTranslateY - mThumbY1Pos);
            mThumb1.draw(canvas);

            setupThumb();
            canvas.translate(mThumbX1Pos - mThumbXPos, mThumbY1Pos - mThumbYPos);
            mThumb.draw(canvas);
        }
    }

    private void drawFourArc(Canvas canvas, int pointerThreshold) {
        for (int i = 0; i < seekBarRangesAr.length - 1; i++) {
            if (pointerThreshold < seekBarRangesAr[i + 1] - seekBarRangesAr[i]) {
                canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[i]), pointerThreshold, false, progressPaint[i]);
                canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[i]) + pointerThreshold, seekBarRangesAr[i + 1] - seekBarRangesAr[i] - pointerThreshold, false, mArcPaint);
            } else {
                canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[i]), seekBarRangesAr[i + 1] - seekBarRangesAr[i], false, progressPaint[i]);
            }
        }
        if (pointerThreshold < 273 - seekBarRangesAr[seekBarRangesAr.length - 1]) {
            canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[seekBarRangesAr.length - 1]), pointerThreshold, false, progressPaint[seekBarRangesAr.length - 1]);
            canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[seekBarRangesAr.length - 1]) + pointerThreshold, 273 - seekBarRangesAr[seekBarRangesAr.length - 1] - pointerThreshold, false, mArcPaint);
        } else {
            canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[seekBarRangesAr.length - 1]), 273 - seekBarRangesAr[seekBarRangesAr.length - 1], false, progressPaint[seekBarRangesAr.length - 1]);
        }
    }

    private void divideArc(Canvas canvas) {
//        setupThumb();
        for (int i = 0; i < seekBarRangesAr.length; i++) {
            if (i < seekBarRangesAr.length - 1) {
                canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[i]), withThreshold(seekBarRangesAr[i + 1]) - withThreshold(seekBarRangesAr[i]), false,
                        progressPaint[i]);
            } else {
                canvas.drawArc(mArcRect, withThreshold(seekBarRangesAr[i]), withThreshold(273) - withThreshold(seekBarRangesAr[i]), false,
                        progressPaint[i]);
            }

        }
    }

    private void setupThumb() {
        for (int i = 0; i < seekBarRangesAr.length; i++) {
            if (i < (seekBarRangesAr.length - 1)) {
                if (mProgressSweep <= seekBarRangesAr[i + 1]) {
                    mThumb = rangesDrawableAr[i];
                    break;
                }
            } else {
                mThumb = rangesDrawableAr[i];
                break;
            }

        }
        int thumbHalfheight = mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
        mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
                thumbHalfheight);
    }

    // TODO: 19/3/19 added second thumb
    private Drawable mThumb1;

    private void setupThumb2() {
        for (int i = 0; i < seekBarRangesAr.length; i++) {
            if (i < (seekBarRangesAr.length - 1)) {
                if (mProgressSweep <= seekBarRangesAr[i + 1]) {
                    mThumb1 = rangesDrawableAr[i];
                    break;
                }
            } else {
                mThumb1 = rangesDrawableAr[i];
                break;
            }

        }
        int thumbHalfheight = mThumb1.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumb1.getIntrinsicWidth() / 2;
        mThumb1.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
                thumbHalfheight);
    }

    private float withThreshold(float value) {
        return value + 135f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int min = Math.min(width, height);
        float top;
        float left;
        int arcDiameter;

        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);

        arcDiameter = min - getPaddingLeft();
        mArcRadius = arcDiameter / 2;
        top = (float) height / 2 - ((float) arcDiameter / 2);
        left = (float) width / 2 - ((float) arcDiameter / 2);
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        if (moveMarker) {
            int arcStart = (int) mProgressSweep + mStartAngle + getArcRotation() + 90;
            mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
            mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));
        }
        if (moveMarker2) {
            int arcStart1 = (int) mProgressSweep2 + mStartAngle + getArcRotation() + 90;
            mThumbX1Pos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart1)));
            mThumbY1Pos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart1)));
        }

        if (mThumb != null) {
            setTouchInSide(mTouchInside);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }

    private void onStartTrackingTouch() {
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onStopTrackingTouch(this);
        }
    }

    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch) {
            return;
        }
        setPressed(true);
        double mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        int progress = getProgressForAngle(mTouchAngle);
        onProgressRefresh(progress, true);
    }

    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = false;
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mTouchIgnoreRadius) {
            ignore = true;
        }
        return ignore;
    }

    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        //invert the x-coord if we are rotating anti-clockwise
        x = (mClockwise) ? x : -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2)
                - Math.toRadians(getArcRotation()));
        if (angle < 0) {
            angle = 360 + angle;
        }
        angle -= mStartAngle;
        return angle;
    }

    private int getProgressForAngle(double angle) {
        int touchProgress = (int) Math.round(valuePerDegree() * angle);

        touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        return touchProgress;
    }

    private float valuePerDegree() {
        return (float) mMax / getSweepAngle();
    }

    private void onProgressRefresh(int progress, boolean fromUser) {
        updateProgress(progress, fromUser, false);
    }

    private void updateThumbPosition() {
        int thumbAngle = (int) (mStartAngle + mProgressSweep + getArcRotation() + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void updateThumbPosition1() {
        int thumbAngle = (int) (mStartAngle + mProgressSweep2 + getArcRotation() + 90);
        mThumbX1Pos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbY1Pos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void updateProgress2(int progress, boolean moveMarker2) {
        this.moveMarker2 = moveMarker2;
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }
        mProgressSweep2 = (float) progress / mMax * getSweepAngle();
        updateThumbPosition1();
        invalidate();
    }

    private void updateProgress(int progress, boolean fromUser, boolean moveMarker) {
        this.moveMarker = moveMarker;
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        progress = (progress > mMax) ? mMax : progress;
        progress = (progress < 0) ? 0 : progress;
        mProgress = progress;

        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener
                    .onProgressChanged(this, progress, fromUser);
        }

        mProgressSweep = (float) progress / mMax * getSweepAngle();

        updateThumbPosition();
        invalidate();
    }

    private boolean moveMarker = false;
    private boolean moveMarker2 = false;

    public void setProgress(int progress, boolean moveMarker, boolean moveMarker2) {
        this.moveMarker = moveMarker;
        this.moveMarker2 = moveMarker2;
        if (moveMarker || moveMarker2) {
            if (moveMarker) {
                updateProgress(progress, false, moveMarker);
            } else {
                updateProgress2(progress, true);
            }
        } else {
            invalidate();
        }

    }

    public void setProgressWidth(int mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        if (progressPaint.length >= 1) progressPaint[0].setStrokeWidth(mProgressWidth);
        if (progressPaint.length >= 2) progressPaint[1].setStrokeWidth(mProgressWidth);
        if (progressPaint.length >= 3) progressPaint[2].setStrokeWidth(mProgressWidth);
        if (progressPaint.length >= 4) progressPaint[3].setStrokeWidth(mProgressWidth);
    }

    public void setArcWidth(int mArcWidth) {
        this.mArcWidth = mArcWidth;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    public int getArcRotation() {
        return mRotation + 15;
    }

    public int getSweepAngle() {
        return mSweepAngle - 9;
    }

    public void setTouchInSide(boolean isEnabled) {
        int thumbHalfheight = mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside) {
            mTouchIgnoreRadius = (float) mArcRadius / 4;
        } else {
            // Don't use the exact radius makes interaction too tricky
            mTouchIgnoreRadius = mArcRadius
                    - Math.min(thumbHalfWidth, thumbHalfheight);
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }
}