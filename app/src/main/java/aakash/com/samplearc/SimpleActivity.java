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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.triggertrap.seekarc.ArcHelper;
import com.triggertrap.seekarc.SeekArc;

/**
 * SimpleActivity.java
 *
 * @author Neil Davies
 */
public class SimpleActivity extends Activity {

    private SeekArc mSeekArc;
    private int gaugeMax = 148;
    private int[] gaugeRange;
    /**
     * This values will be come from api
     */
    private float originalRanges[] = new float[]{60, 80};
    private int originalMin = 40;
    private int originalMax = 100;

    private float originalRanges2[] = new float[]{90, 120};
    private int originalMin2 = 70;
    private int originalMax2 = 170;
    private int[] rangesColorAr;

    /**
     * Here we will provide drawable icon for particular range.
     */
    private Drawable[] rangesDrawableAr;

    protected int getLayoutFile() {
        return R.layout.holo_sample;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutFile());
        rangesColorAr = new int[]{getResources().getColor(R.color.dot_color_red),
                getResources().getColor(R.color.dot_color_blue),
                getResources().getColor(R.color.dot_color_green),
                getResources().getColor(R.color.dot_color_orange)};
        rangesDrawableAr = new Drawable[]{getResources().getDrawable(R.drawable.red_dot),
                getResources().getDrawable(R.drawable.blue_dot),
                getResources().getDrawable(R.drawable.green_dot),
                getResources().getDrawable(R.drawable.orange_dot)};
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

        gaugeRange = new int[originalRanges.length + 2];
        gaugeRange[0] = 0;
        for (int i = 1; i <= originalRanges.length; i++) {
            gaugeRange[i] = i * (gaugeMax / (originalRanges.length + 1));
        }
        gaugeRange[originalRanges.length + 1] = gaugeMax;

        mSeekArc.setRangesAr(originalRanges.length + 1);

        btnAnimate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = Integer.parseInt(((EditText) findViewById(R.id.etMarker)).getText().toString());
                String progress2Text = ((EditText) findViewById(R.id.etMarker2)).getText().toString();
                if (progress2Text.equals("")) {
                    ArcHelper.getSingleMarkerGauge(originalMax, originalMin, originalRanges,
                            rangesColorAr, rangesDrawableAr, progress)
                            .setArcPointer(mSeekArc)
                            .setContext(SimpleActivity.this)
                            .startAnimation();
                } else {
                    int progress2 = Integer.parseInt(progress2Text);
                    ArcHelper.getTwoMarkerGuage(originalMax, originalMin, originalMax2, originalMin2, originalRanges, originalRanges2,
                            rangesColorAr, rangesDrawableAr, progress, progress2)
                            .setArcPointer(mSeekArc)
                            .setContext(SimpleActivity.this)
                            .startAnimation();
                }
            }
        });
    }
}
