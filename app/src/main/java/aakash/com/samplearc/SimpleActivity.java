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
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

/**
 * 
 * SimpleActivity.java
 * @author Neil Davies
 * 
 */
public class SimpleActivity extends Activity {

	private SeekArc mSeekArc;

	protected int getLayoutFile(){
		return R.layout.holo_sample;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutFile());

		mSeekArc = (SeekArc) findViewById(R.id.seekArc);
		mSeekArc.setProgress(273);
		mSeekArc.setRangesAr(new float[]{60, 150});
		mSeekArc.setRangesColorAr(new int[] {
				getResources().getColor(R.color.dot_color_green),
				getResources().getColor(R.color.dot_color_orange),
				getResources().getColor(R.color.dot_color_red),
				getResources().getColor(R.color.dot_color_blue)
		});
		mSeekArc.setRangesDrawableAr(new Drawable[] {
				getResources().getDrawable(R.drawable.green_dot),
				getResources().getDrawable(R.drawable.orange_dot),
				getResources().getDrawable(R.drawable.red_dot),
				getResources().getDrawable(R.drawable.blue_dot)
		});
		int value = getResources().getDisplayMetrics().widthPixels/3;

		ViewGroup.LayoutParams params = mSeekArc.getLayoutParams();
		params.height = value;
		params.width = value;
		mSeekArc.setLayoutParams(params);
	}
}
