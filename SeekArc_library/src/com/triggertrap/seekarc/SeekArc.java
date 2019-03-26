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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
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
    private static int INVALID_PROGRESS_VALUE = -1;

    /**
     * The Drawable for the seek arc thumbnail
     */
    private Drawable mThumb;

    /**
     * The Current value that the SeekArc is set to
     */
    private int mProgress = 0;
    private int mProgress1 = 0;

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
    private Drawable[] rangesDrawableAr = new Drawable[]{getResources().getDrawable(R.drawable.seek_arc_red_dot),
            getResources().getDrawable(R.drawable.seek_arc_blue_dot),
            getResources().getDrawable(R.drawable.seek_arc_green_dot),
            getResources().getDrawable(R.drawable.seek_arc_orange_dot)};
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private int mThumbX1Pos;
    private int mThumbY1Pos;

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
        if (rangesColorAr.length > 1) {
            progressPaint[1] = setupPaint(1);
        }
        if (rangesColorAr.length > 2) {
            progressPaint[2] = setupPaint(2);
        }
        if (rangesColorAr.length > 3) {
            progressPaint[3] = setupPaint(3);
        }
    }

    public void setRangesDrawableAr(Drawable[] rangesDrawableAr) {
        this.rangesDrawableAr = rangesDrawableAr;
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
            mProgress = a.getInteger(R.styleable.SeekArc_progress, mProgress);
            mProgress1 = a.getInteger(R.styleable.SeekArc_progress1, mProgress1);
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

        mSweepAngle = (getSweepAngle() > 360) ? 360 : getSweepAngle();
        mSweepAngle = (getSweepAngle() < 0) ? 0 : getSweepAngle();


        mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
        mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);
        //mArcPaint.setAlpha(45);
        progressPaint[0] = setupPaint(0);
        if (rangesColorAr.length > 1) {
            progressPaint[1] = setupPaint(1);
        }
        if (rangesColorAr.length > 2) {
            progressPaint[2] = setupPaint(2);
        }
        if (rangesColorAr.length > 3) {
            progressPaint[3] = setupPaint(3);
        }
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
        pointerThreshold += 2;
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
                if (mProgressSweep2 <= seekBarRangesAr[i + 1]) {
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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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

    private void updateProgress2(int progress) {
        this.moveMarker2 = true;
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        int mMax1 = 148;
        progress = (progress > mMax1) ? mMax1 : progress;
        int mMin1 = 0;
        progress = (progress < mMin1) ? mMin1 : progress;
        mProgress1 = progress;

        mProgressSweep2 = (float) progress / mMax1 * getSweepAngle();
        updateThumbPosition1();
        invalidate();
    }

    private void updateProgress(int progress, boolean moveMarker) {
        this.moveMarker = moveMarker;
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        int mMax = 148;
        progress = (progress > mMax) ? mMax : progress;
        int mMin = 0;
        progress = (progress < mMin) ? mMin : progress;
        mProgress = progress;

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
                updateProgress(progress, moveMarker);
            } else {
                updateProgress2(progress);
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

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }
}