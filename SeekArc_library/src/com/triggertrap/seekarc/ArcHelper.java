package com.triggertrap.seekarc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class ArcHelper {

    private static ArcHelper getInstance() {
        return new ArcHelper();
    }

    private ArcHelper() {
    }

    public static final int TYPE_DEFAULT_GAUGE = 0;
    public static final int TYPE_TWO_MARKER_GAUGE = 4;

    private static final int GAUGE_ANIMATION_DELAY = 20;
    private static final int GAUGE_TEXT_FADE_DELAY = 10;
    private static final int NOTCH_COUNT = 33;
    private static final int RADIUS = 170;
    public static final int MAX_GAUGE = 148;
    private Context context;
    private SeekArc arcPointer;
    private int totalRangeMin;
    private int totalRangeMax;
    private int gaugeType;
    private int notchReading = 0;
    private Runnable runnable;
    private Handler handler;
    private int maxNotchReading;
    private View centerView;
    private float[] rangeList;
    private int[] colorList;

    // TODO required fields
    private Drawable[] rangesDrawableAr;
    private SeekArc mSeekArc;
    /**
     * This values will be come from api
     */
    private int gaugeMax = 148;
    private int[] gaugeRange;

    public void startAnimation() {
        mSeekArc = this.arcPointer;
        mSeekArc.setRangesColorAr(colorList);
        mSeekArc.setRangesDrawableAr(rangesDrawableAr);
        int value = context.getResources().getDisplayMetrics().widthPixels / 3;

        ViewGroup.LayoutParams params = mSeekArc.getLayoutParams();
        params.height = value;
        params.width = value;
        mSeekArc.setLayoutParams(params);
        gaugeRange = new int[rangeList.length];
        for (int i = 0; i < rangeList.length; i++) {
            gaugeRange[i] = (i + 1) * (gaugeMax / (rangeList.length + 1));
        }

        mSeekArc.setRangesAr(rangeList.length + 1);
        float rangeMin = totalRangeMin, rangeMax = totalRangeMax;
        int gaugeRangeMin = 0, gaugeRangeMax = gaugeMax;

        int[] gaugeRange2 = new int[gaugeRange.length + 2];
        float[] originalRanges2 = new float[rangeList.length + 2];

        gaugeRange2[0] = gaugeRangeMin;
        gaugeRange2[gaugeRange2.length - 1] = gaugeRangeMax;
        System.arraycopy(gaugeRange, 0, gaugeRange2, 1, gaugeRange2.length - 2);

        originalRanges2[0] = rangeMin;
        originalRanges2[originalRanges2.length - 1] = rangeMax;
        System.arraycopy(rangeList, 0, originalRanges2, 1, originalRanges2.length - 2);
        for (int i = 0; i < originalRanges2.length - 1; i++) {
            if (maxNotchReading >= originalRanges2[i] && maxNotchReading <= originalRanges2[i + 1]) {
                rangeMin = originalRanges2[i];
                rangeMax = originalRanges2[i + 1];
                gaugeRangeMin = gaugeRange2[i];
                gaugeRangeMax = gaugeRange2[i + 1];
            }
        }
        float percentage, gaugeProgress;
        if (maxNotchReading == rangeMin) {
            gaugeProgress = gaugeRangeMin;
        } else {
            percentage = ((maxNotchReading - rangeMin) * 100 / (rangeMax - rangeMin));
            gaugeProgress = (((gaugeRangeMax - gaugeRangeMin) * percentage) / 100) + gaugeRangeMin;
        }

        startAnimation(gaugeProgress);
    }

    private int animationDelay = 1;
    private int animationSkipItem = 4;
    private int animationPos = 0;
    private int notchPosition = 0;

    private void startAnimation(final float marker) {
        mSeekArc.resetPointerThreshold();
        animationPos = 0;
        notchPosition = 0;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (animationPos < gaugeMax) {
                    mSeekArc.setProgress(animationPos += animationSkipItem, false, false);
                    handler.postDelayed(runnable, animationDelay);
                } else if (notchPosition < marker) {
                    mSeekArc.setProgress(notchPosition += animationSkipItem, true, false );
                    handler.postDelayed(runnable, animationDelay);
                }
            }
        };
        handler.postDelayed(runnable, animationDelay);

        if (context != null) {
            Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            aniFade.setDuration(GAUGE_ANIMATION_DELAY * maxNotchReading);
            if (centerView != null) {
                centerView.startAnimation(aniFade);
            }
        }
    }

    public static ArcHelper getTwoMarkerGuage(int max, int min,
                                              float[] parameterRange,
                                              int[] colorRange,
                                              Drawable[] rangesDrawableAr,
                                              int notchReading) {
        return ArcHelper.getInstance()
                .setTotalRangeMax(max)
                .setTotalRangeMin(min)
                .setRangeList(parameterRange)
                .setNotchReading(notchReading)
                .setRangesDrawableAr(rangesDrawableAr)
                .setColorList(colorRange)
                .setGaugeType(TYPE_TWO_MARKER_GAUGE);
    }

    public static ArcHelper getSingleMarkerGauge(int max, int min,
                                                 float[] parameterRange,
                                                 Drawable[] rangesDrawableAr,
                                                 int[] colorRange, int notchReading) {
        return ArcHelper.getInstance()
                .setTotalRangeMax(max)
                .setTotalRangeMin(min)
                .setRangeList(parameterRange)
                .setColorList(colorRange)
                .setRangesDrawableAr(rangesDrawableAr)
                .setNotchReading(notchReading)
                .setGaugeType(TYPE_DEFAULT_GAUGE);
    }

    public Runnable getRunnable() {
        return this.runnable;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public ArcHelper setRangesDrawableAr(Drawable[] rangesDrawableAr) {
        this.rangesDrawableAr = rangesDrawableAr;
        return this;
    }

    public ArcHelper setCenterView(View centerView) {
        this.centerView = centerView;
        return this;
    }

    public ArcHelper setContext(Context context) {
        this.context = context;
        return this;
    }

    public ArcHelper setArcPointer(SeekArc arcPointer) {
        this.arcPointer = arcPointer;
        return this;
    }

    public ArcHelper setTotalRangeMin(int totalRangeMin) {
        this.totalRangeMin = totalRangeMin;
        return this;
    }

    public ArcHelper setTotalRangeMax(int totalRangeMax) {
        this.totalRangeMax = totalRangeMax;
        return this;
    }

    public ArcHelper setGaugeType(int gaugeType) {
        this.gaugeType = gaugeType;
        return this;
    }

    public ArcHelper setNotchReading(int maxNotchReading) {
        this.maxNotchReading = maxNotchReading;
        return this;
    }

    public ArcHelper setRangeList(float[] rangeList) {
        this.rangeList = rangeList;
        return this;
    }

    public ArcHelper setColorList(int[] colorList) {
        this.colorList = colorList;
        return this;
    }

    public int getTotalRangeMin() {
        return totalRangeMin;
    }

    public int getTotalRangeMax() {
        return totalRangeMax;
    }

    public int getNotchReading() {
        return notchReading;
    }


    private float getValuePos(float min, float max, float value) {
        return ((value - min) * 100) / (max - min);
    }

    private float[] getGaugeMeterRange(float min, float max, float rangeAr[]) {
        float range = max - min;
        float[] outputRange = new float[rangeAr.length];
        for (int i = 0; i < rangeAr.length; i++) {
            float outPercent = (rangeAr[i] * 100) / range;
            outputRange[i] = (NOTCH_COUNT * outPercent) / 100;
        }
        return outputRange;
    }
}
