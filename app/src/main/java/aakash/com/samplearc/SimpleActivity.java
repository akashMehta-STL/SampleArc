/*
  *****************************************************************************
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
package aakash.com.samplearc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.triggertrap.seekarc.SeekArc;

/**
 * SimpleActivity.java
 *
 * @author Neil Davies
 */
public class SimpleActivity extends Activity {

    private SeekArc mSeekArc;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int animationDelay = 1;
    private int animationSkipItem = 4;

    private int animationPos = 0;
    private int notchPosition = 0;
    private int notchPosition1 = 0;
    private int gaugeMax = 148;
    private int[] gaugeRange;
    /**
     * This values will be come from api
     */
    private int originalRanges[] = new int[]{30, 70};
    private int originalMin = 10;
    private int originalMax = 190;

    private int originalRanges2[] = new int[] {309, 409};
    private int originalMin2 = 300;
    private int originalMax2 = 400;

    protected int getLayoutFile() {
        return R.layout.holo_sample;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutFile());

        mSeekArc = findViewById(R.id.seekArc);
        mSeekArc.setRangesColorAr(new int[]{
                getResources().getColor(R.color.dot_color_green),
                getResources().getColor(R.color.dot_color_orange),
                getResources().getColor(R.color.dot_color_red),
                getResources().getColor(R.color.dot_color_blue)
        });
        mSeekArc.setRangesDrawableAr(new Drawable[]{
                getResources().getDrawable(R.drawable.green_dot),
                getResources().getDrawable(R.drawable.orange_dot),
                getResources().getDrawable(R.drawable.red_dot),
                getResources().getDrawable(R.drawable.blue_dot)
        });
        int value = getResources().getDisplayMetrics().widthPixels / 3;

        ViewGroup.LayoutParams params = mSeekArc.getLayoutParams();
        params.height = value;
        params.width = value;
        mSeekArc.setLayoutParams(params);
        Button btnAnimate = findViewById(R.id.btnAnimate);
        gaugeRange = new int[originalRanges.length];
        for (int i = 0; i < originalRanges.length; i++) {
            gaugeRange[i] = (i + 1) * (gaugeMax / (originalRanges.length + 1));
        }

        mSeekArc.setRangesAr(originalRanges.length + 1);

        btnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = Integer.parseInt(((EditText) findViewById(R.id.etMarker)).getText().toString());
                int gaugeRangeMin = 0, gaugeRangeMax = gaugeMax;
                int[] gaugeRange2 = new int[gaugeRange.length + 2];
                gaugeRange2[0] = gaugeRangeMin;
                gaugeRange2[gaugeRange2.length - 1] = gaugeRangeMax;
                System.arraycopy(gaugeRange, 0, gaugeRange2, 1, gaugeRange2.length - 2);

                int marker1Min = originalMin, marker1Max = originalMax;
                int[] marker1Range = new int[originalRanges.length + 2];
                marker1Range[0] = marker1Min;
                marker1Range[marker1Range.length - 1] = marker1Max;
                System.arraycopy(originalRanges, 0, marker1Range, 1, marker1Range.length - 2);

                for (int i = 0; i < marker1Range.length - 1; i++) {
                    if (progress >= marker1Range[i] && progress <= marker1Range[i + 1]) {
                        marker1Min = marker1Range[i];
                        marker1Max = marker1Range[i + 1];
                        gaugeRangeMin = gaugeRange2[i];
                        gaugeRangeMax = gaugeRange2[i + 1];
                    }
                }
                int percentage, gaugeProgress;
                if (progress == marker1Min) {
                    gaugeProgress = gaugeRangeMin;
                } else {
                    percentage = ((progress - marker1Min) * 100 / (marker1Max - marker1Min));
                    gaugeProgress = (((gaugeRangeMax - gaugeRangeMin) * percentage) / 100) + gaugeRangeMin;
                }
//============================================================================================================

                int marker2Min = originalMin2, marker2Max = originalMax2;
                int[] marker2Range = new int[originalRanges2.length + 2];
                marker2Range[0] = marker2Min;
                marker2Range[marker2Range.length - 1] = marker2Max;
                System.arraycopy(originalRanges2, 0, marker2Range, 1, marker2Range.length - 2);

                int progress2 = Integer.parseInt(((EditText) findViewById(R.id.etMarker2)).getText().toString());
                for (int i = 0; i < marker1Range.length - 1; i++) {
                    if (progress2 >= marker2Range[i] && progress2 <= marker2Range[i + 1]) {
                        marker2Min = marker2Range[i];
                        marker2Max = marker2Range[i + 1];
                        gaugeRangeMin = gaugeRange2[i];
                        gaugeRangeMax = gaugeRange2[i + 1];
                    }
                }
                int percentage2, gaugeProgress2;
                if (progress2 == marker1Min) {
                    gaugeProgress2 = gaugeRangeMin;
                } else {
                    percentage2 = ((progress2 - marker2Min) * 100 / (marker2Max - marker2Min));
                    gaugeProgress2 = (((gaugeRangeMax - gaugeRangeMin) * percentage2) / 100) + gaugeRangeMin;
                }
//============================================================================================================

                startAnimation(gaugeProgress, gaugeProgress2);
            }
        });
    }

    private boolean marker1Progress = false;

    private void startAnimation(final float marker, final float marker2) {
        mSeekArc.resetPointerThreshold();
        animationPos = 0;
        notchPosition = 0;
        notchPosition1 = 0;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (animationPos < gaugeMax) {
                    mSeekArc.setProgress(animationPos += animationSkipItem, false, false);
                    handler.postDelayed(runnable, animationDelay);
                } else if (notchPosition < marker && !marker1Progress) {
                    mSeekArc.setProgress(notchPosition += animationSkipItem, true, false);
                    handler.postDelayed(runnable, animationDelay);
                } else {
                    marker1Progress = true;
                    if (notchPosition1 < marker2) {
                        mSeekArc.setProgress(notchPosition1 += animationSkipItem, false, true);
                        handler.postDelayed(runnable, animationDelay);
                    }
                }
            }
        };
        handler.postDelayed(runnable, animationDelay);
    }
}
