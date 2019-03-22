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

    private static final int TYPE_DEFAULT_GAUGE = 0;
    public static final int TYPE_TWO_MARKER_GAUGE = 4;

    private static final int GAUGE_ANIMATION_DELAY = 20;
    private static final int NOTCH_COUNT = 33;
    private static final int MAX_GAUGE = 148;
    private Context context;
    private SeekArc arcPointer;
    private int totalRangeMin;
    private int totalRangeMax;

    private int gaugeType;
    private int notchReading = 0;
    private Runnable runnable;
    private Handler handler = new Handler();
    private int maxNotchReading;
    private View centerView;
    private float[] rangeList;
    private int[] colorList;

    private int totalRangeMin2;
    private int totalRangeMax2;
    private int maxNotchReading2;
    private float[] rangeList2;

    // TODO required fields
    private Drawable[] rangesDrawableAr;
    private SeekArc mSeekArc;

    private int animationDelay = 1;
    private int animationSkipItem = 4;
    private int animationPos = 0;
    private int notchPosition = 0, notchPosition1 = 0;
    private int gaugeRangeMin = 0, gaugeRangeMax = MAX_GAUGE;
    private boolean marker1Progress = false;

    /**
     * This values will be come from api
     */
    private int[] gaugeRange;

    public void startAnimation() {

        /* Setup Seek Arc class params. */
        mSeekArc = this.arcPointer;
        mSeekArc.setRangesColorAr(colorList);
        mSeekArc.setRangesDrawableAr(rangesDrawableAr);
        int value = context.getResources().getDisplayMetrics().widthPixels / 3;

        ViewGroup.LayoutParams params = mSeekArc.getLayoutParams();
        params.height = value;
        params.width = value;
        mSeekArc.setLayoutParams(params);
        gaugeRange = new int[rangeList.length + 2];
        gaugeRange[0] = 0;
        for (int i = 1; i <= rangeList.length; i++) {
            gaugeRange[i] = i * (MAX_GAUGE / (rangeList.length + 1));
        }
        gaugeRange[rangeList.length + 1] = MAX_GAUGE;

        mSeekArc.setRangesAr(rangeList.length + 1);

        if (gaugeType == TYPE_DEFAULT_GAUGE) {
            startOneMarkerAnimation(maxNotchReading, totalRangeMin, totalRangeMax, rangeList);
        } else if (gaugeType == TYPE_TWO_MARKER_GAUGE) {
            startTwoMarkersAnimation(maxNotchReading, maxNotchReading2,
                    totalRangeMin, totalRangeMax,
                    totalRangeMin2, totalRangeMax2,
                    rangeList, rangeList2);

        }
    }

    private void animateCenterView() {
        if (context != null) {
            Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            aniFade.setDuration(GAUGE_ANIMATION_DELAY * ((MAX_GAUGE + maxNotchReading + maxNotchReading2)/ 4));
            if (centerView != null) {
                centerView.startAnimation(aniFade);
            }
        }
    }

    private void startOneMarkerAnimation(final float progress,
                                         float originalMin, float originalMax,
                                         float[] originalRanges) {
        mSeekArc.resetPointerThreshold();
        animationPos = 0;
        notchPosition = 0;
        final float gaugeProgress = createMarker(progress, originalMin, originalMax, originalRanges);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (animationPos < MAX_GAUGE) {
                    mSeekArc.setProgress(animationPos += animationSkipItem, false, false);
                    handler.postDelayed(runnable, animationDelay);
                } else if (notchPosition < gaugeProgress) {
                    mSeekArc.setProgress(notchPosition += animationSkipItem, true, false);
                    handler.postDelayed(runnable, animationDelay);
                }
            }
        };
        handler.postDelayed(runnable, animationDelay);
        animateCenterView();
    }

    private void startTwoMarkersAnimation(final float marker,
                                          final float marker2,
                                          float originalMin, float originalMax,
                                          float originalMin2, float originalMax2,
                                          float[] originalRanges, float[] originalRanges2) {
        final float gaugeProgress = createMarker(marker, originalMin, originalMax, originalRanges);
        final float gaugeProgress2 = createMarker(marker2, originalMin2, originalMax2, originalRanges2);
        mSeekArc.resetPointerThreshold();
        animationPos = 0;
        notchPosition = 0;
        notchPosition1 = 0;
        mSeekArc.setProgress(MAX_GAUGE, false, false);
        mSeekArc.setProgress((int) gaugeProgress, true, false);
        mSeekArc.setProgress((int) gaugeProgress2, false, true);
        animateCenterView();
    }

    private float createMarker(final float progress,
                               float originalMin,
                               float originalMax,
                               float[] originalRanges) {
        float[] markerRange = new float[originalRanges.length + 2];
        markerRange[0] = originalMin;
        markerRange[markerRange.length - 1] = originalMax;
        System.arraycopy(originalRanges, 0, markerRange, 1, markerRange.length - 2);

        if (originalRanges.length > 0) {
            for (int i = 0; i < markerRange.length - 1; i++) {
                if (progress >= markerRange[i] && progress <= markerRange[i + 1]) {
                    originalMin = markerRange[i];
                    originalMax = markerRange[i + 1];
                    gaugeRangeMin = gaugeRange[i];
                    gaugeRangeMax = gaugeRange[i + 1];
                }
            }
        }
        final float percentage, gaugeProgress;
        if (progress == originalMin) {
            gaugeProgress = gaugeRangeMin;
        } else {
            percentage = ((progress - originalMin) * 100 / (originalMax - originalMin));
            gaugeProgress = (((gaugeRangeMax - gaugeRangeMin) * percentage) / 100) + gaugeRangeMin;
        }
        return gaugeProgress;
    }

    public static ArcHelper getTwoMarkerGuage(int max, int min,
                                              int max2, int min2,
                                              float[] parameterRange,
                                              float[] parameterRange2,
                                              int[] colorRange,
                                              Drawable[] rangesDrawableAr,
                                              int notchReading, int notchReading2) {
        return ArcHelper.getInstance()
                .setTotalRangeMax(max)
                .setTotalRangeMax2(max2)
                .setTotalRangeMin(min)
                .setTotalRangeMin2(min2)
                .setRangeList(parameterRange)
                .setRangeList2(parameterRange2)
                .setNotchReading(notchReading)
                .setNotchReading2(notchReading2)
                .setRangesDrawableAr(rangesDrawableAr)
                .setColorList(colorRange)
                .setGaugeType(TYPE_TWO_MARKER_GAUGE);
    }

    public static ArcHelper getSingleMarkerGauge(int max, int min,
                                                 float[] parameterRange,
                                                 int[] colorRange,
                                                 Drawable[] rangesDrawableAr,
                                                 int notchReading) {
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


    public ArcHelper setTotalRangeMin2(int totalRangeMin2) {
        this.totalRangeMin2 = totalRangeMin2;
        return this;
    }

    public ArcHelper setTotalRangeMax2(int totalRangeMax2) {
        this.totalRangeMax2 = totalRangeMax2;
        return this;
    }

    public ArcHelper setNotchReading2(int maxNotchReading2) {
        this.maxNotchReading2 = maxNotchReading2;
        return this;
    }

    public ArcHelper setRangeList2(float[] rangeList2) {
        this.rangeList2 = rangeList2;
        return this;
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
}
