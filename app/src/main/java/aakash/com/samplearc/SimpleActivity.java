/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Triggertrap Ltd
 * Author Neil Davies
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package aakash.com.samplearc;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
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
    private int max = 148;
    private int marker = 140;

    protected int getLayoutFile() {
        return R.layout.holo_sample;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutFile());

        mSeekArc = (SeekArc) findViewById(R.id.seekArc);
        mSeekArc.setRangesAr(new float[]{60, 150, 180});
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
        startAnimation();
    }

    private void startAnimation() {
        mSeekArc.resetPointerThreshold();
        animationPos = 0;
        notchPosition = 0;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (animationPos < max) {
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
