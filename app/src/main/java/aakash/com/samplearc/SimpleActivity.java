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
    private int gaugeMax = 148;
    private int[] gaugeRange;
    /**
     * This values will be come from api
     */
    private int originalRanges[] = new int[]{30, 70};
    private int originalMin = 10;
    private int originalMax = 190;

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
                int rangeMin = originalMin, rangeMax = originalMax;
                int gaugeRangeMin = 0, gaugeRangeMax = gaugeMax;

                int[] gaugeRange2 = new int[gaugeRange.length + 2];
                int[] originalRanges2 = new int[originalRanges.length + 2];

                gaugeRange2[0] = gaugeRangeMin;
                gaugeRange2[gaugeRange2.length - 1] = gaugeRangeMax;
                System.arraycopy(gaugeRange, 0, gaugeRange2, 1, gaugeRange2.length - 2);

                originalRanges2[0] = rangeMin;
                originalRanges2[originalRanges2.length - 1] = rangeMax;
                System.arraycopy(originalRanges, 0, originalRanges2, 1, originalRanges2.length - 2);
                for (int i = 0; i < originalRanges2.length - 1; i++) {
                    if (progress >= originalRanges2[i] && progress <= originalRanges2[i + 1]) {
                        rangeMin = originalRanges2[i];
                        rangeMax = originalRanges2[i + 1];
                        gaugeRangeMin = gaugeRange2[i];
                        gaugeRangeMax = gaugeRange2[i + 1];
                    }
                }
                int percentage, gaugeProgress;
                if (progress == rangeMin) {
                    gaugeProgress = gaugeRangeMin;
                } else {
                    percentage = ((progress - rangeMin) * 100 / (rangeMax - rangeMin));
                    gaugeProgress = (((gaugeRangeMax - gaugeRangeMin) * percentage) / 100) + gaugeRangeMin;
                }

                startAnimation(gaugeProgress);
            }
        });
    }

    private void startAnimation(final float marker) {
        mSeekArc.resetPointerThreshold();
        animationPos = 0;
        notchPosition = 0;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (animationPos < gaugeMax) {
                    mSeekArc.setProgress(animationPos += animationSkipItem, false);
                    handler.postDelayed(runnable, animationDelay);
                } else if (notchPosition < marker) {
                    mSeekArc.setProgress(notchPosition += animationSkipItem, true);
                    handler.postDelayed(runnable, animationDelay);
                }
            }
        };
        handler.postDelayed(runnable, animationDelay);
    }
}
